# v8 models (executed at the end of gen_models.py, shares its globals: Model, os, math)

# ---------------------------------------------------------------- Жылан (пещерная змея)
zh = Model("zhylan", 64, 32, {
    "scale": (96, 112, 52), "scale_d": (60, 72, 30), "belly": (210, 196, 130), "eye": (230, 200, 40),
    "pupil": (20, 16, 10), "tongue": (200, 40, 50),
}, 801)


def zh_scale(face, x, y, w, h):
    if face == "bottom":
        return "belly"
    if (x + y * 2) % 4 == 0:
        return "scale_d"
    return None


zh.part("head", "root", (0, 21.5, -6), cubes=[
    (-2, -1.5, -4, 4, 3, 4, "scale", {"paint": lambda f, x, y, w, h: ("eye" if x == 0 else "pupil") if f in ("left", "right") and y == 0 and x in (0, 1) else zh_scale(f, x, y, w, h)}),
    (-0.5, 0.5, -5.5, 1, 0.1, 1.5, "tongue")])
prev = "root"
seg_sizes = [3.2, 3.0, 2.8, 2.5, 2.2, 1.8, 1.4]
for i, sz in enumerate(seg_sizes):
    zh.part("s%d" % i, prev, (0, 21.5 + (3.2 - sz) / 2, -6) if i == 0 else (0, 0, 4), cubes=[
        (-sz / 2, -sz / 2 + 0.2, 0, sz, sz * 0.8, 4, "scale", {"paint": zh_scale})])
    prev = "s%d" % i
zh_anim = "\n".join("        this.s%d.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - %d * 0.9F) * (0.35F + limbSpeed * 0.3F);" % (i, i)
                    for i in range(len(seg_sizes)))
zh.write_java("ZhylanModel", """
        this.head.yRot = headYaw * 0.5F + Mth.sin(age * 0.35F + limbPos * 0.8F + 0.9F) * 0.2F;
        this.head.xRot = headPitch * 0.3F;
""" + zh_anim, "Модель пещерной змеи.")

# ---------------------------------------------------------------- Жын (летающий демон)
jn = Model("zhyn", 64, 64, {
    "skin": (70, 60, 90), "skin_d": (40, 32, 56), "smoke": (90, 80, 110), "horn": (220, 210, 190),
    "eye": (255, 240, 120), "mouth": (20, 10, 20), "claw": (230, 220, 200), "mark": (150, 120, 200),
}, 802)


def jn_skin(face, x, y, w, h):
    if (x * 3 + y) % 6 == 0:
        return "skin_d"
    if face == "front" and (x + y) % 7 == 0:
        return "mark"
    return None


def jn_face(face, x, y, w, h):
    if face == "front":
        if y == 2 and x in (1, 2, 4, 5):
            return "eye"
        if y == 5 and 2 <= x <= 4:
            return "mouth"
    return jn_skin(face, x, y, w, h)


jn.part("body", "root", (0, 6, 0), cubes=[(-4, 0, -2.5, 8, 9, 5, "skin", {"paint": jn_skin})])
jn.part("head", "body", (0, 0, 0), cubes=[(-3.5, -7, -3.5, 7, 7, 7, "skin", {"paint": jn_face})])
for side, s in (("l", 1), ("r", -1)):
    jn.part("horn_" + side, "head", (2.5 * s, -6.5, -1), (-0.4, 0, 0.5 * s), cubes=[(-0.75, -4, -0.75, 1.5, 4, 1.5, "horn")])
    jn.part("arm_" + side, "body", (5 * s, 1, 0), cubes=[(-1.5, -1, -1.5, 3, 10, 3, "skin", {"paint": jn_skin}),
                                                       (-1.5, 9, -1.5, 3, 1.5, 3, "claw")])
jn.part("tail1", "body", (0, 9, 0), (0.2, 0, 0), cubes=[(-3, 0, -2, 6, 5, 4, "smoke", {"paint": jn_skin})])
jn.part("tail2", "tail1", (0, 5, 0), (0.25, 0, 0), cubes=[(-2, 0, -1.5, 4, 5, 3, "smoke")])
jn.part("tail3", "tail2", (0, 5, 0), (0.3, 0, 0), cubes=[(-1, 0, -1, 2, 4, 2, "smoke")])
jn.write_java("ZhynModel", """
        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        this.body.y += Mth.sin(age * 0.1F) * 0.8F;
        this.tail1.zRot = Mth.sin(age * 0.15F) * 0.2F;
        this.tail2.zRot = Mth.sin(age * 0.15F - 0.8F) * 0.3F;
        this.tail3.zRot = Mth.sin(age * 0.15F - 1.6F) * 0.4F;
        float reach = state.attacking ? -1.6F : -0.2F + Mth.sin(age * 0.08F) * 0.1F;
        this.arm_l.xRot = reach;
        this.arm_r.xRot = reach;
        this.arm_l.zRot = -0.15F;
        this.arm_r.zRot = 0.15F;""", "Модель жына — летающего демона без ног, с дымным хвостом и рогами.")

# ---------------------------------------------------------------- Шахмаран
sm = Model("shahmaran", 128, 64, {
    "skin": (214, 176, 140), "skin_d": (184, 146, 112), "scale": (120, 150, 60), "scale_d": (70, 100, 36),
    "belly": (230, 206, 120), "gold": (238, 190, 60), "gold_d": (180, 130, 30), "eye": (40, 120, 60),
    "hair": (30, 26, 20), "robe": (40, 90, 60), "gem": (60, 200, 180),
}, 803)


def sm_scale(face, x, y, w, h):
    if face == "bottom":
        return "belly"
    if (x + (y // 2) * 2) % 4 == 0 and y % 2 == 0:
        return "scale_d"
    return None


def sm_face(face, x, y, w, h):
    if face == "front":
        if y == 3 and x in (1, 2, 5, 6):
            return "eye" if x in (2, 5) else None
    if face in ("left", "right", "back") and y < 5:
        return "hair"
    return None


sm.part("body", "root", (0, 2, 0), cubes=[(-4, 0, -2, 8, 11, 4, "scale", {"paint": sm_scale}),
                                          (-4.5, 0, -2.5, 9, 3, 5, "gold", {"paint": lambda f, x, y, w, h: "gem" if f == "front" and x == 4 and y == 1 else None})])
sm.part("head", "body", (0, 0, 0), cubes=[(-4, -8, -4, 8, 8, 8, "skin", {"paint": sm_face})])
sm.part("crown", "head", (0, -8, 0), cubes=[(-4.5, -3, -4.5, 9, 3, 9, "gold", {"paint": lambda f, x, y, w, h: "gold_d" if y == 0 and x % 2 else ("gem" if f == "front" and x == 4 and y == 1 else None)})])
for side, s in (("l", 1), ("r", -1)):
    sm.part("arm_" + side, "body", (5 * s, 1, 0), cubes=[(-1.5, -1, -1.5, 3, 11, 3, "skin")])
prev = "body"
tail = [(7, 9, 0.0), (6.5, 7, 1.35), (6, 7, 0.15), (5, 7, 0.05), (4, 6, 0.05), (3, 6, 0.0), (2, 5, 0.0)]
for i, (sz, ln, rx) in enumerate(tail):
    sm.part("t%d" % i, prev, (0, 11, 0) if i == 0 else (0, 0, 0), (rx, 0, 0), cubes=[
        (-sz / 2, 0, -sz / 2, sz, ln, sz, "scale", {"paint": sm_scale})])
    # следующий сегмент начинается в конце текущего
    prev = "t%d" % i
# переносим точки поворота: сегменты идут друг за другом по длине
fixed = []
for name, parent, pivot, rot, cubes in sm.parts:
    if name.startswith("t") and name != "t0" and name[1:].isdigit():
        idx = int(name[1:])
        pivot = (0, tail[idx - 1][1], 0)
    fixed.append((name, parent, pivot, rot, cubes))
sm.parts = fixed
sm_anim = "\n".join("        this.t%d.yRot = Mth.sin(age * 0.12F - %d * 0.8F) * 0.18F;" % (i, i) for i in range(2, len(tail)))
sm.write_java("ShahmaranModel", """
        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        this.body.y += Mth.sin(age * 0.07F) * 0.5F;
        this.arm_l.xRot = state.attacking ? -1.4F : Mth.sin(age * 0.05F) * 0.1F;
        this.arm_r.xRot = state.attacking ? -1.4F : -Mth.sin(age * 0.05F) * 0.1F;
        this.arm_l.zRot = -0.1F;
        this.arm_r.zRot = 0.1F;
""" + sm_anim, "Модель Шахмарана — царя змей: человек по пояс, ниже — длинный змеиный хвост.")

# ---------------------------------------------------------------- Шойынқұлақ
sq = Model("shoiynqulaq", 128, 128, {
    "skin": (150, 120, 100), "skin_d": (118, 92, 76), "iron": (70, 72, 78), "iron_l": (120, 124, 132),
    "iron_d": (40, 42, 46), "beard": (60, 50, 44), "eye": (220, 60, 40), "fur": (110, 80, 50),
    "fur_d": (80, 56, 34), "belt": (60, 36, 20), "gold": (210, 160, 50), "leg": (80, 60, 44),
}, 804)


def sq_face(face, x, y, w, h):
    if face == "front":
        if y == 4 and x in (2, 3, 6, 7):
            return "eye" if x in (3, 6) else None
        if y >= 7:
            return "beard"
    return None


def sq_iron(face, x, y, w, h):
    if (x + y) % 5 == 0:
        return "iron_l"
    if (x * y) % 7 == 3:
        return "iron_d"
    return None


def sq_fur(face, x, y, w, h):
    if y >= h - 3:
        return "belt" if y < h - 1 else "gold"
    if (x * 3 + y) % 5 == 0:
        return "fur_d"
    return None


sq.part("body", "root", (0, -6, 0), cubes=[(-7, 0, -4, 14, 16, 8, "fur", {"paint": sq_fur})])
sq.part("head", "body", (0, 0, 0), cubes=[(-5, -10, -5, 10, 10, 10, "skin", {"paint": sq_face}),
                                          (-5, 0, -5.5, 10, 4, 1, "beard")])
for side, s in (("l", 1), ("r", -1)):
    # огромные чугунные уши
    sq.part("ear_" + side, "head", (5 * s, -6, 0), (0, 0.2 * s, 0), cubes=[(-1 if s > 0 else -1, -7, -5, 2, 14, 10, "iron", {"paint": sq_iron})])
    sq.part("arm_" + side, "body", (9 * s, 2, 0), cubes=[(-3, -2, -3, 6, 18, 6, "skin", {"paint": lambda f, x, y, w, h: "iron" if y >= 15 else None})])
    sq.part("leg_" + side, "root", (3.5 * s, 10, 0), cubes=[(-3, 0, -3, 6, 14, 6, "leg", {"paint": lambda f, x, y, w, h: "iron_d" if y >= 12 else None})])
sq.write_java("ShoiynqulaqModel", """
        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        float swing = Mth.cos(limbPos * 0.5F) * 1.0F * limbSpeed;
        this.leg_l.xRot = swing;
        this.leg_r.xRot = -swing;
        this.arm_l.xRot = -swing * 0.8F;
        this.arm_r.xRot = swing * 0.8F;
        // уши раскрываются перед хлопком
        float flap = state.attacking ? 0.9F + Mth.sin(age * 0.8F) * 0.4F : 0.15F + Mth.sin(age * 0.05F) * 0.05F;
        this.ear_l.yRot = flap;
        this.ear_r.yRot = -flap;
        if (state.attacking) {
            this.arm_l.xRot = -1.8F;
            this.arm_r.xRot = -1.8F;
        }""", "Модель Шойынқұлақ — великана с огромными чугунными ушами.")

# ---------------------------------------------------------------- Қаңбақ (перекати-поле)
qb = Model("qanbaq", 64, 32, {
    "twig": (170, 140, 92), "twig_d": (120, 96, 60), "twig_l": (204, 180, 128), "dry": (150, 130, 80),
}, 805)


def qb_twig(face, x, y, w, h):
    v = (x * 7 + y * 3) % 5
    return "twig_d" if v == 0 else ("twig_l" if v == 3 else None)


qb.part("ball", "root", (0, 17, 0))
for i in range(9):
    a, b, c = (i * 0.7) % 3.1, (i * 1.3) % 3.1, (i * 0.45) % 3.1
    ln = 12 - (i % 3) * 2
    qb.part("t%d" % i, "ball", (0, 0, 0), (a, b, c), cubes=[(-ln / 2, -0.5, -0.5, ln, 1, 1, "twig", {"paint": qb_twig})])
qb.part("c0", "ball", (0, 0, 0), (0, 0, 0.3), cubes=[(-3, -3, -3, 6, 6, 6, "dry", {"paint": qb_twig})])
qb.write_java("QanbaqModel", """
        this.ball.xRot = limbPos * 0.9F;
        this.ball.zRot = Mth.sin(limbPos * 0.3F) * 0.2F;""", "Модель перекати-поля — клубок сухих веток.")

V8_MODELS = (zh, jn, sm, sq, qb)
