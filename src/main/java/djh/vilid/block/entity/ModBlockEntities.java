package djh.vilid.block.entity;

import djh.vilid.Vilid;
import djh.vilid.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModBlockEntities {

//    public static final BlockEntityType<TributeChestBlockEntity> TRIBUTE_CHEST =
//            Registry.register(
//                    Registries.BLOCK_ENTITY_TYPE,
//                    Identifier.of(Vilid.MOD_ID, "tribute_chest"),
//                    FabricBlockEntityTypeBuilder.create(
//                            // This instantiates your updated TributeChestBlockEntity
//                            (pos, state) -> new TributeChestBlockEntity(ModBlockEntities.TRIBUTE_CHEST, pos, state),
//                            // Links it to your barrel-style TributeChestBlock
//                            ModBlocks.TRIBUTE_CHEST
//                    ).build()
//            );

//    public static final BlockEntityType<BallotBoxBlockEntity> BALLOT_BOX =
//            Registry.register(
//                    Registries.BLOCK_ENTITY_TYPE,
//                    new Identifier(Vilid.MOD_ID, "ballot_box"),
//                    FabricBlockEntityTypeBuilder.create(
//                            (pos, state) -> new BallotBoxBlockEntity(ModBlockEntities.BALLOT_BOX, pos, state),
//                            ModBlocks.BALLOT_BOX
//                    ).build()
//            );

    public static void registerModBlockEntities() {
        Vilid.LOGGER.info("Registering ModBlockEntities for " + Vilid.MOD_ID);
    }
}