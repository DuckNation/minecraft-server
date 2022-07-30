package io.github.haappi.ducksmp.listeners;

import io.github.haappi.ducksmp.DuckSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class StringRecipe {

    private final DuckSMP plugin;

    public StringRecipe() {
        this.plugin = DuckSMP.getInstance();


        Bukkit.addRecipe(woolRecipe());
        Bukkit.addRecipe(stringRecipe());
    }

    private ShapelessRecipe stringRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "string");
        ItemStack item = new ItemStack(Material.STRING, 4);
        ShapelessRecipe recipe = new ShapelessRecipe(key, item);

        recipe.addIngredient(Material.WHITE_WOOL);
        return recipe;
    }

    private ShapelessRecipe woolRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, "wool");
        ItemStack item = new ItemStack(Material.WHITE_WOOL);
        ShapelessRecipe recipe = new ShapelessRecipe(key, item);

        recipe.addIngredient(Material.STRING).addIngredient(Material.STRING).addIngredient(Material.STRING).addIngredient(Material.STRING);
        return recipe;
    }
}
