package kz.ezhelgi.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** Общее состояние для моделей мода. */
public class EzhelgiRenderState extends LivingEntityRenderState {
    public boolean sitting;
    public boolean flying;
    public boolean saddled;
    public float wheel;
    public boolean male;
    public boolean attacking;
    public net.minecraft.resources.Identifier texture;
}
