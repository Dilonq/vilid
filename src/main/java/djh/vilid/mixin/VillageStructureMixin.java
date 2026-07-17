//package djh.vilid.mixin;
//
//import net.minecraft.entity.passive.VillagerEntity;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.BlockPos;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import djh.vilid.villager.VillagerExt;
//
//@Mixin(TemplateStructurePiece.class)
//public abstract class TemplateStructurePieceMixin {
//    @Inject(method = "handleMetadata", at = @At("HEAD"))
//    private void onHandleMetadata(String metadata, BlockPos pos, ServerWorld world, CallbackInfo ci) {
//        if (metadata.equalsIgnoreCase("villager")) {
//            // Example: detect biome and assign nation tag accordingly
//            var biome = world.getBiome(pos).value();
//            String nation;
//            if (biome.getCategory() == Biome.Category.DESERT) {
//                nation = "desert_nation";
//            } else if (biome.getCategory() == Biome.Category.PLAINS) {
//                nation = "plains_nation";
//            } else {
//                nation = "default_nation";
//            }
//            TemplateStructurePiece self = (TemplateStructurePiece) (Object) this;
//            for (StructureEntityInfo entityInfo : self.getEntityInfos()) {
//                if (entityInfo.nbt.getString("id").equals("minecraft:villager")) {
//                    entityInfo.nbt.putString("vilid_nation", nation);
//                }
//            }
//        }
//    }
//}
