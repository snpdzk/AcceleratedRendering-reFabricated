package com.github.argon4w.acceleratedrendering.features.items;

import com.github.argon4w.acceleratedrendering.core.mixins.MCItemColorsAccessor;
import com.github.argon4w.acceleratedrendering.features.items.mixins.ItemColorsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

public class AcceleratedItemRenderContext {

    private final ItemStack itemStack;
    private final ItemColor itemColor;
    private final BakedModel bakedModel;
    private final RandomSource random;

    public AcceleratedItemRenderContext(
            ItemStack itemStack,
            BakedModel bakedModel,
            RandomSource random
    ) {
        this.itemStack = itemStack;
        this.itemColor = getItemColorOrDefault(itemStack);
        this.bakedModel = bakedModel;
        this.random = random;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemColor getItemColor() {
        return itemColor;
    }

    public BakedModel getBakedModel() {
        return bakedModel;
    }

    public RandomSource getRandom() {
        return random;
    }

    private static ItemColor getItemColorOrDefault(ItemStack itemStack) {
        MCItemColorsAccessor accessor = (MCItemColorsAccessor) Minecraft.getInstance();
        int id = BuiltInRegistries.ITEM.getId(itemStack.getItem());
        ItemColor color = ((ItemColorsAccessor) (accessor.getItemColors())).getItemColors().byId(id);
        if (color == null) return EmptyItemColor.INSTANCE;
        return color;
    }
}
