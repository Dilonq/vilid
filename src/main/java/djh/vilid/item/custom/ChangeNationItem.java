package djh.vilid.item.custom;

import djh.vilid.nation.NationData;
import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class ChangeNationItem extends Item {
    public ChangeNationItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient()) {
            if (entity instanceof VillagerEntity villager) {
                if (!(villager instanceof VillagerExt ext)) {
                    user.sendMessage(Text.literal("This villager does not support nation data!"), false);
                    return ActionResult.FAIL;
                }

                // Get custom name of the item
                String newNation = stack.getName().getString();

                if (newNation == null || newNation.isBlank()) {
                    user.sendMessage(Text.literal("This item has no name!"), false);
                    return ActionResult.FAIL;
                }

                // Set in memory
                ext.getViewpoint().setNation(newNation);

                // Persist to NationData
                ServerWorld world = (ServerWorld) user.getWorld();
                NationData data = NationData.get(world);

                data.updateVillager(
                        villager.getUuid(),
                        newNation,
                        ext.getViewpoint().getHappy(),
                        ext.getViewpoint().getIdeology()
                );

                user.sendMessage(Text.literal("Set villager's nation to: " + newNation), false);
                return ActionResult.SUCCESS;
            } else {
                user.sendMessage(Text.literal("That’s not a villager!"), false);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }
}
