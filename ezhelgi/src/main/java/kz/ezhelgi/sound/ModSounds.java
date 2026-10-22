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
    // v6
    public static final RegistryObject<SoundEvent> BALBAL_AWAKE = sound("entity.balbal.awake");
    public static final RegistryObject<SoundEvent> BAQSY_WAVE = sound("entity.baqsy_kolenkesi.wave");
    public static final RegistryObject<SoundEvent> BAQSY_AMBIENT = sound("entity.baqsy_kolenkesi.ambient");
    public static final RegistryObject<SoundEvent> DAU_ROAR = sound("entity.dau.roar");
    public static final RegistryObject<SoundEvent> DAU_SLAM = sound("entity.dau.slam");
    public static final RegistryObject<SoundEvent> DAU_HIT = sound("entity.dau.hit");
    public static final RegistryObject<SoundEvent> DAU_AMBIENT = sound("entity.dau.ambient");
    public static final RegistryObject<SoundEvent> DAU_HURT = sound("entity.dau.hurt");
    public static final RegistryObject<SoundEvent> DAU_DEATH = sound("entity.dau.death");
    public static final RegistryObject<SoundEvent> SAIGA_AMBIENT = sound("entity.aqboken.ambient");
    public static final RegistryObject<SoundEvent> SAIGA_HURT = sound("entity.aqboken.hurt");
    public static final RegistryObject<SoundEvent> SAIGA_DEATH = sound("entity.aqboken.death");
    public static final RegistryObject<SoundEvent> BARYS_AMBIENT = sound("entity.barys.ambient");
    public static final RegistryObject<SoundEvent> BARYS_ANGRY = sound("entity.barys.angry");
    public static final RegistryObject<SoundEvent> BARYS_HURT = sound("entity.barys.hurt");
    public static final RegistryObject<SoundEvent> BARYS_DEATH = sound("entity.barys.death");
    public static final RegistryObject<SoundEvent> KORKYT_KUI = sound("entity.qorqyt_ata.kui");
    public static final RegistryObject<SoundEvent> KORKYT_VANISH = sound("entity.qorqyt_ata.vanish");

    // v7: звуки
    public static final RegistryObject<SoundEvent> AIDAHAR_ROAR = sound("entity.aidahar.roar");
    public static final RegistryObject<SoundEvent> AIDAHAR_BITE = sound("entity.aidahar.bite");
    public static final RegistryObject<SoundEvent> AIDAHAR_FIRE = sound("entity.aidahar.fire");
    public static final RegistryObject<SoundEvent> AIDAHAR_AMBIENT = sound("entity.aidahar.ambient");
    public static final RegistryObject<SoundEvent> AIDAHAR_HURT = sound("entity.aidahar.hurt");
    public static final RegistryObject<SoundEvent> AIDAHAR_DEATH = sound("entity.aidahar.death");
    public static final RegistryObject<SoundEvent> AKSAKAL_TALK = sound("entity.aqsaqal.talk");
    public static final RegistryObject<SoundEvent> TOGYZ_MOVE = sound("block.togyz.move");
    public static final RegistryObject<SoundEvent> SINGING_SAND = sound("block.an_qum.sing");
    public static final RegistryObject<SoundEvent> PETROGLYPH = sound("block.tanba.found");

    // v7: кюй Курмангазы (20 частей) и пластинка «Адай»
    public static final java.util.List<RegistryObject<SoundEvent>> KURMANGAZY_KUI = kuiParts();
    public static final RegistryObject<SoundEvent> MUSIC_DISC_ADAI = sound("music_disc.adai");

    private static java.util.List<RegistryObject<SoundEvent>> kuiParts() {
        java.util.List<RegistryObject<SoundEvent>> list = new java.util.ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add(sound("item.dombyra.kurmangazy." + i));
        }
        return java.util.List.copyOf(list);
    }

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name)));
    }
}
