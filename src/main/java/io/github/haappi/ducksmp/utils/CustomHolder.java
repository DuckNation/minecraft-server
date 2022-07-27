package io.github.haappi.ducksmp.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomHolder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        throw new RuntimeException("Trying to getInventory() on a custom holder!");
    }
}
