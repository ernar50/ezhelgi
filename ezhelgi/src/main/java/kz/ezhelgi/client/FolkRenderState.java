package kz.ezhelgi.client;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/** Состояние для мобов из сказок (жезтырнақ, албасты, жалмауыз кемпір). */
public class FolkRenderState extends HumanoidRenderState {
    /** 0..1 — насколько выдвинуты когти. */
    public float claws;
    /** Тики удара когтями (длинный выпад). */
    public float strike;
    public boolean revealed;
    /** Номер заклинания, 0 — не колдует. */
    public int casting;
}
