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

    // v5
    public static final RegistryObject<SoundEvent> KOBYZ_PLAY = sound("item.kobyz.play");
    public static final RegistryObject<SoundEvent> QAMSHY_CRACK = sound("item.qamshy.crack");
    public static final RegistryObject<SoundEvent> RUNE_ACTIVATE = sound("block.runa.activate");
    public static final RegistryObject<SoundEvent> RUNE_CURSE = sound("block.runa.curse");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_AMBIENT = sound("entity.zheztyrnak.ambient");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_ANGRY = sound("entity.zheztyrnak.angry");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_SHRIEK = sound("entity.zheztyrnak.shriek");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_CLAW = sound("entity.zheztyrnak.claw");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_LEAP = sound("entity.zheztyrnak.leap");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_HURT = sound("entity.zheztyrnak.hurt");
    public static final RegistryObject<SoundEvent> ZHEZTYRNAK_DEATH = sound("entity.zheztyrnak.death");
    public static final RegistryObject<SoundEvent> ARUAQ_AMBIENT = sound("entity.batyr_aruagy.ambient");
    public static final RegistryObject<SoundEvent> ARUAQ_HURT = sound("entity.batyr_aruagy.hurt");
    public static final RegistryObject<SoundEvent> ARUAQ_DEATH = sound("entity.batyr_aruagy.death");
    public static final RegistryObject<SoundEvent> ARUAQ_PARRY = sound("entity.batyr_aruagy.parry");
    public static final RegistryObject<SoundEvent> ARUAQ_VANISH = sound("entity.batyr_aruagy.vanish");
    public static final RegistryObject<SoundEvent> ALBASTY_AMBIENT = sound("entity.albasty.ambient");
    public static final RegistryObject<SoundEvent> ALBASTY_HURT = sound("entity.albasty.hurt");
    public static final RegistryObject<SoundEvent> ALBASTY_DEATH = sound("entity.albasty.death");
    public static final RegistryObject<SoundEvent> ALBASTY_VANISH = sound("entity.albasty.vanish");
    public static final RegistryObject<SoundEvent> ALBASTY_APPEAR = sound("entity.albasty.appear");
    public static final RegistryObject<SoundEvent> KEMPIR_LAUGH = sound("entity.zhalmauyz_kempir.laugh");
    public static final RegistryObject<SoundEvent> KEMPIR_HURT = sound("entity.zhalmauyz_kempir.hurt");
    public static final RegistryObject<SoundEvent> KEMPIR_DEATH = sound("entity.zhalmauyz_kempir.death");
    public static final RegistryObject<SoundEvent> KEMPIR_CAST = sound("entity.zhalmauyz_kempir.cast");
    public static final RegistryObject<SoundEvent> KEMPIR_BOLT = sound("entity.zhalmauyz_kempir.bolt");
    public static final RegistryObject<SoundEvent> KEMPIR_SNARE = sound("entity.zhalmauyz_kempir.snare");
    public static final RegistryObject<SoundEvent> KEMPIR_SUMMON = sound("entity.zhalmauyz_kempir.summon");
    public static final RegistryObject<SoundEvent> SAMURYK_AMBIENT = sound("entity.samuryk.ambient");
    public static final RegistryObject<SoundEvent> SAMURYK_HURT = sound("entity.samuryk.hurt");
    public static final RegistryObject<SoundEvent> SAMURYK_DEATH = sound("entity.samuryk.death");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name)));
    }
}
