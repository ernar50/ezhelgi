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
    public static final Consumable MEAT_EAT = Consumables.defaultFood().build();
}
