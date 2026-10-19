"""Synthesizes dombyra kui phrases (two plucked strings, Karplus-Strong) as OGG files.

Dombyra is tuned in fourths: bass string D3, melody string G3. A kui is played
with fast down/up strokes over both strings: the bass string mostly drones while
the melody moves on the top string. Requires numpy, scipy and ffmpeg.
Run: python3 tools/gen_sounds.py
"""
import os
import subprocess
import tempfile

import numpy as np
from scipy.io import wavfile
from scipy.signal import lfilter

SR = 44100
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src/main/resources/assets/ezhelgi/sounds/dombyra")

NOTE = {  # частоты (Гц)
    "C3": 130.81, "D3": 146.83, "E3": 164.81, "F3": 174.61, "G3": 196.00, "A3": 220.00,
    "Bb3": 233.08, "B3": 246.94, "C4": 261.63, "D4": 293.66, "E4": 329.63, "F4": 349.23, "G4": 392.00,
}


def pluck(freq, dur, amp, rng, bright=0.5, decay=0.996):
    n = int(SR * dur)
    period = SR / freq
    p = int(period)
    frac = period - p
    buf = rng.uniform(-1, 1, p)
    # мягкий медиатор: сглаживаем шум
    k = max(1, int(3 - bright * 2))
    buf = np.convolve(buf, np.ones(k) / k, mode="same")
    out = np.zeros(n)
    idx = 0
    for i in range(n):
        a = buf[idx % p]
        b = buf[(idx + 1) % p]
        val = (1 - frac) * a + frac * b
        out[i] = val
        buf[idx % p] = decay * 0.5 * (a + b)
        idx += 1
    env = np.minimum(1.0, np.arange(n) / (0.002 * SR))
    return out * env * amp


def body(signal):
    """Резонанс деревянного корпуса: два резонатора + немного «комнаты»."""
    out = signal.copy()
    for f0, q, g in ((210.0, 6.0, 0.6), (470.0, 8.0, 0.35), (1100.0, 5.0, 0.15)):
        w = 2 * np.pi * f0 / SR
        r = 1 - w / (2 * q)
        b = [1 - r]
        a = [1, -2 * r * np.cos(w), r * r]
        out += g * lfilter(b, a, signal)
    # короткое эхо
    for delay, g in ((0.023, 0.18), (0.041, 0.12), (0.067, 0.07)):
        d = int(delay * SR)
        out[d:] += g * out[:-d]
    # мягкий срез высоких
    out = lfilter([0.35, 0.35], [1, -0.3], out)
    return out


def phrase(melody, bass, seed, tempo=152, total=3.1):
    """melody/bass: списки нот на каждый удар (триоли: вниз-вниз-вверх)."""
    rng = np.random.default_rng(seed)
    n = int(SR * total)
    mix = np.zeros(n + 2 * SR)
    step = 60.0 / tempo / 3.0
    t = 0.0
    pattern = [1.0, 0.7, 0.55]  # акценты: вниз (сильно), вниз, вверх
    i = 0
    while t < total - 0.25 and i < len(melody):
        acc = pattern[i % 3]
        up = (i % 3) == 2
        top = NOTE[melody[i]]
        low = NOTE[bass[i % len(bass)]]
        ring = 1.2
        start = int(t * SR)
        human = rng.uniform(-0.006, 0.006)
        s_top = pluck(top, ring, 0.55 * acc, rng, bright=0.7)
        s_low = pluck(low, ring, 0.45 * acc, rng, bright=0.4, decay=0.995)
        gap = int(0.009 * SR)  # струны задеваются не одновременно
        a0 = max(0, start + int(human * SR))
        if up:
            mix[a0:a0 + len(s_top)] += s_top
            mix[a0 + gap:a0 + gap + len(s_low)] += s_low * 0.8
        else:
            mix[a0:a0 + len(s_low)] += s_low
            mix[a0 + gap:a0 + gap + len(s_top)] += s_top
        t += step
        i += 1
    out = body(mix[:n + int(0.3 * SR)])
    out = lfilter([1, -1], [1, -0.995], out)  # убираем постоянную составляющую
    fade = int(0.35 * SR)
    out[-fade:] *= np.linspace(1, 0, fade)
    out /= np.max(np.abs(out)) + 1e-9
    return (out * 0.85 * 32767).astype(np.int16)


PHRASES = [
    # мелодия на верхней струне (каждый символ — удар), бас — бурдон
    (["G3", "G3", "A3", "C4", "C4", "A3", "D4", "D4", "C4", "A3", "A3", "G3",
      "A3", "A3", "C4", "D4", "E4", "D4", "C4", "C4", "A3", "G3", "G3", "G3"],
     ["D3", "D3", "D3", "D3", "D3", "D3", "E3", "E3", "D3"], 1),
    (["D4", "D4", "E4", "D4", "C4", "A3", "C4", "C4", "D4", "A3", "G3", "A3",
      "C4", "C4", "D4", "E4", "G4", "E4", "D4", "C4", "A3", "G3", "A3", "G3"],
     ["D3", "D3", "D3", "G3", "G3", "D3"], 2),
    (["A3", "A3", "G3", "A3", "C4", "A3", "G3", "G3", "F3", "G3", "A3", "G3",
      "C4", "C4", "A3", "G3", "G3", "F3", "G3", "G3", "A3", "G3", "G3", "G3"],
     ["D3", "D3", "D3", "C3", "C3", "D3"], 3),
    (["G3", "A3", "C4", "D4", "D4", "D4", "E4", "D4", "C4", "D4", "D4", "D4",
      "C4", "A3", "G3", "A3", "C4", "A3", "G3", "G3", "G3", "G3", "A3", "G3"],
     ["D3", "D3", "D3", "D3", "E3", "D3"], 4),
]


def main():
    os.makedirs(OUT, exist_ok=True)
    for k, (mel, bass, seed) in enumerate(PHRASES, start=1):
        data = phrase(mel, bass, seed)
        with tempfile.TemporaryDirectory() as tmp:
            wav = os.path.join(tmp, "k.wav")
            wavfile.write(wav, SR, data)
            ogg = os.path.join(OUT, f"kui{k}.ogg")
            subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-ac", "1",
                            "-c:a", "libvorbis", "-q:a", "5", ogg], check=True)
        print("wrote", ogg)


if __name__ == "__main__":
    main()
