package kz.ezhelgi.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

/** Казахская кухня. */
public final class ModFoods {
    private ModFoods() {}

    // Бешбармақ: самое сытное блюдо, даёт регенерацию
    public static final FoodProperties BESHBARMAK = new FoodProperties.Builder()
            .nutrition(12).saturationModifier(1.0f).build();
    public static final Consumable BESHBARMAK_EAT = Consumables.defaultFood()
            .consumeSeconds(2.4f)
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SATURATION, 20, 0), 1.0f))
            .build();

    // Бауырсақ: быстрые пончики
    public static final FoodProperties BAURSAK = new FoodProperties.Builder()
            .nutrition(4).saturationModifier(0.5f).build();
    public static final Consumable BAURSAK_EAT = Consumables.defaultFood()
            .consumeSeconds(1.0f).build();

    // Құрт: сушёный творог, можно есть всегда, даёт скорость
    public static final FoodProperties KURT = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable KURT_EAT = Consumables.defaultFood()
            .consumeSeconds(0.8f)
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 400, 0), 1.0f))
            .build();

    // Қазы: конская колбаса
    public static final FoodProperties KAZY = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.9f).build();
    public static final Consumable KAZY_EAT = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.ABSORPTION, 600, 0), 1.0f))
            .build();

    // Қымыз: сила батыра (и немного кружится голова)
    public static final FoodProperties KUMYS = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.4f).alwaysEdible().build();
    public static final Consumable KUMYS_DRINK = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.STRENGTH, 1200, 0), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.SPEED, 1200, 0), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.NAUSEA, 160, 0), 0.15f))
            .build();

    // Шұбат: верблюжье молоко, стойкость
    public static final FoodProperties SHUBAT = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.4f).alwaysEdible().build();
    public static final Consumable SHUBAT_DRINK = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.RESISTANCE, 1200, 0), 1.0f))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0), 1.0f))
            .build();

    // Жылқы еті — конина (сырая и жареная)
    public static final FoodProperties JYLQY_ETI = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3f).build();
    public static final FoodProperties PISKEN_JYLQY_ETI = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.9f).build();
    // Ақбөкен еті — мясо сайгака
    public static final FoodProperties AQBOKEN_ETI = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.3f).build();
    public static final FoodProperties PISKEN_AQBOKEN_ETI = new FoodProperties.Builder()
            .nutrition(7).saturationModifier(0.8f).build();
    /** Мясо дичи: архар, джейран, тау-теке. */
    public static final FoodProperties AN_ETI = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.3f).build();
    public static final FoodProperties PISKEN_AN_ETI = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.85f).build();
    public static final Consumable MEAT_EAT = Consumables.defaultFood().build();

    // ---------- v8: кухня аула ----------
    private static ApplyStatusEffectsConsumeEffect fx(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> e, int sec, int amp) {
        return new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(e, sec * 20, amp), 1.0f);
    }

    /** Нан из тандыра. */
    public static final FoodProperties NAN = new FoodProperties.Builder().nutrition(6).saturationModifier(0.7f).build();
    public static final Consumable NAN_EAT = Consumables.defaultFood().consumeSeconds(1.2f).build();
    /** Самса — пирожок с мясом из тандыра: сытно и быстро. */
    public static final FoodProperties SAMSA = new FoodProperties.Builder().nutrition(8).saturationModifier(0.9f).build();
    public static final Consumable SAMSA_EAT = Consumables.defaultFood().consumeSeconds(1.0f).onConsume(fx(MobEffects.ABSORPTION, 60, 0)).build();
    /** Қақ ет — вяленое мясо: в дорогу. */
    public static final FoodProperties QAQ_ET = new FoodProperties.Builder().nutrition(6).saturationModifier(1.0f).build();
    /** Сорпа — горячий бульон: лечит и греет. */
    public static final FoodProperties SORPA = new FoodProperties.Builder().nutrition(6).saturationModifier(0.8f).alwaysEdible().build();
    public static final Consumable SORPA_EAT = Consumables.defaultDrink().onConsume(fx(MobEffects.REGENERATION, 20, 1))
            .onConsume(fx(MobEffects.HEALTH_BOOST, 180, 0)).build();
    /** Қуырдақ — жаркое: сила. */
    public static final FoodProperties QUYRDAQ = new FoodProperties.Builder().nutrition(10).saturationModifier(0.9f).build();
    public static final Consumable QUYRDAQ_EAT = Consumables.defaultFood().onConsume(fx(MobEffects.STRENGTH, 180, 0)).build();
    /** Палау — плов: долгая сытость и защита. */
    public static final FoodProperties PALAU = new FoodProperties.Builder().nutrition(12).saturationModifier(1.1f).build();
    public static final Consumable PALAU_EAT = Consumables.defaultFood().onConsume(fx(MobEffects.ABSORPTION, 120, 1))
            .onConsume(fx(MobEffects.RESISTANCE, 120, 0)).build();
    /** Наурыз-көже из семи продуктов: удача и благополучие на весь день. */
    public static final FoodProperties NAURYZ_KOZHE = new FoodProperties.Builder().nutrition(10).saturationModifier(1.2f).alwaysEdible().build();
    public static final Consumable NAURYZ_KOZHE_EAT = Consumables.defaultDrink().onConsume(fx(MobEffects.LUCK, 600, 1))
            .onConsume(fx(MobEffects.REGENERATION, 60, 0)).onConsume(fx(MobEffects.HERO_OF_THE_VILLAGE, 600, 0)).build();
    /** Қара шай — чёрный чай: бодрость. */
    public static final FoodProperties QARA_SHAI = new FoodProperties.Builder().nutrition(1).saturationModifier(0.2f).alwaysEdible().build();
    public static final Consumable QARA_SHAI_DRINK = Consumables.defaultDrink().onConsume(fx(MobEffects.HASTE, 120, 0)).build();
    /** Сүтті шай — чай с молоком из самауыра: бодрость и меньше голода. */
    public static final FoodProperties SUTTI_SHAI = new FoodProperties.Builder().nutrition(4).saturationModifier(1.2f).alwaysEdible().build();
    public static final Consumable SUTTI_SHAI_DRINK = Consumables.defaultDrink().onConsume(fx(MobEffects.HASTE, 300, 0))
            .onConsume(fx(MobEffects.SATURATION, 1, 0)).build();
}
