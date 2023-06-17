package io.github.haappi.duckpaper.enchants;


import org.bukkit.enchantments.EnchantmentTarget;

public @interface CustomEnchantment {
    // https://github.com/oddlama/vane/blob/ace0e15bb30060ffc43406840951cefe9ac250ca/vane-annotations/src/main/java/org/oddlama/vane/annotation/enchantment/VaneEnchantment.java#L11
    String name();

    int maxLevel() default 1;

    Rarity rarity() default Rarity.COMMON;

    boolean curse() default false;

    boolean tradeable() default false;

    boolean treasure() default false;

    boolean generateInTreasure() default false;

    EnchantmentTarget target() default EnchantmentTarget.BREAKABLE;

    boolean allowCustom() default false;
}
