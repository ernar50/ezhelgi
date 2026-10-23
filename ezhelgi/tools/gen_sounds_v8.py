"""v8 sounds: сыбызғы, жетіген, шаңқобыз, дауылпаз; бульканье казана и кипение самауыра.
Run: python3 tools/gen_sounds_v8.py  (numpy, scipy, ffmpeg)
"""
import os

import numpy as np

from gen_sounds_v5 import write, resonate, SR, SND


def t_of(dur):
    return np.arange(int(SR * dur)) / SR


def env_adsr(n, a, d, s, r):
    e = np.ones(n) * s
    ai, di, ri = int(a * SR), int(d * SR), int(r * SR)
    e[:ai] = np.linspace(0, 1, ai)
    e[ai:ai + di] = np.linspace(1, s, di)
    if ri:
        e[-ri:] *= np.linspace(1, 0, ri)
    return e


def place(out, sig, at):
    i = int(at * SR)
    j = min(len(out), i + len(sig))
    out[i:j] += sig[:j - i]


# ------------------------------------------------------------------ сыбызғы: флейта с горловым гудением
def sybyzgy(seed, notes):
    rng = np.random.default_rng(seed)
    total = sum(d for _, d in notes) + 0.4
    out = np.zeros(int(SR * total))
    drone_t = t_of(total)
    # горловой бурдон исполнителя
    drone = 0.25 * np.sin(2 * np.pi * 146.8 * drone_t) + 0.12 * np.sin(2 * np.pi * 293.6 * drone_t)
    drone *= np.minimum(1, drone_t / 0.3) * np.minimum(1, (total - drone_t) / 0.3)
    out += drone
    at = 0.0
    for f, d in notes:
        t = t_of(d + 0.15)
        vib = 1 + 0.012 * np.sin(2 * np.pi * 5.2 * t) * np.minimum(1, t / 0.25)
        ph = 2 * np.pi * np.cumsum(f * vib) / SR
        tone = np.sin(ph) + 0.3 * np.sin(2 * ph) + 0.08 * np.sin(3 * ph)
        breath = resonate(rng.normal(0, 1, len(t)), f * 2, 4, 0.2) + resonate(rng.normal(0, 1, len(t)), 3000, 1, 0.05)
        e = env_adsr(len(t), 0.06, 0.1, 0.8, 0.12)
        place(out, e * (tone * 0.7 + breath), at)
        at += d
    return out


# ------------------------------------------------------------------ жетіген: щипковая арфа (Карплус — Стронг)
def pluck(f, dur, rng, bright=0.5):
    n = int(SR * dur)
    period = int(SR / f)
    buf = rng.uniform(-1, 1, period)
    out = np.zeros(n)
    for i in range(n):
        j = i % period
        out[i] = buf[j]
        buf[j] = 0.997 * (bright * buf[j] + (1 - bright) * buf[(j + 1) % period])
    body = resonate(out, 220, 3, 0.4) + out
    return body * np.exp(-np.arange(n) / SR * 1.2)


def zhetigen(seed, notes):
    rng = np.random.default_rng(seed)
    total = notes[-1][0] + 2.0
    out = np.zeros(int(SR * total))
    for at, f in notes:
        place(out, pluck(f, 1.9, rng), at)
    return out


# ------------------------------------------------------------------ шаңқобыз: варган с «ва-ва» формантой
def shanqobyz(seed, rhythm):
    rng = np.random.default_rng(seed)
    total = rhythm[-1][0] + 0.9
    out = np.zeros(int(SR * total))
    f0 = 98.0
    for at, formant in rhythm:
        t = t_of(0.8)
        ph = 2 * np.pi * f0 * t
        saw = sum(np.sin(k * ph) / k for k in range(1, 30))
        e = np.exp(-t * 4.5) * np.minimum(1, t / 0.004)
        # движение языка меняет форманту
        sweep = formant * (1 + 0.6 * np.sin(2 * np.pi * 3 * t))
        sig = np.zeros(len(t))
        seg = 400
        for i in range(0, len(t), seg):
            sig[i:i + seg] = resonate(saw[i:i + seg], float(np.mean(sweep[i:i + seg])), 9, 1.0)
        place(out, (sig + 0.15 * saw) * e, at)
    out += 0.02 * rng.normal(0, 1, len(out))
    return out


# ------------------------------------------------------------------ дауылпаз: боевой барабан
def drum_hit(rng, f=70, dur=0.7, loud=1.0):
    t = t_of(dur)
    pitch = f * (1 + 1.4 * np.exp(-t * 25))
    ph = 2 * np.pi * np.cumsum(pitch) / SR
    body = np.sin(ph) * np.exp(-t * 6)
    skin = resonate(rng.normal(0, 1, len(t)), 180, 2, 0.6) * np.exp(-t * 30)
    return loud * (body + skin)


def dauylpaz(seed, pattern):
    rng = np.random.default_rng(seed)
    total = pattern[-1][0] + 0.9
    out = np.zeros(int(SR * total))
    for at, loud in pattern:
        place(out, drum_hit(rng, 62 + rng.uniform(-3, 3), 0.8, loud), at)
    return out


# ------------------------------------------------------------------ кухня
def bubbles(seed, n=6, dur=1.1):
    rng = np.random.default_rng(seed)
    out = np.zeros(int(SR * dur))
    for _ in range(n):
        t = t_of(0.06 + rng.uniform(0, 0.05))
        f = rng.uniform(300, 700) * (1 + 3 * t / t[-1])
        b = np.sin(2 * np.pi * np.cumsum(f) / SR) * np.exp(-t * 40)
        place(out, b * rng.uniform(0.4, 1.0), rng.uniform(0, dur - 0.12))
    out += resonate(rng.normal(0, 1, len(out)), 400, 1, 0.05)
    return out


def boil(seed, dur=2.2):
    rng = np.random.default_rng(seed)
    t = t_of(dur)
    hiss = resonate(rng.normal(0, 1, len(t)), 2500, 0.8, 0.5) + resonate(rng.normal(0, 1, len(t)), 900, 1.5, 0.3)
    e = np.minimum(1, t / 0.8) * np.minimum(1, (dur - t) / 0.4)
    out = hiss * e * (1 + 0.3 * np.sin(2 * np.pi * 7 * t))
    out += bubbles(seed + 9, 14, dur) * 0.8
    return out


def main():
    d = os.path.join(SND, "instruments")
    # пентатоника степной мелодии
    A, B, Cs, E, Fs, A2 = 440.0, 493.9, 554.4, 659.3, 740.0, 880.0
    write(f"{d}/sybyzgy1.ogg", sybyzgy(1, [(E, 0.5), (Fs, 0.3), (E, 0.3), (Cs, 0.5), (B, 0.4), (A, 0.9)]))
    write(f"{d}/sybyzgy2.ogg", sybyzgy(2, [(A, 0.4), (Cs, 0.4), (E, 0.6), (A2, 0.5), (Fs, 0.4), (E, 0.9)]))
    D3 = 146.8
    scale = [D3, D3 * 9 / 8, D3 * 5 / 4, D3 * 3 / 2, D3 * 5 / 3, D3 * 2, D3 * 9 / 4]
    write(f"{d}/zhetigen1.ogg", zhetigen(3, [(i * 0.18, f) for i, f in enumerate(scale)] + [(1.5, scale[3]), (1.7, scale[5])]))
    write(f"{d}/zhetigen2.ogg", zhetigen(4, [(0.0, scale[0]), (0.25, scale[3]), (0.5, scale[5]), (0.8, scale[4]),
                                             (1.0, scale[2]), (1.3, scale[3]), (1.6, scale[0])]))
    write(f"{d}/shanqobyz1.ogg", shanqobyz(5, [(0.0, 800), (0.3, 1200), (0.6, 700), (0.75, 1400), (1.05, 900), (1.35, 1600), (1.65, 800)]))
    write(f"{d}/shanqobyz2.ogg", shanqobyz(6, [(0.0, 1000), (0.2, 1000), (0.4, 1500), (0.7, 700), (0.9, 700), (1.1, 1300), (1.5, 900)]))
    write(f"{d}/dauylpaz1.ogg", dauylpaz(7, [(0.0, 1.0), (0.35, 0.6), (0.55, 0.6), (0.9, 1.0), (1.25, 0.6), (1.45, 0.6), (1.8, 1.0)]))
    write(f"{d}/dauylpaz2.ogg", dauylpaz(8, [(0.0, 1.0), (0.25, 0.5), (0.5, 1.0), (0.75, 0.5), (1.0, 1.0), (1.2, 0.7), (1.4, 0.7), (1.6, 1.0)]))
    k = os.path.join(SND, "kitchen")
    for i in range(1, 4):
        write(f"{k}/bubble{i}.ogg", bubbles(10 + i))
    for i in range(1, 3):
        write(f"{k}/boil{i}.ogg", boil(20 + i))


if __name__ == "__main__":
    main()
