package net.sockmc.socky;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class Blocks {
    public static final Block AMBROSE_BLOCK = new StuffyBlock(AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque()                  // otherwise face is missing on neighboring blocks
    );
    public static final Block MR_OLIVE_BLOCK = new StuffyBlock(AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque()                  // otherwise face is missing on neighboring blocks
    );
    public static final Block SOCKY_BALE = new PillarBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOL));
    public static final Block SOCKY_BLOCK = new StuffyBlock(AbstractBlock.Settings.create()
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque()                  // otherwise face is missing on neighboring blocks
    );

    public static void initialize()
    {
        Registry.register(Registries.BLOCK, Ids.AMBROSE, AMBROSE_BLOCK);
        Registry.register(Registries.BLOCK, Ids.MR_OLIVE, MR_OLIVE_BLOCK);
        Registry.register(Registries.BLOCK, Ids.SOCKY_BALE, SOCKY_BALE);
        Registry.register(Registries.BLOCK, Ids.SOCKY, SOCKY_BLOCK);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(itemGroup ->{
            itemGroup.add(AMBROSE_BLOCK.asItem());
            itemGroup.add(MR_OLIVE_BLOCK.asItem());
            itemGroup.add(SOCKY_BALE.asItem());
            itemGroup.add(SOCKY_BLOCK.asItem());
        });
    }
}
