package kz.ezhelgi.block.togyz;

import java.util.Arrays;
import java.util.Random;

/**
 * Правила тоғыз құмалақ.
 * <ul>
 *   <li>У каждого игрока 9 лунок (отау) по 9 құмалақ, всего 162.</li>
 *   <li>Ход: берёшь все камни из своей лунки, первый кладёшь обратно, остальные — по одному
 *       в следующие лунки против часовой стрелки. Если в лунке один камень, он уходит в соседнюю.</li>
 *   <li>Если последний камень упал в лунку соперника и там стало чётное число — забираешь их в свой қазан.</li>
 *   <li>Если там стало ровно 3 — лунка становится твоим тұздық (не девятая, одна за игру,
 *       не напротив тұздық соперника). Всё, что туда попадает, идёт в твой қазан.</li>
 *   <li>Побеждает тот, кто собрал больше 81. Кому нечем ходить — у соперника забираются его остатки.</li>
 * </ul>
 * Лунки 0–8 — игрок 0 (ближний ряд), 9–17 — игрок 1.
 */
public final class TogyzGame {
    public static final int PITS = 9;
    public static final int START = 9;

    public final int[] pits = new int[PITS * 2];
    public final int[] kazan = new int[2];
    /** Тұздық игрока: глобальный номер лунки соперника или -1. */
    public final int[] tuzdyk = {-1, -1};
    public int turn;
    /** -1 — игра идёт, 0/1 — победитель, 2 — ничья. */
    public int winner = -1;
    /** Последняя лунка, куда упал камень (для подсветки), -1 — нет. */
    public int lastPit = -1;

    public TogyzGame() {
        reset();
    }

    public void reset() {
        Arrays.fill(this.pits, START);
        this.kazan[0] = this.kazan[1] = 0;
        this.tuzdyk[0] = this.tuzdyk[1] = -1;
        this.turn = 0;
        this.winner = -1;
        this.lastPit = -1;
    }

    public TogyzGame copy() {
        TogyzGame g = new TogyzGame();
        System.arraycopy(this.pits, 0, g.pits, 0, this.pits.length);
        g.kazan[0] = this.kazan[0];
        g.kazan[1] = this.kazan[1];
        g.tuzdyk[0] = this.tuzdyk[0];
        g.tuzdyk[1] = this.tuzdyk[1];
        g.turn = this.turn;
        g.winner = this.winner;
        return g;
    }

    public boolean isStart() {
        return this.kazan[0] == 0 && this.kazan[1] == 0 && this.turn == 0 && this.pits[0] == START && this.pits[17] == START;
    }

    private int owner(int pit) {
        if (this.tuzdyk[0] == pit) return 0;
        if (this.tuzdyk[1] == pit) return 1;
        return -1;
    }

    public boolean canMove(int side, int index) {
        if (this.winner >= 0 || side != this.turn || index < 0 || index >= PITS) {
            return false;
        }
        int g = side * PITS + index;
        return this.pits[g] > 0 && owner(g) < 0;
    }

    public boolean hasMoves(int side) {
        for (int i = 0; i < PITS; i++) {
            int g = side * PITS + i;
            if (this.pits[g] > 0 && owner(g) < 0) return true;
        }
        return false;
    }

    /** Результат хода: сколько камней взято и объявлен ли тұздық. */
    public record Result(int captured, boolean newTuzdyk) {}

    public Result move(int index) {
        int side = this.turn;
        int g = side * PITS + index;
        int stones = this.pits[g];
        this.pits[g] = 0;
        int pos;
        if (stones == 1) {
            pos = (g + 1) % 18;
            deposit(pos);
        } else {
            pos = g;
            for (int k = 0; k < stones; k++) {
                pos = (g + k) % 18;
                deposit(pos);
            }
        }
        this.lastPit = pos;
        int captured = 0;
        boolean tuz = false;
        int opp = 1 - side;
        if (pos / PITS == opp && owner(pos) < 0) {
            int n = this.pits[pos];
            if (n % 2 == 0) {
                captured = n;
                this.kazan[side] += n;
                this.pits[pos] = 0;
            } else if (n == 3 && this.tuzdyk[side] < 0 && pos % PITS != PITS - 1
                    && (this.tuzdyk[opp] < 0 || this.tuzdyk[opp] % PITS != pos % PITS)) {
                this.tuzdyk[side] = pos;
                this.kazan[side] += 3;
                this.pits[pos] = 0;
                captured = 3;
                tuz = true;
            }
        }
        this.turn = opp;
        checkEnd();
        return new Result(captured, tuz);
    }

    private void deposit(int pos) {
        int o = owner(pos);
        if (o >= 0) {
            this.kazan[o]++;
        } else {
            this.pits[pos]++;
        }
    }

    private void checkEnd() {
        if (this.kazan[0] > 81) { this.winner = 0; return; }
        if (this.kazan[1] > 81) { this.winner = 1; return; }
        if (!hasMoves(this.turn)) {
            // ходить нечем: соперник забирает камни со своей стороны
            int other = 1 - this.turn;
            for (int i = 0; i < PITS; i++) {
                int g = other * PITS + i;
                this.kazan[other] += this.pits[g];
                this.pits[g] = 0;
            }
            this.winner = this.kazan[0] > this.kazan[1] ? 0 : this.kazan[1] > this.kazan[0] ? 1 : 2;
        } else if (this.kazan[0] == 81 && this.kazan[1] == 81) {
            this.winner = 2;
        }
    }

    // ------------------------------------------------------------------ ИИ аксакала
    /** Выбирает ход: жадный поиск на три полухода с небольшой случайностью. */
    public int bestMove(Random random, int depth) {
        int side = this.turn;
        int best = -1;
        double bestScore = -1e9;
        for (int i = 0; i < PITS; i++) {
            if (!canMove(side, i)) continue;
            TogyzGame next = copy();
            next.move(i);
            double score = -next.search(depth - 1) + random.nextDouble() * 0.6;
            if (score > bestScore) {
                bestScore = score;
                best = i;
            }
        }
        return best;
    }

    /** Оценка позиции с точки зрения того, чей ход. */
    private double search(int depth) {
        int side = this.turn;
        if (this.winner >= 0) {
            if (this.winner == 2) return 0;
            return this.winner == side ? 1000 : -1000;
        }
        if (depth <= 0) {
            double eval = this.kazan[side] - this.kazan[1 - side];
            if (this.tuzdyk[side] >= 0) eval += 6;
            if (this.tuzdyk[1 - side] >= 0) eval -= 6;
            return eval;
        }
        double best = -1e9;
        for (int i = 0; i < PITS; i++) {
            if (!canMove(side, i)) continue;
            TogyzGame next = copy();
            next.move(i);
            best = Math.max(best, -next.search(depth - 1));
        }
        return best;
    }

    // ------------------------------------------------------------------ сохранение
    public int[] save() {
        int[] out = new int[18 + 7];
        System.arraycopy(this.pits, 0, out, 0, 18);
        out[18] = this.kazan[0];
        out[19] = this.kazan[1];
        out[20] = this.tuzdyk[0];
        out[21] = this.tuzdyk[1];
        out[22] = this.turn;
        out[23] = this.winner;
        out[24] = this.lastPit;
        return out;
    }

    public void load(int[] in) {
        if (in == null || in.length < 25) {
            reset();
            return;
        }
        System.arraycopy(in, 0, this.pits, 0, 18);
        this.kazan[0] = in[18];
        this.kazan[1] = in[19];
        this.tuzdyk[0] = in[20];
        this.tuzdyk[1] = in[21];
        this.turn = in[22];
        this.winner = in[23];
        this.lastPit = in[24];
    }
}
