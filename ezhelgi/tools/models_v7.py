# v7 models (executed at the end of gen_models.py, shares its globals: Model, os, math)

def _speckle(dark, light=None, every=5):
    def paint(face, x, y, w, h):
        if face == "bottom" and light:
            return light
        if (x * 3 + y * 5) % every == 0:
            return dark
        return None
    return paint


QUAD_ANIM = """
        this.head.yRot = headYaw;
        this.head.xRot += headPitch;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;"""


def legs(m, y, xs, zs, w, h, col, paint=None, hoof=None):
    for leg, (x, z) in {"leg_fl": (-xs, -zs), "leg_fr": (xs, -zs), "leg_bl": (-xs, zs), "leg_br": (xs, zs)}.items():
        cubes = [(-w / 2, 0, -w / 2, w, h - (1 if hoof else 0), w, col, {"paint": paint} if paint else {})]
        if hoof:
            cubes.append((-w / 2, h - 1, -w / 2, w, 1, w, hoof))
        m.part(leg, "root", (x, y, z), cubes=cubes)


# ---------------------------------------------------------------- Арқар
ark = Model("arqar", 128, 64, {
    "coat": (150, 112, 78), "dark": (104, 74, 50), "white": (238, 232, 220), "ruff": (220, 210, 196),
    "horn": (200, 186, 150), "horn_d": (150, 136, 104), "hoof": (50, 40, 32), "eye": (30, 20, 10), "nose": (60, 50, 44),
}, 701)


def ark_coat(face, x, y, w, h):
    if face == "bottom" or (face in ("left", "right") and y >= h - 2):
        return "white"
    if face == "back":
        return "white"
    if face in ("left", "right") and y == h - 3:
        return "dark"
    return None


def horn_ridge(face, x, y, w, h):
    return "horn_d" if (x + y) % 2 == 0 else None


ark.part("body", "root", (0, 12, 0), cubes=[(-4, -4, -8, 8, 8, 16, "coat", {"paint": ark_coat}),
                                          (-4.5, -4.5, -8.5, 9, 6, 4, "ruff")])
ark.part("head", "root", (0, 8, -8), (0.25, 0, 0), cubes=[
    (-2.5, -4, -5, 5, 5, 6, "coat", {"paint": lambda f, x, y, w, h: "eye" if f in ("left", "right") and y == 1 and x == 1 else None}),
    (-1.6, -2, -7.6, 3.2, 3, 2.8, "white", {"paint": lambda f, x, y, w, h: "nose" if f == "front" and y == 0 else None})])
for side, s in (("l", 1), ("r", -1)):
    ark.part("horn_%s" % side, "head", (2.2 * s, -3.5, -1), (-0.5, 0.5 * s, 0.25 * s),
             cubes=[(-1.4, -1.4, 0, 2.8, 2.8, 5, "horn", {"paint": horn_ridge})])
    ark.part("horn_%s2" % side, "horn_%s" % side, (0, 0, 5), (1.2, 0, 0),
             cubes=[(-1.2, -1.2, 0, 2.4, 2.4, 5, "horn", {"paint": horn_ridge})])
    ark.part("horn_%s3" % side, "horn_%s2" % side, (0, 0, 5), (1.3, 0, 0),
             cubes=[(-1, -1, 0, 2, 2, 4, "horn", {"paint": horn_ridge})])
ark.part("tail", "root", (0, 9, 8), (0.5, 0, 0), cubes=[(-1, 0, 0, 2, 3, 1, "dark")])
legs(ark, 16, 2.6, 6, 2.6, 8, "coat", hoof="hoof")
ark.write_java("ArqarModel", QUAD_ANIM, "Модель архара с огромными закрученными рогами.")

# ---------------------------------------------------------------- Қарақұйрық (джейран)
jey = Model("qaraquiryq", 64, 64, {
    "coat": (214, 172, 112), "white": (246, 240, 228), "stripe": (140, 98, 60), "black": (30, 26, 24),
    "horn": (40, 34, 30), "eye": (20, 14, 10),
}, 702)


def jey_coat(face, x, y, w, h):
    if face == "bottom" or (face in ("left", "right") and y >= h - 1):
        return "white"
    if face in ("left", "right") and y == h - 2:
        return "stripe"
    if face == "back":
        return "white"
    return None


jey.part("body", "root", (0, 12, 0), cubes=[(-2.5, -3, -6, 5, 5, 12, "coat", {"paint": jey_coat})])
jey.part("neck", "root", (0, 10, -5), (0.6, 0, 0), cubes=[(-1.2, -5, -1.5, 2.4, 6, 2.6, "coat")])
jey.part("head", "neck", (0, -5, 0), (-0.6, 0, 0), cubes=[
    (-1.5, -2.5, -4, 3, 3, 4.5, "coat", {"paint": lambda f, x, y, w, h: "eye" if f in ("left", "right") and y == 0 and x == 1 else ("white" if f == "front" else None)}),
    (-1.0, -1.5, -5.4, 2, 2, 1.6, "white"), (-1.8, -3.5, -0.5, 0.8, 1.5, 0.6, "coat"), (1.0, -3.5, -0.5, 0.8, 1.5, 0.6, "coat")])
jey.part("horns", "head", (0, -2.5, -1.5), cubes=[(-1.2, -5, 0, 0.6, 5, 0.6, "horn"), (0.6, -5, 0, 0.6, 5, 0.6, "horn")])
jey.part("tail", "root", (0, 10, 6), (0.5, 0, 0), cubes=[(-0.5, 0, 0, 1, 3, 1, "black")])
legs(jey, 14, 1.5, 4.5, 1.4, 10, "coat", hoof="black")
jey.write_java("QaraquiryqModel", QUAD_ANIM + """
        this.horns.visible = state.male;""", "Модель джейрана (қарақұйрық) — стройной газели.")

# ---------------------------------------------------------------- Тау-теке
teke = Model("tau_teke", 128, 64, {
    "coat": (140, 112, 84), "dark": (70, 52, 38), "light": (200, 184, 160), "horn": (110, 96, 76), "horn_d": (70, 60, 48),
    "eye": (210, 160, 60), "hoof": (40, 32, 26),
}, 703)
teke.part("body", "root", (0, 13, 0), cubes=[(-3.5, -3.5, -7, 7, 7, 14, "coat", {"paint": _speckle("dark", "light", 7)})])
teke.part("head", "root", (0, 9, -7), (0.35, 0, 0), cubes=[
    (-2, -3.5, -5, 4, 4, 5, "coat", {"paint": lambda f, x, y, w, h: "eye" if f in ("left", "right") and y == 1 and x == 1 else None}),
    (-1.5, -2, -6.8, 3, 2.5, 2, "dark"), (-1, 0.5, -5, 2, 4, 1.5, "dark")])
for side, s in (("l", 1), ("r", -1)):
    teke.part("horn_%s" % side, "head", (1.2 * s, -3.5, -2.5), (-0.9, 0.15 * s, 0.1 * s),
              cubes=[(-0.9, -0.9, 0, 1.8, 1.8, 6, "horn", {"paint": horn_ridge})])
    teke.part("horn_%s2" % side, "horn_%s" % side, (0, 0, 6), (0.55, 0, 0),
              cubes=[(-0.75, -0.75, 0, 1.5, 1.5, 6, "horn", {"paint": horn_ridge})])
teke.part("tail", "root", (0, 10, 7), (0.7, 0, 0), cubes=[(-0.8, 0, 0, 1.6, 2.5, 1, "dark")])
legs(teke, 16.5, 2.2, 5, 2.2, 7.5, "coat", hoof="hoof")
teke.write_java("TauTekeModel", QUAD_ANIM, "Модель тау-теке (горного козла) с саблевидными рогами.")

# ---------------------------------------------------------------- Тянь-шаньский аю
ayu = Model("ayu", 128, 64, {
    "fur": (176, 142, 100), "fur_d": (130, 100, 70), "fur_l": (204, 176, 134), "snout": (150, 120, 90),
    "nose": (30, 24, 20), "eye": (20, 16, 12), "claw": (226, 214, 190),
}, 704)


def fur(face, x, y, w, h):
    if (x * 5 + y * 3) % 6 == 0:
        return "fur_d"
    if (x * 7 + y) % 9 == 0:
        return "fur_l"
    return None


ayu.part("body", "root", (0, 10, 0), cubes=[(-6, -5, -10, 12, 11, 20, "fur", {"paint": fur}),
                                          (-5, -7, -9, 10, 3, 7, "fur", {"paint": fur})])
ayu.part("head", "root", (0, 7, -10), cubes=[
    (-4, -4, -6, 8, 8, 7, "fur", {"paint": lambda f, x, y, w, h: "eye" if f == "front" and y == 2 and x in (1, w - 2) else None}),
    (-2.5, -0.5, -9, 5, 4, 3.5, "snout", {"paint": lambda f, x, y, w, h: "nose" if f == "front" and y == 0 and 1 <= x <= 3 else None}),
    (-4.5, -5.5, -2.5, 2.5, 2.5, 1.5, "fur_d"), (2, -5.5, -2.5, 2.5, 2.5, 1.5, "fur_d")])
for leg, (x, z) in {"leg_fl": (-3.5, -7), "leg_fr": (3.5, -7), "leg_bl": (-3.5, 7), "leg_br": (3.5, 7)}.items():
    ayu.part(leg, "root", (x, 15, z), cubes=[(-2.25, 0, -2.25, 4.5, 9, 4.5, "fur_d", {"paint": fur}),
                                            (-2.25, 8.5, -3.0, 4.5, 0.5, 1, "claw")])
ayu.write_java("AyuModel", QUAD_ANIM + """
        if (state.sitting) {
            this.body.xRot = -0.9F;
            this.body.y += 4.0F;
            this.head.y -= 6.0F;
            this.head.z += 4.0F;
            this.leg_fl.xRot = -1.2F;
            this.leg_fr.xRot = -1.2F;
            this.leg_fl.y -= 7.0F;
            this.leg_fr.y -= 7.0F;
            this.leg_fl.z += 4.0F;
            this.leg_fr.z += 4.0F;
        }""", "Модель тянь-шаньского бурого медведя (светлого, с длинными когтями).")

# ---------------------------------------------------------------- Манул
man = Model("manul", 64, 64, {
    "fur": (150, 140, 126), "fur_d": (110, 100, 90), "rust": (176, 142, 110), "white": (230, 226, 216),
    "black": (34, 30, 28), "eye": (230, 200, 70), "pupil": (20, 20, 20),
}, 705)


def man_fur(face, x, y, w, h):
    if face == "bottom":
        return "rust"
    if face in ("left", "right") and x % 3 == 0 and y < h - 1:
        return "fur_d"
    if (x * 7 + y * 3) % 5 == 0:
        return "white"
    return None


def man_face(face, x, y, w, h):
    if face == "front":
        if y == 2 and x in (1, w - 2):
            return "eye"
        if y == 2 and x in (2, w - 3):
            return "pupil"
        if y == 4 and x in (0, 1, w - 1, w - 2):
            return "black"
        if y >= 3 and 2 <= x <= w - 3:
            return "white"
    if face == "top" and y in (1, 2) and x % 2 == 0:
        return "black"
    return None


man.part("body", "root", (0, 18, 0), cubes=[(-3.5, -3.5, -5, 7, 6.5, 10, "fur", {"paint": man_fur})])
man.part("head", "root", (0, 16, -5), cubes=[(-3.5, -3, -3.5, 7, 5, 4, "fur", {"paint": man_face}),
                                            (-4.2, -1.5, -1.8, 1, 1.5, 1.2, "fur_d"), (3.2, -1.5, -1.8, 1, 1.5, 1.2, "fur_d")])
man.part("tail", "root", (0, 17, 5), (-0.6, 0, 0), cubes=[(-1.5, -1.5, 0, 3, 3, 8, "fur",
                                                           {"paint": lambda f, x, y, w, h: "black" if y % 3 == 0 or y >= h - 2 else None})])
legs(man, 21, 2, 3.5, 2.2, 3, "fur_d")
man.write_java("ManulModel", QUAD_ANIM + """
        this.tail.yRot = Mth.sin(age * 0.07F) * 0.3F;
        if (state.sitting) {
            this.body.y += 1.5F;
            this.leg_bl.visible = false;
            this.leg_br.visible = false;
        } else {
            this.leg_bl.visible = true;
            this.leg_br.visible = true;
        }""", "Модель манула — пушистого степного кота.")

# ---------------------------------------------------------------- Қарсақ (корсак)
kor = Model("qarsaq", 64, 32, {
    "fur": (196, 168, 130), "fur_d": (150, 124, 92), "white": (240, 236, 226), "black": (36, 30, 26), "eye": (40, 26, 10),
}, 706)
kor.part("body", "root", (0, 18, 0), cubes=[(-2, -2, -5, 4, 4, 9, "fur", {"paint": lambda f, x, y, w, h: "white" if f == "bottom" else None})])
kor.part("head", "root", (0, 16.5, -5), cubes=[
    (-2.5, -2, -3, 5, 4, 3, "fur", {"paint": lambda f, x, y, w, h: "eye" if f == "front" and y == 1 and x in (1, 3) else ("white" if f == "front" and y == 3 else None)}),
    (-1, 0, -5, 2, 2, 2, "white", {"paint": lambda f, x, y, w, h: "black" if f == "front" and y == 0 else None}),
    (-2.5, -4.5, -1.5, 1.5, 2.5, 1, "fur_d"), (1, -4.5, -1.5, 1.5, 2.5, 1, "fur_d")])
kor.part("tail", "root", (0, 17, 4), (-0.9, 0, 0), cubes=[(-1.5, -1.5, 0, 3, 3, 8, "fur",
                                                           {"paint": lambda f, x, y, w, h: "black" if y >= h - 2 else None})])
legs(kor, 20, 1.3, 3.5, 1.2, 4, "fur_d")
kor.write_java("QarsaqModel", QUAD_ANIM + """
        this.tail.yRot = Mth.sin(age * 0.15F) * 0.2F;""", "Модель корсака — маленькой степной лисы.")

# ---------------------------------------------------------------- Итбалық (каспийский тюлень)
seal = Model("itbalyq", 64, 64, {
    "skin": (128, 132, 138), "dark": (78, 82, 90), "light": (182, 186, 190), "eye": (12, 12, 14), "nose": (40, 40, 44),
}, 707)
seal.part("body", "root", (0, 21, 0), cubes=[(-3.5, -3, -6, 7, 6, 12, "skin", {"paint": _speckle("dark", "light", 6)}),
                                           (-2.5, -2.5, 6, 5, 5, 4, "skin", {"paint": _speckle("dark", "light", 6)})])
seal.part("head", "root", (0, 20, -6), cubes=[(-3, -3, -4, 6, 5, 4.5, "skin", {"paint": lambda f, x, y, w, h: "eye" if f == "front" and y == 1 and x in (1, 4) else None}),
                                            (-1.5, -1, -5.5, 3, 2.5, 1.5, "light", {"paint": lambda f, x, y, w, h: "nose" if f == "front" and y == 0 else None})])
seal.part("flipper_l", "root", (3.5, 23, -3), cubes=[(0, -0.5, -1, 3, 1, 3, "dark")])
seal.part("flipper_r", "root", (-3.5, 23, -3), cubes=[(-3, -0.5, -1, 3, 1, 3, "dark")])
seal.part("tail", "root", (0, 22, 10), cubes=[(-3, -0.5, 0, 6, 1, 3, "dark")])
seal.write_java("ItbalyqModel", """
        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        float wave = Mth.sin(limbPos * 0.6F) * limbSpeed;
        this.flipper_l.zRot = -0.3F + wave * 0.6F;
        this.flipper_r.zRot = 0.3F - wave * 0.6F;
        this.tail.xRot = Mth.sin(age * 0.2F) * 0.15F + wave * 0.4F;""", "Модель каспийского тюленя (итбалық).")

# ---------------------------------------------------------------- Қосаяқ (тушканчик)
jer = Model("qosayaq", 64, 32, {
    "fur": (216, 186, 140), "white": (246, 242, 234), "black": (30, 26, 24), "ear": (230, 190, 170), "eye": (14, 10, 8),
}, 708)
jer.part("body", "root", (0, 20, 0), (-0.5, 0, 0), cubes=[(-1.5, -2, -2, 3, 3.5, 4, "fur", {"paint": lambda f, x, y, w, h: "white" if f in ("bottom", "front") else None})])
jer.part("head", "root", (0, 18, -2), cubes=[(-1.5, -2, -2.5, 3, 2.5, 3, "fur", {"paint": lambda f, x, y, w, h: "eye" if f in ("left", "right") and y == 0 else None}),
                                           (-1.3, -5, -0.5, 1, 3, 0.4, "ear"), (0.3, -5, -0.5, 1, 3, 0.4, "ear")])
jer.part("leg_l", "root", (1, 21, 1), cubes=[(-0.5, 0, -0.5, 1, 3, 1, "fur"), (-0.5, 2.5, -2.5, 1, 0.5, 3, "white")])
jer.part("leg_r", "root", (-1, 21, 1), cubes=[(-0.5, 0, -0.5, 1, 3, 1, "fur"), (-0.5, 2.5, -2.5, 1, 0.5, 3, "white")])
jer.part("tail", "root", (0, 21, 2), (-0.3, 0, 0), cubes=[(-0.3, -0.3, 0, 0.6, 0.6, 8, "fur"),
                                                         (-0.8, -0.8, 7, 1.6, 1.6, 2.5, "black", {"paint": lambda f, x, y, w, h: "white" if y == h - 1 else None})])
jer.write_java("QosayaqModel", """
        this.head.yRot = headYaw;
        float hop = Mth.sin(limbPos * 0.9F) * limbSpeed;
        this.leg_l.xRot = hop;
        this.leg_r.xRot = hop;
        this.tail.xRot = -0.3F + hop * 0.3F;""", "Модель тушканчика (қосаяқ) с длинным хвостом-кисточкой.")

# ---------------------------------------------------------------- Пржевальский жылқысы
prz = Model("przewalski", 128, 128, {
    "coat": (196, 152, 96), "coat_d": (160, 118, 70), "belly": (226, 204, 160), "muzzle": (236, 222, 196),
    "mane": (52, 40, 30), "legs": (60, 46, 34), "hoof": (30, 24, 20), "eye": (20, 14, 10), "stripe": (90, 64, 40),
    "leather": (120, 72, 36), "red": (172, 34, 30), "gold": (238, 186, 56),
}, 709)


def prz_coat(face, x, y, w, h):
    if face == "bottom":
        return "belly"
    if face == "top" and abs(x - (w - 1) / 2) < 1:
        return "stripe"
    return None


prz.part("body", "root", (0, 9, 0), cubes=[(-5, -5, -11, 10, 10, 21, "coat", {"paint": prz_coat})])
prz.part("neck", "root", (0, 6, -9), (0.55, 0, 0), cubes=[(-2, -10, -3, 4, 11, 6, "coat"),
                                                         (-1, -12, 2, 2, 12, 2, "mane")])
prz.part("head", "neck", (0, -9, 0), cubes=[(-3, -4, -7, 6, 5, 8, "coat", {"paint": lambda f, x, y, w, h: "eye" if f in ("left", "right") and y == 1 and x == w - 3 else None}),
                                           (-2.5, -3, -11, 5, 4, 4, "muzzle"), (-2.5, -6, -1, 1, 2, 1, "coat"), (1.5, -6, -1, 1, 2, 1, "coat")])
prz.part("tail", "root", (0, 5, 10), (0.5, 0, 0), cubes=[(-1.5, 0, -1.5, 3, 12, 3, "mane")])
for leg, (x, z) in {"leg_fl": (-3, -8), "leg_fr": (3, -8), "leg_bl": (-3, 7), "leg_br": (3, 7)}.items():
    prz.part(leg, "root", (x, 13, z), cubes=[(-2, 0, -2, 4, 10, 4, "legs"), (-2, 10, -2, 4, 1, 4, "hoof")])
prz.part("saddle", "root", (0, 9, 0), cubes=[(-5.5, -6, -5, 11, 1, 10, "red"), (-3, -7, -3, 6, 1, 6, "leather"),
                                           (5, -5, -4, 1, 5, 8, "red"), (-6, -5, -4, 1, 5, 8, "red"), (-1, -8, -3, 2, 1, 1, "gold")])
prz.write_java("PrzewalskiModel", """
        this.saddle.visible = state.saddled;
        this.neck.yRot = headYaw * 0.6F;
        this.neck.xRot += headPitch * 0.4F;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.1F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.xRot += Mth.cos(age * 0.12F) * 0.08F + limbSpeed * 0.5F;""", "Модель лошади Пржевальского — мышастой, со стоячей гривой.")

# ---------------------------------------------------------------- Қарақұс (стервятник)
vul = Model("qaraqus", 64, 64, {
    "feather": (62, 46, 36), "dark": (36, 26, 20), "ruff": (226, 214, 196), "head": (196, 160, 150),
    "beak": (70, 62, 56), "beak_l": (200, 180, 150), "eye": (20, 14, 10), "leg": (170, 160, 150),
}, 710)
vul.part("body", "root", (0, 17, 0), (-0.3, 0, 0), cubes=[(-3.5, -3.5, -4, 7, 7, 9, "feather", {"paint": _speckle("dark", None, 6)})])
vul.part("ruff", "root", (0, 13.5, -3), cubes=[(-3, -1.5, -2, 6, 3, 3, "ruff")])
vul.part("neck", "root", (0, 13, -4), (0.6, 0, 0), cubes=[(-1, -4, -1, 2, 4, 2, "head")])
vul.part("head", "neck", (0, -4, 0), (-0.6, 0, 0), cubes=[(-1.5, -2.5, -2.5, 3, 3, 3, "head", {"paint": lambda f, x, y, w, h: "eye" if f in ("left", "right") and y == 0 and x == 1 else None}),
                                                        (-0.75, -1.5, -4.5, 1.5, 2, 2, "beak", {"paint": lambda f, x, y, w, h: "beak_l" if y == 0 else None})])
vul.part("wing_l", "root", (3.5, 14, -3), cubes=[(0, 0, 0, 1, 9, 11, "feather", {"paint": lambda f, x, y, w, h: "dark" if y >= h - 3 or x % 3 == 0 else None})])
vul.part("wing_r", "root", (-3.5, 14, -3), cubes=[(-1, 0, 0, 1, 9, 11, "feather", {"paint": lambda f, x, y, w, h: "dark" if y >= h - 3 or x % 3 == 0 else None})])
vul.part("tail", "root", (0, 20, 4.5), (-0.4, 0, 0), cubes=[(-2.5, 0, 0, 5, 1, 5, "dark")])
vul.part("leg_l", "root", (1.5, 21, 0), cubes=[(-0.5, 0, -0.5, 1, 3, 1, "leg")])
vul.part("leg_r", "root", (-1.5, 21, 0), cubes=[(-0.5, 0, -0.5, 1, 3, 1, "leg")])
vul.write_java("QaraqusModel", """
        this.head.yRot = headYaw;
        if (state.flying) {
            float flap = Mth.sin(age * 0.35F) * 0.4F;
            this.wing_l.zRot = -1.55F + flap;
            this.wing_r.zRot = 1.55F - flap;
            this.leg_l.xRot = 1.1F;
            this.leg_r.xRot = 1.1F;
        } else {
            float step = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
            this.leg_l.xRot = step;
            this.leg_r.xRot = -step;
            this.wing_l.zRot = -0.05F;
            this.wing_r.zRot = 0.05F;
        }""", "Модель стервятника қарақұс.")

# ---------------------------------------------------------------- Айдаһар
dr = Model("aidahar", 256, 256, {
    "scale": (40, 120, 70), "scale_d": (24, 80, 46), "scale_l": (90, 170, 100), "belly": (226, 190, 90),
    "mane": (196, 40, 30), "mane_d": (130, 20, 20), "horn": (236, 220, 170), "eye": (255, 220, 60),
    "pupil": (20, 10, 10), "tooth": (250, 248, 240), "mouth": (120, 20, 24), "claw": (230, 214, 170),
}, 711)


def dscale(face, x, y, w, h):
    if face == "bottom":
        return "belly" if x % 3 else "scale_d"
    if face in ("left", "right") and y >= h - 2:
        return "belly"
    if (x + (y // 2) * 2) % 4 == 0 and y % 2 == 0:
        return "scale_d"
    if (x * 3 + y) % 7 == 0:
        return "scale_l"
    return None


def dhead(face, x, y, w, h):
    if face in ("left", "right") and y == 2 and x in (w - 4, w - 3):
        return "eye" if x == w - 4 else "pupil"
    return dscale(face, x, y, w, h)


dr.part("head", "root", (0, 8, -6), cubes=[
    (-5, -5, -10, 10, 7, 10, "scale", {"paint": dhead}),
    (-4, -2, -17, 8, 3, 7, "scale", {"paint": dscale}),
    (-1.5, -3, -17, 1, 1, 1, "scale_d"), (0.5, -3, -17, 1, 1, 1, "scale_d")])
dr.part("jaw", "head", (0, 2, -9), cubes=[(-3.5, 0, -8, 7, 2, 8, "scale", {"paint": lambda f, x, y, w, h: "tooth" if f == "top" and x % 2 == 0 and y < 2 else ("mouth" if f == "top" else None)})])
for side, s in (("l", 1), ("r", -1)):
    dr.part("horn_%s" % side, "head", (3.5 * s, -5, -2), (0.8, 0.3 * s, 0), cubes=[(-0.75, -0.75, 0, 1.5, 1.5, 9, "horn")])
    dr.part("whisker_%s" % side, "head", (3 * s, 0, -15), (0.3, 0.5 * s, 0), cubes=[(-0.3, -0.3, 0, 0.6, 0.6, 10, "mane")])
dr.part("mane", "head", (0, -5, 0), (0.3, 0, 0), cubes=[(-5.5, -3, -2, 11, 4, 7, "mane", {"paint": lambda f, x, y, w, h: "mane_d" if x % 3 == 0 else None})])
prev = "root"
sizes = [8, 8, 8, 7.5, 7, 6.5, 6, 5, 4, 3, 2]
pivot = (0, 8, 0)
for i, sz in enumerate(sizes):
    name = "seg%d" % i
    dr.part(name, prev, pivot if i == 0 else (0, 0, 8), cubes=[
        (-sz / 2, -sz / 2, 0, sz, sz, 8, "scale", {"paint": dscale}),
        (-0.5, -sz / 2 - 2.5, 2, 1, 2.5, 3, "mane", {"paint": lambda f, x, y, w, h: "mane_d" if y > 1 else None})])
    if i in (1, 6):
        for side, s in (("l", 1), ("r", -1)):
            dr.part("leg%d%s" % (i, side), name, (sz / 2 * s, sz / 2 - 1, 4), (0.3, 0, -0.5 * s),
                    cubes=[(-1.25, 0, -1.25, 2.5, 6, 2.5, "scale", {"paint": dscale}),
                           (-1.5, 5.5, -2.5, 3, 1, 3, "claw")])
    prev = name
dr.part("tailfin", prev, (0, 0, 8), cubes=[(-0.5, -4, 0, 1, 8, 6, "mane", {"paint": lambda f, x, y, w, h: "mane_d" if y % 2 else None})])
seg_anim = "\n".join(
    "        this.seg%d.yRot = Mth.sin(age * 0.18F - %d * 0.7F) * 0.22F;\n"
    "        this.seg%d.xRot = Mth.cos(age * 0.14F - %d * 0.6F) * 0.12F;" % (i, i, i, i) for i in range(len(sizes)))
dr.write_java("AidaharModel", """
        this.head.yRot = headYaw * 0.5F;
        this.head.xRot = headPitch * 0.5F;
        this.jaw.xRot = 0.15F + Mth.sin(age * 0.1F) * 0.12F + (state.attacking ? 0.6F : 0.0F);
""" + seg_anim + """
        this.leg1l.xRot = 0.3F + Mth.sin(age * 0.2F) * 0.3F;
        this.leg1r.xRot = 0.3F - Mth.sin(age * 0.2F) * 0.3F;
        this.leg6l.xRot = 0.3F - Mth.sin(age * 0.2F) * 0.3F;
        this.leg6r.xRot = 0.3F + Mth.sin(age * 0.2F) * 0.3F;""", "Модель Айдаһара — змееподобного дракона с гривой и рогами.")

V7_MODELS = (ark, jey, teke, ayu, man, kor, seal, jer, prz, vul, dr)
