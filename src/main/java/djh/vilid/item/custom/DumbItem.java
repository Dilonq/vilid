package djh.vilid.item.custom;

import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerProfession;

public class DumbItem extends Item {
    public DumbItem(Settings settings){
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient()){
            if (entity instanceof VillagerEntity){
                VillagerEntity villager = (VillagerEntity) entity;
                VillagerExt ext = (VillagerExt) entity;

                if (!villager.getVillagerData().getProfession().equals(VillagerProfession.NITWIT)) {
                    villager.setVillagerData(villager.getVillagerData().withProfession(VillagerProfession.NITWIT));
                    if (!user.isCreative()){stack.decrement(1);}
                }

                if (!user.isCreative()){stack.decrement(1);}

                return ActionResult.SUCCESS;
            }else{
                return ActionResult.FAIL;
            }
        }else{
            return ActionResult.PASS;
        }
    }
}
