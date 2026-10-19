"""Generates entity model Java classes and matching textures for the Ezhelgi mod.

Each model is described as a list of parts with cubes; the script packs the
cube UV nets onto a texture, paints them and writes the Java createBodyLayer().
Run: python3 tools/gen_models.py  (from the project root)
"""
import math
import os
import random
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA_DIR = os.path.join(ROOT, "src/main/java/kz/ezhelgi/client")
TEX_DIR = os.path.join(ROOT, "src/main/resources/assets/ezhelgi/textures/entity")


def f(v):
    s = ("%.4f" % v).rstrip("0").rstrip(".")
    if s in ("-0", ""):
        s = "0"
    return s + "F"


class Model:
    def __init__(self, name, tex_w, tex_h, colors, seed):
        self.name = name
        self.tw, self.th = tex_w, tex_h
        self.colors = colors
        self.parts = []  # (name, parent, pivot, rot, cubes)
        self.rng = random.Random(seed)

    def part(self, name, parent, pivot, rot=(0, 0, 0), cubes=()):
        self.parts.append((name, parent, pivot, rot, list(cubes)))

    def pack(self):
        # simple shelf packing of every cube net
        x = y = shelf = 0
        placed = []
        for p in self.parts:
            for c in p[4]:
                _, _, _, w, h, d = c[:6]
                nw, nh = 2 * (math.ceil(d) + math.ceil(w)), math.ceil(d) + math.ceil(h)
                if x + nw > self.tw:
                    x, y, shelf = 0, y + shelf, 0
                if y + nh > self.th:
                    raise SystemExit("texture too small for " + self.name)
                placed.append((x, y))
                x += nw
                shelf = max(shelf, nh)
        return placed

    def render(self):
        uvs = self.pack()
        img = Image.new("RGBA", (self.tw, self.th), (0, 0, 0, 0))
        px = img.load()
        rng = self.rng
        i = 0
        java_parts = []
        self.preview_parts = []
        for name, parent, pivot, rot, cubes in self.parts:
            builder = "CubeListBuilder.create()"
            pcubes = []
            for c in cubes:
                cx, cy, cz, w, h, d, ck = c[:7]
                extra = c[7] if len(c) > 7 else {}
                u, v = uvs[i]
                i += 1
                builder += ".texOffs(%d, %d).addBox(%s, %s, %s, %s, %s, %s)" % (
                    u, v, f(cx), f(cy), f(cz), f(w), f(h), f(d))
                W, H, D = math.ceil(w), math.ceil(h), math.ceil(d)
                pcubes.append((cx, cy, cz, w, h, d, u, v))
                base = self.colors[ck]
                faces = {
                    "top": (u + D, v, W, D, 1.12),
                    "bottom": (u + D + W, v, W, D, 0.78),
                    "right": (u, v + D, D, H, 0.92),
                    "front": (u + D, v + D, W, H, 1.0),
                    "left": (u + D + W, v + D, D, H, 0.92),
                    "back": (u + 2 * D + W, v + D, W, H, 0.88),
                }
                for fname, (fx, fy, fw, fh, shade) in faces.items():
                    for yy in range(fh):
                        for xx in range(fw):
                            grad = 1.0
                            if fname in ("front", "back", "left", "right") and fh > 2:
                                grad = 1.06 - 0.14 * (yy / (fh - 1))
                            n = rng.uniform(-0.07, 0.07)
                            src = base
                            painter = extra.get("paint")
                            if painter is not None:
                                custom = painter(fname, xx, yy, fw, fh)
                                if custom is not None:
                                    src = self.colors[custom] if isinstance(custom, str) else custom
                            col = tuple(max(0, min(255, int(ch * (shade * grad + n)))) for ch in src[:3])
                            alpha = src[3] if len(src) > 3 else 255
                            px[fx + xx, fy + yy] = col + (alpha,)
                # details painted on the front face
                fx, fy, fw, fh, _ = faces["front"]
                for (dx, dy, colkey) in extra.get("front", []):
                    if 0 <= dx < fw and 0 <= dy < fh:
                        px[fx + dx, fy + dy] = self.colors[colkey] + (255,)
                # accent stripe on top face
                if "tip" in extra:
                    tx, ty, tw_, td, _ = faces["top"]
                    tipc = self.colors[extra["tip"]]
                    for yy in range(td):
                        for xx in range(tw_):
                            if xx >= tw_ - 3:
                                px[tx + xx, ty + yy] = tipc + (255,)
                    bx, by, bw, bd, _ = faces["bottom"]
                    for yy in range(bd):
                        for xx in range(bw):
                            if xx <= 2:
                                px[bx + xx, by + yy] = tipc + (255,)
            if any(rot):
                pose = "PartPose.offsetAndRotation(%s, %s, %s, %s, %s, %s)" % (
                    f(pivot[0]), f(pivot[1]), f(pivot[2]), f(rot[0]), f(rot[1]), f(rot[2]))
            else:
                pose = "PartPose.offset(%s, %s, %s)" % (f(pivot[0]), f(pivot[1]), f(pivot[2]))
            java_parts.append((name, parent, builder, pose))
            self.preview_parts.append((name, parent, pivot, rot, pcubes))
        os.makedirs(TEX_DIR, exist_ok=True)
        img.save(os.path.join(TEX_DIR, self.name.lower() + ".png"))
        return java_parts

    def write_java(self, cls, anim_code, doc):
        parts = self.render()
        fields = "\n".join("    private final ModelPart %s;" % p[0] for p in parts)
        lookups = []
        for name, parent, _, _ in parts:
            src = "root" if parent == "root" else parent
            lookups.append("        this.%s = %s.getChild(\"%s\");" % (name, src, name))
        defs = []
        for name, parent, builder, pose in parts:
            src = "root" if parent == "root" else "p_" + parent
            defs.append("        PartDefinition p_%s = %s.addOrReplaceChild(\"%s\", %s, %s);" % (
                name, src, name, builder, pose))
        code = f"""package kz.ezhelgi.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * {doc}
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class {cls} extends EntityModel<EzhelgiRenderState> {{
{fields}

    public {cls}(ModelPart root) {{
        super(root);
{chr(10).join(lookups)}
    }}

    public static LayerDefinition createBodyLayer() {{
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
{chr(10).join(defs)}
        return LayerDefinition.create(mesh, {self.tw}, {self.th});
    }}

    @Override
    public void setupAnim(EzhelgiRenderState state) {{
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float limbPos = state.walkAnimationPos;
        float limbSpeed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;
        float headYaw = Mth.clamp(state.yRot, -40.0F, 40.0F) * ((float) Math.PI / 180.0F);
        float headPitch = Mth.clamp(state.xRot, -30.0F, 40.0F) * ((float) Math.PI / 180.0F);
{anim_code}
    }}
}}
"""
        with open(os.path.join(JAVA_DIR, cls + ".java"), "w", encoding="utf-8") as fh:
            fh.write(code)


# ---------------------------------------------------------------- Көкбөрі
kok = Model("kokbori", 64, 64, {
    "fur": (122, 132, 146), "dark": (78, 86, 100), "snout": (160, 164, 170),
    "eye": (235, 190, 40), "nose": (25, 25, 30), "belly": (175, 180, 186),
}, 11)
eyes = {"front": [(1, 2, "eye"), (5, 2, "eye"), (1, 3, "nose"), (5, 3, "nose")]}
kok.part("body", "root", (0, 13, 0), cubes=[(-4, -3.5, -7, 8, 7, 14, "fur"),
                                           (-4.5, -4.5, -7.5, 9, 8, 6, "dark")])
kok.part("head", "root", (0, 11.5, -7.5), cubes=[(-3.5, -3.5, -6, 7, 7, 6, "fur", eyes),
                                                (-2, 0, -9, 4, 3, 3, "snout", {"front": [(1, 0, "nose"), (2, 0, "nose")]}),
                                                (-3, -6, -3, 2, 3, 1, "dark"),
                                                (1, -6, -3, 2, 3, 1, "dark")])
kok.part("tail", "root", (0, 10.5, 7), (0.9, 0, 0), cubes=[(-1, 0, -1, 2, 9, 2, "dark")])
for leg, (x, z) in {"leg_fl": (-2.5, -5), "leg_fr": (2.5, -5), "leg_bl": (-2.5, 5), "leg_br": (2.5, 5)}.items():
    kok.part(leg, "root", (x, 16, z), cubes=[(-1.5, 0, -1.5, 3, 8, 3, "fur")])
kok.write_java("KokboriModel", """
        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.3F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.yRot = Mth.cos(age * 0.2F) * 0.15F + Mth.cos(limbPos * 0.6662F) * 0.4F * limbSpeed;
        if (state.sitting) {
            this.body.xRot = -0.45F;
            this.body.y += 2.0F;
            this.head.y += 0.5F;
            this.tail.y += 5.0F;
            this.tail.z -= 1.0F;
            this.tail.xRot = 1.5F;
            this.leg_bl.xRot = -1.5708F;
            this.leg_br.xRot = -1.5708F;
            this.leg_bl.y = 22.5F;
            this.leg_br.y = 22.5F;
            this.leg_bl.z -= 2.0F;
            this.leg_br.z -= 2.0F;
        }""", "Модель Көкбөрі (степной волк).")

# ---------------------------------------------------------------- Тұлпар
tul = Model("tulpar", 128, 128, {
    "coat": (246, 242, 232), "dapple": (222, 216, 204), "belly": (252, 250, 244),
    "mane": (232, 176, 48), "mane_dark": (186, 128, 26), "mane_light": (255, 214, 102),
    "hoof": (214, 160, 40), "hoof_dark": (140, 96, 20),
    "wing": (252, 250, 244), "wing2": (236, 230, 214), "gold": (238, 186, 56), "gold_dark": (196, 140, 30),
    "eye": (24, 40, 70), "eye_white": (250, 250, 250), "muzzle": (196, 170, 160), "nostril": (90, 64, 60),
    "leather": (120, 72, 36), "leather_dark": (84, 48, 22), "red": (172, 34, 30), "red_dark": (120, 20, 20),
}, 22)


def coat_paint(face, x, y, w, h):
    if face == "bottom":
        return "belly"
    if (x * 7 + y * 13) % 11 == 0 and face != "top":
        return "dapple"
    return None


def mane_paint(face, x, y, w, h):
    if x % 3 == 0:
        return "mane_dark"
    if x % 3 == 2 and y % 4 == 1:
        return "mane_light"
    return None


def tail_paint(face, x, y, w, h):
    return "mane_dark" if (x + y // 3) % 3 == 0 else ("mane_light" if y > h - 3 else None)


def hoof_paint(face, x, y, w, h):
    return "hoof_dark" if y == h - 1 else None


def head_paint(face, x, y, w, h):
    if face == "front":
        # тұмар: золотой ромб на лбу
        if abs(x - (w - 1) / 2) + abs(y - 1) <= 1.0:
            return "gold"
    if face in ("left", "right"):
        if y == 1 and x in (w - 2, w - 3):
            return "eye_white" if x == w - 3 else "eye"
    return None


def muzzle_paint(face, x, y, w, h):
    if face == "front" and y == h - 2 and x in (1, w - 2):
        return "nostril"
    return "muzzle" if y >= h - 2 else None


def wing_paint(face, x, y, w, h):
    # x: размах крыла (16), y: глубина (12) на верхней/нижней грани
    if face in ("top", "bottom"):
        outer = x if face == "top" else w - 1 - x
        if outer >= w - 3:
            return "gold" if (y % 2 == 0) else "gold_dark"
        if y >= h - 2 and (outer % 2 == 0):
            return "wing2"
        if outer % 3 == 0:
            return "wing2"
        return None
    return "gold" if face in ("front", "back") and x >= w - 3 else None


def wing_paint_mirror(face, x, y, w, h):
    if face in ("top", "bottom"):
        return wing_paint("bottom" if face == "top" else "top", x, y, w, h)
    if face in ("front", "back") and x <= 2:
        return "gold"
    return None


def blanket_paint(face, x, y, w, h):
    if x == 0 or y == 0 or x == w - 1 or y == h - 1:
        return "gold"
    if (x + y) % 4 == 0:
        return "gold_dark"
    return None


def flap_paint(face, x, y, w, h):
    if y == h - 1 or x == 0 or x == w - 1:
        return "gold"
    if (x - y) % 3 == 0:
        return "red_dark"
    return None


tul.part("body", "root", (0, 8, 0), cubes=[(-5, -5, -11, 10, 10, 22, "coat", {"paint": coat_paint})])
tul.part("neck", "root", (0, 5, -9), (0.55, 0, 0), cubes=[(-2, -11, -3, 4, 12, 6, "coat", {"paint": coat_paint}),
                                                         (-1, -12, 3, 2, 13, 2, "mane", {"paint": mane_paint})])
tul.part("head", "neck", (0, -10, 0), cubes=[(-3, -4, -7, 6, 5, 8, "coat", {"paint": head_paint}),
                                             (-2.5, -3, -11, 5, 4, 4, "coat", {"paint": muzzle_paint}),
                                             (-2.5, -6, -1, 1, 2, 1, "coat"),
                                             (1.5, -6, -1, 1, 2, 1, "coat"),
                                             (-1, -5, -4, 2, 1, 4, "mane", {"paint": mane_paint})])
tul.part("tail", "root", (0, 4, 11), (0.5, 0, 0), cubes=[(-1.5, 0, -1.5, 3, 13, 3, "mane", {"paint": tail_paint})])
for leg, (x, z) in {"leg_fl": (-3, -8), "leg_fr": (3, -8), "leg_bl": (-3, 8), "leg_br": (3, 8)}.items():
    tul.part(leg, "root", (x, 13, z), cubes=[(-2, 0, -2, 4, 9, 4, "coat", {"paint": coat_paint}),
                                            (-2, 9, -2, 4, 2, 4, "hoof", {"paint": hoof_paint})])
tul.part("wing_l", "root", (5, 4, -6), cubes=[(0, -1, 0, 16, 1, 12, "wing", {"paint": wing_paint})])
tul.part("wing_r", "root", (-5, 4, -6), cubes=[(-16, -1, 0, 16, 1, 12, "wing", {"paint": wing_paint_mirror})])
tul.part("saddle", "root", (0, 8, 0), cubes=[
    (-5.5, -6, -5, 11, 1, 10, "red", {"paint": blanket_paint}),
    (5, -5, -4, 1, 5, 8, "red", {"paint": flap_paint}),
    (-6, -5, -4, 1, 5, 8, "red", {"paint": flap_paint}),
    (-3, -7, -3, 6, 1, 6, "leather"),
    (-3, -8, 2, 6, 1, 1, "leather_dark"),
    (-1, -8, -3, 2, 1, 1, "gold"),
    (6, -1, -1, 1, 3, 1, "gold"),
    (-7, -1, -1, 1, 3, 1, "gold")])
tul.write_java("TulparModel", """
        this.saddle.visible = state.saddled;
        this.neck.yRot = headYaw * 0.6F;
        this.neck.xRot += headPitch * 0.4F;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.1F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.xRot += Mth.cos(age * 0.12F) * 0.08F + limbSpeed * 0.5F;
        float wing;
        if (state.flying) {
            wing = -0.25F + Mth.sin(age * 0.6F) * 0.65F;
        } else {
            wing = -1.25F + Mth.sin(age * 0.08F) * 0.05F - limbSpeed * 0.25F;
        }
        this.wing_l.zRot = wing;
        this.wing_r.zRot = -wing;""", "Модель Тұлпара (крылатый конь) с седлом.")

# ---------------------------------------------------------------- Бүркіт
ber = Model("berkut", 64, 64, {
    "feather": (96, 64, 40), "dark": (58, 39, 24), "head": (196, 146, 74),
    "beak": (238, 196, 62), "eye": (20, 15, 10), "white": (236, 230, 220),
}, 33)
ber.part("body", "root", (0, 19, 0), cubes=[(-2.5, -3, -3, 5, 6, 7, "feather")])
ber.part("head", "root", (0, 16, -2.5), cubes=[(-2, -4, -2.5, 4, 4, 4, "head", {"front": [(0, 1, "eye"), (3, 1, "eye")]}),
                                               (-1, -2.5, -4.5, 2, 2, 2, "beak")])
ber.part("wing_l", "root", (2.5, 16.5, -2), cubes=[(0, 0, 0, 1, 7, 9, "dark")])
ber.part("wing_r", "root", (-2.5, 16.5, -2), cubes=[(-1, 0, 0, 1, 7, 9, "dark")])
ber.part("tail", "root", (0, 21, 4), (-0.4, 0, 0), cubes=[(-2, 0, 0, 4, 1, 5, "dark", {"tip": "white"})])
ber.part("leg_l", "root", (1.2, 22, 0.5), cubes=[(-0.5, 0, -0.5, 1, 2, 1, "beak")])
ber.part("leg_r", "root", (-1.2, 22, 0.5), cubes=[(-0.5, 0, -0.5, 1, 2, 1, "beak")])
ber.write_java("BerkutModel", """
        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        if (state.flying) {
            float flap = Mth.sin(age * 0.9F) * 0.55F;
            this.wing_l.zRot = -1.45F + flap;
            this.wing_r.zRot = 1.45F - flap;
            this.wing_l.xRot = 0.15F;
            this.wing_r.xRot = 0.15F;
            this.leg_l.xRot = 0.9F;
            this.leg_r.xRot = 0.9F;
            this.tail.xRot = -0.1F;
        } else {
            float step = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
            this.leg_l.xRot = step;
            this.leg_r.xRot = -step;
            this.wing_l.zRot = -0.08F;
            this.wing_r.zRot = 0.08F;
        }""", "Модель Бүркіта (беркут).")

# ---------------------------------------------------------------- Арба (телега)
arba = Model("arba", 128, 128, {
    "wood": (142, 100, 58), "wood_d": (104, 70, 38), "wood_l": (176, 132, 82),
    "iron": (72, 72, 76), "red": (164, 36, 30), "felt": (226, 214, 190),
}, 44)


def plank_paint(face, x, y, w, h):
    if face in ("top", "bottom"):
        return "wood_d" if x % 4 == 3 else ("wood_l" if (x * 5 + y) % 13 == 0 else None)
    return "wood_d" if y % 3 == 2 else None


def wheel_paint(face, x, y, w, h):
    if face in ("left", "right"):
        cx, cy = (w - 1) / 2, (h - 1) / 2
        r = math.hypot(x - cx, y - cy)
        if r > 6.9:
            return (0, 0, 0, 0)
        if r > 5.9:
            return "iron"
        if r > 4.9:
            return "wood_d"
        if r < 1.6:
            return "iron"
        dx, dy = x - cx, y - cy
        if abs(dx) < 0.8 or abs(dy) < 0.8 or abs(abs(dx) - abs(dy)) < 0.9:
            return "wood_l"
        return (0, 0, 0, 0)
    return (0, 0, 0, 0)


def rug_paint(face, x, y, w, h):
    if x in (0, w - 1) or y in (0, h - 1):
        return "felt"
    return "red" if (x + y) % 3 else "felt"


arba.part("bed", "root", (0, 14, 0), cubes=[
    (-8, -2, -11, 16, 2, 22, "wood", {"paint": plank_paint}),
    (-8, -6, -11, 1, 4, 22, "wood", {"paint": plank_paint}),
    (7, -6, -11, 1, 4, 22, "wood", {"paint": plank_paint}),
    (-7, -6, 10, 14, 4, 1, "wood", {"paint": plank_paint}),
    (-7, -6, -11, 14, 4, 1, "wood", {"paint": plank_paint}),
    (-6, -3, -9, 12, 1, 6, "red", {"paint": rug_paint}),
    (-6, -5, -10, 12, 2, 2, "wood_l")])
arba.part("axle", "root", (0, 17, 1), cubes=[(-9, -1, -1, 18, 2, 2, "wood_d")])
arba.part("wheel_l", "root", (9.5, 17, 1), cubes=[(-0.5, -7, -7, 1, 14, 14, "wood", {"paint": wheel_paint})])
arba.part("wheel_r", "root", (-9.5, 17, 1), cubes=[(-0.5, -7, -7, 1, 14, 14, "wood", {"paint": wheel_paint})])
arba.part("shafts", "root", (0, 14, -11), cubes=[
    (-6, -1, -22, 1, 1, 22, "wood_d"),
    (5, -1, -22, 1, 1, 22, "wood_d"),
    (-6, -1, -20, 12, 1, 1, "wood_d")])
arba.write_java("ArbaModel", """
        this.wheel_l.xRot = state.wheel;
        this.wheel_r.xRot = state.wheel;
        this.shafts.xRot = 0.12F;""", "Модель арбы — деревянной телеги с большими колёсами.")

if __name__ == "__main__" and os.environ.get("PREVIEW"):
    import preview
    out = os.environ["PREVIEW"]
    for m in (kok, tul, ber, arba):
        for yaw in (35, 215):
            preview.render(m.preview_parts, os.path.join(TEX_DIR, m.name + ".png"),
                           os.path.join(out, "%s_%d.png" % (m.name, yaw)), yaw=yaw,
                           scale=12 if m.name != "tulpar" else 8)
print("models done")
