package net.sockmc.socky;

import java.util.function.Function;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class Items {
    public static final Item SOCKY_BODY = register(Ids.SOCKY_BODY, Item::new, new Item.Settings().maxCount(16));
    public static final Item SOCKY_CAPE = register(Ids.SOCKY_CAPE, Item::new, new Item.Settings().maxCount(16));
    public static final Item SOCKY_PANTS = register(Ids.SOCKY_PANTS, Item::new, new Item.Settings().maxCount(16));
    public static final Item SOCKY_SCARF = register(Ids.SOCKY_SCARF, Item::new, new Item.Settings().maxCount(16));

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(itemGroup ->{
            itemGroup.add(Items.SOCKY_BODY);
            itemGroup.add(Items.SOCKY_CAPE);
            itemGroup.add(Items.SOCKY_PANTS);
            itemGroup.add(Items.SOCKY_SCARF);
        });
    }

    private static Item register(Identifier id, Function<Item.Settings, Item> itemFactory, Item.Settings settings)
    {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }
}
