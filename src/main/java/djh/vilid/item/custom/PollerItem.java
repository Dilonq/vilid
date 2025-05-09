package djh.vilid.item.custom;

import djh.vilid.mixin.VillagerEntityMixin;
import djh.vilid.villager.VillagerExt;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerProfession;

public class PollerItem extends Item {
    public PollerItem(Settings settings){
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient()){
            if (entity instanceof VillagerEntity){
                VillagerExt ext = (VillagerExt) entity;

                user.sendMessage(Text.literal("yes a villager"));

                user.sendMessage(Text.literal("my name is "+ext.getLegalName()));

//                user.sendMessage(Text.literal("i am "+villager.getViewpoint().getRigidity()+"% confident in "+villager.getViewpoint().getIdeology().toString()));

                String job = ((VillagerEntity)ext).getVillagerData().getProfession().toString();
                user.sendMessage(Text.literal("i am a "+job));

                user.sendMessage(Text.literal("happy : "+ext.getIdeology().happy));

                user.sendMessage(Text.literal("social : "+ext.getIdeology().social));
                user.sendMessage(Text.literal("economic : "+ext.getIdeology().economic));
                user.sendMessage(Text.literal("governmental : "+ext.getIdeology().governmental));

                return ActionResult.SUCCESS;
            }else{
                user.sendMessage(Text.literal("Not a villager"));
                return ActionResult.FAIL;
            }
        }else{
            return ActionResult.PASS;
        }
    }
}
