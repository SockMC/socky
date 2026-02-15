package net.sockmc.socky;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class Blocks {
    public static final Block AMBROSE_BLOCK = register(Ids.AMBROSE, StuffyBlock::new, AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque()                  // otherwise face is missing on neighboring blocks
    );
    public static final Block MR_OLIVE_BLOCK = register(Ids.MR_OLIVE, StuffyBlock::new, AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque()                  // otherwise face is missing on neighboring blocks
    );
    public static final Block SOCKY_BALE = register(Ids.SOCKY_BALE, PillarBlock::new, AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
    );
    public static final Block SOCKY_BLOCK = register(Ids.SOCKY, StuffyBlock::new, AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque()                  // otherwise face is missing on neighboring blocks
    );

    public static void initialize()
    {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(itemGroup ->{
            itemGroup.add(AMBROSE_BLOCK.asItem());
            itemGroup.add(MR_OLIVE_BLOCK.asItem());
            itemGroup.add(SOCKY_BALE.asItem());
            itemGroup.add(SOCKY_BLOCK.asItem());
        });
    }

    private static Block register(Identifier id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
        Registry.register(Registries.ITEM, itemKey, blockItem);
        return Registry.register(Registries.BLOCK, blockKey, block);
    }
}
