package kz.ezhelgi.client;

import kz.ezhelgi.entity.AltynJauynger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** Старое имя рендерера золотого воина (оставлено для совместимости). */
public class AltynJauyngerRenderer extends HumanoidMonsterRenderer<AltynJauynger> {
    public AltynJauyngerRenderer(EntityRendererProvider.Context context) {
        super(context, "altyn_jauynger", 1.0f);
    }
}
