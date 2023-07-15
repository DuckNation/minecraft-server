package io.github.haappi.NPC;

import net.minestom.server.item.Enchantment;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class ItemPojo {
    private final String material;
    private final boolean enchanted;

    public ItemPojo(String material, boolean enchanted) {
        this.material = material;
        this.enchanted = enchanted;
    }

    public String getMaterial() {
        return material;
    }

    public boolean isEnchanted() {
        return enchanted;
    }

    public ItemStack build() {
        if (!enchanted) {
            return ItemStack.of(Material.fromNamespaceId(material));
        }
        return ItemStack.builder(Material.fromNamespaceId(material))
                .meta(metaBuilder ->
                        metaBuilder.enchantment(Enchantment.EFFICIENCY, (short) 1)
                )
                .build();
    }
}
