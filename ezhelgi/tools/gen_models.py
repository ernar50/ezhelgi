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
        for name, parent, pivot, rot, cubes in self.parts:
            builder = "CubeListBuilder.create()"
            for c in cubes:
                cx, cy, cz, w, h, d, ck = c[:7]
                extra = c[7] if len(c) > 7 else {}
                u, v = uvs[i]
                i += 1
                builder += ".texOffs(%d, %d).addBox(%s, %s, %s, %s, %s, %s)" % (
                    u, v, f(cx), f(cy), f(cz), f(w), f(h), f(d))
                W, H, D = math.ceil(w), math.ceil(h), math.ceil(d)
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
                            col = tuple(max(0, min(255, int(ch * (shade * grad + n)))) for ch in base)
                            px[fx + xx, fy + yy] = col + (255,)
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
    "coat": (236, 228, 208), "mane": (214, 160, 52), "hoof": (72, 58, 46),
    "wing": (250, 247, 236), "gold": (226, 178, 60), "eye": (30, 25, 25), "dark": (190, 180, 160),
}, 22)
tul.part("body", "root", (0, 8, 0), cubes=[(-5, -5, -11, 10, 10, 22, "coat")])
tul.part("neck", "root", (0, 5, -9), (0.55, 0, 0), cubes=[(-2, -11, -3, 4, 12, 6, "coat"),
                                                         (-1, -12, 3, 2, 13, 2, "mane")])
tul.part("head", "neck", (0, -10, 0), cubes=[(-3, -4, -7, 6, 5, 8, "coat", {"front": [(0, 1, "eye"), (5, 1, "eye")]}),
                                             (-2.5, -3, -11, 5, 4, 4, "dark"),
                                             (-2.5, -6, -1, 1, 2, 1, "mane"),
                                             (1.5, -6, -1, 1, 2, 1, "mane")])
tul.part("tail", "root", (0, 4, 11), (0.5, 0, 0), cubes=[(-1.5, 0, -1.5, 3, 13, 3, "mane")])
for leg, (x, z) in {"leg_fl": (-3, -8), "leg_fr": (3, -8), "leg_bl": (-3, 8), "leg_br": (3, 8)}.items():
    tul.part(leg, "root", (x, 13, z), cubes=[(-2, 0, -2, 4, 9, 4, "coat"), (-2.5, 9, -2.5, 5, 2, 5, "hoof")])
tul.part("wing_l", "root", (5, 4, -6), cubes=[(0, -1, 0, 16, 1, 12, "wing", {"tip": "gold"})])
tul.part("wing_r", "root", (-5, 4, -6), cubes=[(-16, -1, 0, 16, 1, 12, "wing", {"tip": "gold"})])
tul.write_java("TulparModel", """
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
        this.wing_r.zRot = -wing;""", "Модель Тұлпара (крылатый конь).")

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
print("models done")
