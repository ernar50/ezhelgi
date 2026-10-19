package kz.ezhelgi.sound;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** Звуки мода (описаны в assets/ezhelgi/sounds.json). */
public final class ModSounds {
    private ModSounds() {}

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, EzhelgiMod.MODID);

    public static final RegistryObject<SoundEvent> DOMBYRA_KUI = sound("item.dombyra.kui");

    public static final RegistryObject<SoundEvent> WARRIOR_AMBIENT = sound("entity.altyn_jauynger.ambient");
    public static final RegistryObject<SoundEvent> WARRIOR_HURT = sound("entity.altyn_jauynger.hurt");
    public static final RegistryObject<SoundEvent> WARRIOR_DEATH = sound("entity.altyn_jauynger.death");
    public static final RegistryObject<SoundEvent> WARRIOR_CRY = sound("entity.altyn_jauynger.cry");

    public static final RegistryObject<SoundEvent> JUNGAR_AMBIENT = sound("entity.jungar.ambient");
    public static final RegistryObject<SoundEvent> JUNGAR_HURT = sound("entity.jungar.hurt");
    public static final RegistryObject<SoundEvent> JUNGAR_DEATH = sound("entity.jungar.death");

    public static final RegistryObject<SoundEvent> BOSS_AMBIENT = sound("entity.altyn_adam.ambient");
    public static final RegistryObject<SoundEvent> BOSS_HURT = sound("entity.altyn_adam.hurt");
    public static final RegistryObject<SoundEvent> BOSS_DEATH = sound("entity.altyn_adam.death");
    public static final RegistryObject<SoundEvent> BOSS_ROAR = sound("entity.altyn_adam.roar");
    public static final RegistryObject<SoundEvent> BOSS_SLAM = sound("entity.altyn_adam.slam");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name)));
    }
}
