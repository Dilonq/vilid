package djh.vilid.block;

import djh.vilid.Vilid;
import djh.vilid.block.custom.BallotBoxBlock;
import djh.vilid.block.custom.TributeChestBlock;
import djh.vilid.block.entity.ModBlockEntities;
import djh.vilid.item.custom.LeftItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block DESK = registerBlock("desk",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block TRIBUTE_CHEST = registerBlock("tribute_chest",
            new TributeChestBlock(
                    FabricBlockSettings.copyOf(Blocks.BARREL),
                    () -> ModBlockEntities.TRIBUTE_CHEST
            ));

    public static final Block BALLOT_BOX = registerBlock("ballot_box",
            new BallotBoxBlock(
                    FabricBlockSettings.copyOf(Blocks.BARREL),
                    () -> ModBlockEntities.BALLOT_BOX
            ));



    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Vilid.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(Vilid.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks(){
        Vilid.LOGGER.info("Registering ModBlocks for "+Vilid.MOD_ID);
    }
}
