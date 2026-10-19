package kz.ezhelgi.client;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

/** Состояние для людей и духов мода: своя текстура и «игра на кобызе». */
public class NpcRenderState extends HumanoidRenderState {
    public Identifier texture;
    public boolean playing;
    public boolean statue;
}
