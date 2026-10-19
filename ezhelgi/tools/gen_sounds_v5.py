"""v5 sounds: kobyz (bowed horse-hair fiddle of the baksy) and qamshy whip crack.
Run: python3 tools/gen_sounds_v5.py  (numpy, scipy, ffmpeg)
"""
import os
import subprocess
import tempfile

import numpy as np
from scipy.io import wavfile
from scipy.signal import lfilter

SR = 44100
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SND = os.path.join(ROOT, "src/main/resources/assets/ezhelgi/sounds")


def write(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    data = data / (np.max(np.abs(data)) + 1e-9) * 0.88
    with tempfile.TemporaryDirectory() as tmp:
        wav = os.path.join(tmp, "a.wav")
        wavfile.write(wav, SR, (data * 32767).astype(np.int16))
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-i", wav, "-ac", "1",
                        "-c:a", "libvorbis", "-q:a", "5", path], check=True)
    print("wrote", path)


def resonate(sig, f0, q, g):
    w = 2 * np.pi * f0 / SR
    r = 1 - w / (2 * q)
    return g * lfilter([1 - r], [1, -2 * r * np.cos(w), r * r], sig)


def bowed(freq_curve, amp_curve, rng):
    """Bowed string: band-limited sawtooth with bow noise and vibrato."""
    n = len(freq_curve)
    phase = np.cumsum(freq_curve) / SR
    out = np.zeros(n)
    for k in range(1, 18):
        # kobyz: strong odd+even harmonics, slightly nasal
        a = (1.0 / k) * (1.25 if k in (3, 4, 5) else 1.0)
        mask = freq_curve * k < SR / 2.2
        out += a * np.sin(2 * np.pi * k * phase + rng.uniform(0, 6.28)) * mask
    noise = rng.normal(0, 1, n)
    noise = lfilter([1], [1, -0.97], noise) * 0.004
    out = (out + noise * 25) * amp_curve
    # hollow bowl body: formants
    body = out * 0.5
    for f0, q, g in ((260, 4, 0.8), (620, 6, 0.6), (1250, 7, 0.45), (2600, 5, 0.2)):
        body += resonate(out, f0, q, g)
    return body


def kobyz_phrase(notes, seed, total):
    """notes: (start, dur, freq_from, freq_to). Drone on a low string under the melody."""
    rng = np.random.default_rng(seed)
    n = int(total * SR)
    t = np.arange(n) / SR
    freq = np.full(n, notes[0][2])
    amp = np.zeros(n)
    for (st, du, f0, f1) in notes:
        a, b = int(st * SR), min(n, int((st + du) * SR))
        seg = np.linspace(0, 1, b - a)
        glide = f0 * (f1 / f0) ** np.minimum(1, seg * 3)  # glissando at the start of the note
        freq[a:b] = glide
        env = np.minimum(1, seg * du / 0.12) * np.minimum(1, (1 - seg) * du / 0.15)
        amp[a:b] = np.maximum(amp[a:b], env * (0.8 + 0.2 * np.sin(seg * 3.1)))
    vib = 1 + 0.012 * np.sin(2 * np.pi * 5.3 * t) * np.minimum(1, t / 0.6)
    melody = bowed(freq * vib, amp, rng)
    drone_amp = np.minimum(1, t / 0.3) * np.minimum(1, (total - t) / 0.4) * 0.55
    drone = bowed(np.full(n, 110.0) * (1 + 0.003 * np.sin(2 * np.pi * 0.7 * t)), drone_amp, rng)
    out = melody + drone
    for d, g in ((0.031, 0.25), (0.057, 0.18), (0.093, 0.12), (0.17, 0.08)):
        k = int(d * SR)
        out[k:] += g * out[:-k]
    out = lfilter([1, -1], [1, -0.995], out)
    return out


def kobyz():
    A2, D3, E3, F3, G3, A3, C4, D4 = 110, 146.8, 164.8, 174.6, 196, 220, 261.6, 293.7
    phrases = [
        [(0.0, 0.7, A2 * 1.3, D3), (0.7, 0.45, D3, E3), (1.15, 0.9, E3 * 0.97, G3), (2.05, 0.4, G3, E3)],
        [(0.0, 0.5, E3, A3), (0.5, 0.6, A3, G3), (1.1, 0.4, G3, E3), (1.5, 1.0, E3, D3)],
        [(0.0, 0.9, D3 * 0.95, A3), (0.9, 0.35, C4, A3), (1.25, 0.5, A3, G3), (1.75, 0.75, G3 * 1.02, D3)],
    ]
    for i, ph in enumerate(phrases, 1):
        write(os.path.join(SND, "kobyz", f"kobyz{i}.ogg"), kobyz_phrase(ph, 70 + i, 2.6))


def crack():
    for i in (1, 2):
        rng = np.random.default_rng(90 + i)
        n = int(0.45 * SR)
        t = np.arange(n) / SR
        # swish before the crack
        sw = rng.normal(0, 1, n) * np.exp(-((t - 0.09) / 0.05) ** 2) * 0.25
        sw = lfilter([1, -0.6], [1], sw)
        c0 = int((0.12 + 0.01 * i) * SR)
        cr = np.zeros(n)
        k = np.arange(n - c0) / SR
        burst = rng.normal(0, 1, n - c0) * np.exp(-k / 0.006)
        burst += np.sign(np.sin(2 * np.pi * 2200 * k)) * np.exp(-k / 0.002) * 0.8
        cr[c0:] = burst
        tail = lfilter([1], [1, -0.9], cr) * 0.15
        write(os.path.join(SND, "qamshy", f"crack{i}.ogg"), sw + cr + tail)


if __name__ == "__main__":
    kobyz()
    crack()
