"""v7 sounds: поющий песок (низкий гул бархана) и стук құмалақ о доску.
Run: python3 tools/gen_sounds_v7.py  (numpy, scipy, ffmpeg)
"""
import os
import numpy as np
from gen_sounds_v5 import write, resonate, SR, SND


def sand_hum(seed, f0, dur=2.4):
    rng = np.random.default_rng(seed)
    n = int(SR * dur)
    t = np.arange(n) / SR
    env = np.minimum(1, t / 0.35) * np.exp(-np.maximum(0, t - 0.6) * 1.6)
    # гул: основной тон с медленным «плаванием» и биениями
    drift = f0 * (1 + 0.012 * np.sin(2 * np.pi * 0.7 * t + rng.uniform(0, 6)))
    phase = 2 * np.pi * np.cumsum(drift) / SR
    tone = np.zeros(n)
    for k, a in ((1, 1.0), (2, 0.55), (3, 0.3), (4, 0.18), (5, 0.08)):
        tone += a * np.sin(k * phase + rng.uniform(0, 6))
    tone += 0.35 * np.sin(phase * 1.007)  # биения
    # шорох осыпающегося песка
    hiss = rng.normal(0, 1, n)
    hiss = resonate(hiss, 2400, 1.2, 0.08) + resonate(hiss, 700, 2, 0.05)
    grains = (rng.random(n) < 0.004) * rng.normal(0, 1, n)
    grains = resonate(grains, 3200, 6, 0.6)
    am = 1 + 0.25 * np.sin(2 * np.pi * (6 + rng.uniform(0, 3)) * t)
    return env * (tone * am * 0.8 + hiss + grains)


def stone_click(seed):
    rng = np.random.default_rng(seed)
    n = int(SR * 0.5)
    out = np.zeros(n)
    # несколько камешков падают в лунку один за другим
    t0 = 0
    for i in range(rng.integers(2, 5)):
        burst = np.zeros(n)
        idx = int(t0 * SR)
        if idx >= n - 10:
            break
        burst[idx] = 1.0
        burst[idx + 1:idx + 4] = rng.normal(0, 0.5, 3)
        f = rng.uniform(2200, 3400)
        out += resonate(burst, f, 30, 1.0) + resonate(burst, f * 1.63, 20, 0.5) + resonate(burst, 420, 5, 0.6)
        t0 += rng.uniform(0.05, 0.11)
    t = np.arange(n) / SR
    return out * np.exp(-t * 6)


if __name__ == "__main__":
    for i, f0 in enumerate((92, 104, 118)):
        write(os.path.join(SND, "an_qum", f"sing{i + 1}.ogg"), sand_hum(500 + i, f0))
    for i in range(4):
        write(os.path.join(SND, "togyz", f"move{i + 1}.ogg"), stone_click(600 + i))
