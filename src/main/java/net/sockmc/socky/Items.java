package net.sockmc.socky;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Items {
    public static final Item SOCKY_BODY = new Item(new Item.Settings().maxCount(16));
    public static final Item SOCKY_CAPE = new Item(new Item.Settings().maxCount(16));
    public static final Item SOCKY_PANTS = new Item(new Item.Settings().maxCount(16));
    public static final Item SOCKY_SCARF = new Item(new Item.Settings().maxCount(16));

    public static final BlockItem AMBROSE = new BlockItem(Blocks.AMBROSE_BLOCK, new Item.Settings().maxCount(16));
    public static final BlockItem SOCKY = new BlockItem(Blocks.SOCKY_BLOCK, new Item.Settings().maxCount(16));
    public static final BlockItem SOCKY_BALE = new BlockItem(Blocks.SOCKY_BALE, new Item.Settings());

    public static void initialize() {
        Registry.register(Registries.ITEM, Ids.SOCKY_BODY, Items.SOCKY_BODY);
        Registry.register(Registries.ITEM, Ids.SOCKY_CAPE, Items.SOCKY_CAPE);
        Registry.register(Registries.ITEM, Ids.SOCKY_PANTS, Items.SOCKY_PANTS);
        Registry.register(Registries.ITEM, Ids.SOCKY_SCARF, Items.SOCKY_SCARF);

        Registry.register(Registries.ITEM, Ids.AMBROSE, Items.AMBROSE);
        Registry.register(Registries.ITEM, Ids.SOCKY, Items.SOCKY);
        Registry.register(Registries.ITEM, Ids.SOCKY_BALE, Items.SOCKY_BALE);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup ->{
            itemGroup.add(Items.SOCKY_BODY);
            itemGroup.add(Items.SOCKY_CAPE);
            itemGroup.add(Items.SOCKY_PANTS);
            itemGroup.add(Items.SOCKY_SCARF);
        });
    }
}
