"""Preview renderer for humanoid skins with a JSON item model worn on the head.
Not needed for the build; only to check how things look.
Usage: python3 tools/preview2.py skin.png [item_model.json] out.png [yaw]
"""
import json
import math
import os
import sys
from PIL import Image, ImageDraw

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
TEX = os.path.join(ROOT, "src/main/resources/assets")

# humanoid boxes in model space (y down, front = -z): (pivot, box origin, size, uv)
HUMANOID = [
    ((0, 0, 0), (-4, -8, -4), (8, 8, 8), (0, 0)),        # head
    ((0, 0, 0), (-4, -8, -4), (8, 8, 8), (32, 0), 0.5),  # hat
    ((0, 0, 0), (-4, 0, -2), (8, 12, 4), (16, 16)),      # body
    ((-5, 2, 0), (-3, -2, -2), (4, 12, 4), (40, 16)),    # right arm
    ((5, 2, 0), (-1, -2, -2), (4, 12, 4), (40, 16), 0, True),  # left arm (mirrored)
    ((-1.9, 12, 0), (-2, 0, -2), (4, 12, 4), (0, 16)),   # right leg
    ((1.9, 12, 0), (-2, 0, -2), (4, 12, 4), (0, 16), 0, True),
]


def box_quads(o, s, uv, infl=0.0, mirror=False):
    x0, y0, z0 = o[0] - infl, o[1] - infl, o[2] - infl
    x1, y1, z1 = o[0] + s[0] + infl, o[1] + s[1] + infl, o[2] + s[2] + infl
    W, H, D = s
    u, v = uv
    # model space: y down. Faces given as (TL, TR, BR, BL) seen from outside, uv rect (u1,v1,u2,v2)
    q = [
        (((x0, y0, z0), (x1, y0, z0), (x1, y1, z0), (x0, y1, z0)), (u + D, v + D, u + D + W, v + D + H)),  # front -z
        (((x1, y0, z1), (x0, y0, z1), (x0, y1, z1), (x1, y1, z1)), (u + 2 * D + W, v + D, u + 2 * D + 2 * W, v + D + H)),
        (((x0, y0, z1), (x0, y0, z0), (x0, y1, z0), (x0, y1, z1)), (u, v + D, u + D, v + D + H)),  # right side -x
        (((x1, y0, z0), (x1, y0, z1), (x1, y1, z1), (x1, y1, z0)), (u + D + W, v + D, u + 2 * D + W, v + D + H)),
        (((x0, y0, z1), (x1, y0, z1), (x1, y0, z0), (x0, y0, z0)), (u + D, v, u + D + W, v + D)),  # top
        (((x0, y1, z0), (x1, y1, z0), (x1, y1, z1), (x0, y1, z1)), (u + D + W, v, u + D + 2 * W, v + D)),
    ]
    if mirror:
        q = [(c, (r[2], r[1], r[0], r[3])) for c, r in q]
    return q


def json_quads(model_path):
    data = json.load(open(model_path))
    tex = {}
    for k, val in data["textures"].items():
        ns, p = val.split(":")
        tex[k] = Image.open(os.path.join(TEX, ns, "textures", p + ".png")).convert("RGBA")
    out = []

    def m(u, v, w):  # json block units -> head model space (CustomHeadLayer transform)
        return (-0.625 * (u - 8), -0.625 * (v - 8) - 4, 0.625 * (w - 8))
    for el in data["elements"]:
        (a, b, c), (d, e, f) = el["from"], el["to"]
        corners = {
            "north": ((d, e, c), (a, e, c), (a, b, c), (d, b, c)),
            "south": ((a, e, f), (d, e, f), (d, b, f), (a, b, f)),
            "west": ((a, e, c), (a, e, f), (a, b, f), (a, b, c)),
            "east": ((d, e, f), (d, e, c), (d, b, c), (d, b, f)),
            "up": ((a, e, c), (d, e, c), (d, e, f), (a, e, f)),
            "down": ((a, b, f), (d, b, f), (d, b, c), (a, b, c)),
        }
        for fname, face in el["faces"].items():
            img = tex[face["texture"][1:]]
            sc = img.size[0] / 16.0
            uv = [x * sc for x in face.get("uv", [0, 0, 16, 16])]
            out.append(([m(*p) for p in corners[fname]], img, uv))
    return out


def render(skin, item, out, yaw=25, pitch=10, scale=14, size=(460, 520), head_only=False, extra=()):
    skin_img = Image.open(skin).convert("RGBA")
    quads = []
    for entry in HUMANOID:
        pivot, o, s, uv = entry[:4]
        infl = entry[4] if len(entry) > 4 else 0.0
        mirror = entry[5] if len(entry) > 5 else False
        if head_only and uv not in ((0, 0), (32, 0)):
            continue
        for corners, r in box_quads(o, s, uv, infl, mirror):
            quads.append(([(p[0] + pivot[0], p[1] + pivot[1], p[2] + pivot[2]) for p in corners], skin_img, r))
    for pivot, o, sz, uv in extra:
        for corners, r in box_quads(o, sz, uv):
            quads.append(([(p[0] + pivot[0], p[1] + pivot[1], p[2] + pivot[2]) for p in corners], skin_img, r))
    if item:
        quads += json_quads(item)
    cy, sy = math.cos(math.radians(yaw + 180)), math.sin(math.radians(yaw + 180))
    cp, sp = math.cos(math.radians(pitch)), math.sin(math.radians(pitch))

    def proj(p):
        X, Y, Z = p[0], -p[1], p[2]
        x1 = X * cy + Z * sy
        z1 = -X * sy + Z * cy
        y2 = Y * cp - z1 * sp
        z2 = Y * sp + z1 * cp
        oy = size[1] * (0.62 if head_only else 0.36)
        return (size[0] / 2 - x1 * scale, oy - y2 * scale, z2)

    img = Image.new("RGBA", size, (120, 156, 196, 255))
    dr = ImageDraw.Draw(img)
    proj_quads = []
    for corners, timg, r in quads:
        pc = [proj(p) for p in corners]
        ax, ay = pc[1][0] - pc[0][0], pc[1][1] - pc[0][1]
        bx, by = pc[2][0] - pc[1][0], pc[2][1] - pc[1][1]
        if ax * by - ay * bx <= 0:
            continue
        proj_quads.append((sum(p[2] for p in pc) / 4, pc, timg, r))
    polys = []
    for _, pc, timg, (u1, v1, u2, v2) in proj_quads:
        tp = timg.load()
        tw, th = timg.size
        nu, nv = int(round(abs(u2 - u1))), int(round(abs(v2 - v1)))
        if nu == 0 or nv == 0:
            continue
        tl, tr, br, bl = pc

        def pt(s, t):
            top = [tl[k] + (tr[k] - tl[k]) * s for k in range(3)]
            bot = [bl[k] + (br[k] - bl[k]) * s for k in range(3)]
            return [top[k] + (bot[k] - top[k]) * t for k in range(3)]
        for i in range(nu):
            for j in range(nv):
                tu = int(min(u1, u2) + i) if u2 >= u1 else int(u1 - 1 - i)
                tv = int(min(v1, v2) + j) if v2 >= v1 else int(v1 - 1 - j)
                if not (0 <= tu < tw and 0 <= tv < th):
                    continue
                col = tp[tu, tv]
                if col[3] < 10:
                    continue
                corners = [pt(i / nu, j / nv), pt((i + 1) / nu, j / nv), pt((i + 1) / nu, (j + 1) / nv), pt(i / nu, (j + 1) / nv)]
                depth = sum(c[2] for c in corners) / 4
                polys.append((depth, [(c[0], c[1]) for c in corners], col))
    for depth, poly, col in sorted(polys, key=lambda q: q[0]):
        dr.polygon(poly, fill=col)
    img.save(out)


if __name__ == "__main__":
    args = sys.argv[1:]
    yaw = float(args[3]) if len(args) > 3 else 25
    render(args[0], args[1] if args[1] != "-" else None, args[2], yaw=yaw)
