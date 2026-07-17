package djh.vilid.mixin;

import djh.vilid.nation.NationData;
import djh.vilid.villager.VillagerExt;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SpawnEggItem.class)
public abstract class VillagerEggMixin {

//    @Inject(method = "spawnBaby", at = @At("HEAD"), cancellable = true)
//    private void disableBabySpawn(PlayerEntity user, MobEntity entity, EntityType<? extends MobEntity> entityType, ServerWorld world, Vec3d pos, ItemStack stack, CallbackInfoReturnable<Optional<MobEntity>> cir) {
//        if (entity instanceof VillagerEntity) {
//            user.sendMessage(Text.literal("You cannot use a spawn egg to create a baby villager."), false);
//            cir.setReturnValue(Optional.empty());
//        }
//    }



//    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
//    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
//        ItemStack stack = context.getStack();
//        World world = context.getWorld();
//
//        // Only run on the server
//        if (world.isClient()) {
//            return;
//        }
//
//        ServerPlayerEntity player = (ServerPlayerEntity) context.getPlayer();
//        BlockPos spawnPos = context.getBlockPos().offset(context.getSide());
//
//        if (!(stack.getItem() instanceof SpawnEggItem egg)) return;
//
//        // Check if this is a villager egg
//        if (egg.getEntityType(null) != EntityType.VILLAGER) return;
//
//        // Retrieve the custom name component directly
//        Text nameComponent = stack.get(DataComponentTypes.CUSTOM_NAME);
//// Convert it to a String if it exists, otherwise keep it null
//        String customName = nameComponent != null ? nameComponent.getString() : null;
//
//        if (customName == null || customName.isEmpty()) {
//            player.sendMessage(Text.literal("You must /rename the egg to a registered nation before using it."), false);
//            cir.setReturnValue(ActionResult.FAIL);
//            return;
//        }
//
//        if (!NationData.get((ServerWorld) world).contains(customName)) {
//            player.sendMessage(Text.literal("Nation '" + customName + "' is not registered. Use /registernation first."), false);
//            cir.setReturnValue(ActionResult.FAIL);
//            return;
//        }
//
//        VillagerEntity villager = EntityType.VILLAGER.create(
//                (ServerWorld) world,
//                // The 'null' NbtCompound argument used to be here, but was removed in 1.21
//                v -> {
////            v.setBaby(false); //force adult
//                    VillagerExt ext = (VillagerExt) v;
//                    ext.genBaseNBT(v.getVillagerData().getProfession());
//                    ext.getViewpoint().setNation(customName);
//                    v.setCustomName(null); // hide nametag
//                },
//                spawnPos,
//                SpawnReason.SPAWN_EGG,
//                true,
//                false
//        );
//
//        if (villager == null) {
//            player.sendMessage(Text.literal("Failed to spawn villager at: " + spawnPos), false);
//            cir.setReturnValue(ActionResult.FAIL);
//            return;
//        }
//
//        world.spawnEntity(villager);
//
//        if (!player.isCreative()) {
//            stack.decrement(1);
//        }
//
//        player.sendMessage(Text.literal("Spawned villager for nation: " + customName), false);
//        cir.setReturnValue(ActionResult.SUCCESS);
//
//
//        player.sendMessage(Text.literal("Attempting to spawn villager at: " + spawnPos), false);
//        player.sendMessage(Text.literal("Spawned villager for nation: " + customName), false);
//        cir.setReturnValue(ActionResult.SUCCESS);
//    }
}
