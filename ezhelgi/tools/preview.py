"""Tiny software renderer to preview box models (entity models and armor) as PNG.
Not needed for the mod build; used only to check how textures look.
"""
import math
from PIL import Image, ImageDraw


def rot_matrix(rx, ry, rz):
    cx, sx = math.cos(rx), math.sin(rx)
    cy, sy = math.cos(ry), math.sin(ry)
    cz, sz = math.cos(rz), math.sin(rz)
    Rx = [[1, 0, 0], [0, cx, -sx], [0, sx, cx]]
    Ry = [[cy, 0, sy], [0, 1, 0], [-sy, 0, cy]]
    Rz = [[cz, -sz, 0], [sz, cz, 0], [0, 0, 1]]

    def mul(a, b):
        return [[sum(a[i][k] * b[k][j] for k in range(3)) for j in range(3)] for i in range(3)]
    return mul(mul(Rz, Ry), Rx)


def apply(m, t, p):
    return [sum(m[i][k] * p[k] for k in range(3)) + t[i] for i in range(3)]


def compose(parent, local_t, local_r):
    pm, pt = parent
    lm = rot_matrix(*local_r)
    nm = [[sum(pm[i][k] * lm[k][j] for k in range(3)) for j in range(3)] for i in range(3)]
    nt = apply(pm, pt, local_t)
    return nm, nt


def cube_faces(x, y, z, w, h, d, u, v, inflate=0.0):
    W, H, D = math.ceil(w), math.ceil(h), math.ceil(d)
    x0, y0, z0 = x - inflate, y - inflate, z - inflate
    x1, y1, z1 = x + w + inflate, y + h + inflate, z + d + inflate
    v0 = (x0, y0, z0); v1 = (x1, y0, z0); v2 = (x1, y1, z0); v3 = (x0, y1, z0)
    v4 = (x0, y0, z1); v5 = (x1, y0, z1); v6 = (x1, y1, z1); v7 = (x0, y1, z1)
    return [
        ((v5, v4, v0, v1), (u + D, v, u + D + W, v + D)),
        ((v2, v3, v7, v6), (u + D + W, v + D, u + D + W + W, v)),
        ((v0, v4, v7, v3), (u, v + D, u + D, v + D + H)),
        ((v1, v0, v3, v2), (u + D, v + D, u + D + W, v + D + H)),
        ((v5, v1, v2, v6), (u + D + W, v + D, u + D + W + D, v + D + H)),
        ((v4, v5, v6, v7), (u + D + W + D, v + D, u + D + W + D + W, v + D + H)),
    ]


def render(parts, texture, out, yaw=35, pitch=20, scale=10, size=(420, 420), hidden=()):
    """parts: list of (name, parent, pivot, rot, cubes) where cube=(x,y,z,w,h,d,u,v[,inflate])."""
    tex = Image.open(texture).convert("RGBA")
    tw, th = tex.size
    tp = tex.load()
    xforms = {"root": ([[1, 0, 0], [0, 1, 0], [0, 0, 1]], [0, 0, 0])}
    quads = []
    cyaw, syaw = math.cos(math.radians(yaw)), math.sin(math.radians(yaw))
    cp, sp = math.cos(math.radians(pitch)), math.sin(math.radians(pitch))

    def project(p):
        # model space: y down; flip to world (x mirrored like the game renderer)
        X, Y, Z = -p[0], -p[1] + 24, p[2]
        x1 = X * cyaw - Z * syaw
        z1 = X * syaw + Z * cyaw
        y2 = Y * cp - z1 * sp
        z2 = Y * sp + z1 * cp
        return (size[0] / 2 + x1 * scale, size[1] * 0.78 - y2 * scale, z2)

    for name, parent, pivot, rot, cubes in parts:
        xforms[name] = compose(xforms[parent], pivot, rot)
        if name in hidden:
            continue
        m, t = xforms[name]
        for c in cubes:
            infl = c[8] if len(c) > 8 else 0.0
            for corners, (u1, v1, u2, v2) in cube_faces(*c[:8], inflate=infl):
                wc = [apply(m, t, list(p)) for p in corners]
                pc = [project(p) for p in wc]
                # back-face culling in screen space
                ax, ay = pc[1][0] - pc[0][0], pc[1][1] - pc[0][1]
                bx, by = pc[2][0] - pc[1][0], pc[2][1] - pc[1][1]
                if ax * by - ay * bx > 0:
                    continue
                quads.append((pc, (u1, v1, u2, v2)))
    img = Image.new("RGBA", size, (110, 150, 200, 255))
    dr = ImageDraw.Draw(img)

    def depth(q):
        return sum(p[2] for p in q[0]) / 4
    for pc, (u1, v1, u2, v2) in sorted(quads, key=depth, reverse=True):
        nu, nv = abs(u2 - u1), abs(v2 - v1)
        if nu == 0 or nv == 0:
            continue
        c0, c1, c2 = pc[0], pc[1], pc[2]

        def pt(s, t_):
            return (c1[0] + s * (c0[0] - c1[0]) + t_ * (c2[0] - c1[0]),
                    c1[1] + s * (c0[1] - c1[1]) + t_ * (c2[1] - c1[1]))
        for i in range(nu):
            for j in range(nv):
                tu = int(min(u1, u2) + i) if u2 > u1 else int(u1 - 1 - i)
                tv = int(v1 + j) if v2 > v1 else int(v1 - 1 - j)
                if not (0 <= tu < tw and 0 <= tv < th):
                    continue
                col = tp[tu, tv]
                if col[3] < 10:
                    continue
                s0, s1 = i / nu, (i + 1) / nu
                t0, t1 = j / nv, (j + 1) / nv
                poly = [pt(s0, t0), pt(s1, t0), pt(s1, t1), pt(s0, t1)]
                dr.polygon(poly, fill=col)
    img.save(out)
