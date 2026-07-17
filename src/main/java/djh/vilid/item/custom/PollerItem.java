package djh.vilid.item.custom;

import djh.vilid.gui.PollerData;
import djh.vilid.gui.PollerScreenHandler;
import djh.vilid.villager.ModProfessions;
import djh.vilid.villager.VillagerExt;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerProfession;

import java.util.ArrayList;
import java.util.List;

public class PollerItem extends Item {
    public PollerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient()) {
            if (entity instanceof VillagerEntity villager) {
                VillagerExt ext = (VillagerExt) villager;

                List<String> lines = new ArrayList<>();
                List<String> moodLines = new ArrayList<>();

                lines.add("NAME:");
                lines.add(ext.getLegalName().toUpperCase());

                lines.add("COUNTRY:");
                lines.add(ext.getViewpoint().getNation().toUpperCase());

                String job = villager.getVillagerData().getProfession().toString();
                lines.add("OCCUPATION:");
                lines.add(job.toUpperCase().replace("\"", "").replace("'", ""));

                lines.add("AFFILIATION:");
                lines.add(ext.getViewpoint().getIdeology().toString().toUpperCase());

                String status;
                if (ext.getViewpoint().getHappy() >= 80) {
                    status = "Happy";
                } else if (ext.getViewpoint().getHappy() >= 20) {
                    status = "Content";
                } else {
                    status = "Unhappy";
                }
                moodLines.add("Mood: " + status);

                if (ext.getMoodOutlook() > 0) {
                    moodLines.add("Outlook: Rising");
                } else if (ext.getMoodOutlook() == 0) {
                    moodLines.add("Outlook: Stable");
                } else {
                    moodLines.add("Outlook: Declining");
                }

                if (ext.getViewpoint().getMilitant()) moodLines.add("(x) I am militant");
                if (villager.getVillagerData().getProfession().equals(VillagerProfession.NONE)) moodLines.add("(-) I am unemployed");
                if (!ext.hasBed()) moodLines.add("(-) I am homeless");
                if (ext.getNearbyVillagerCount(8) > 6) moodLines.add("(-) I hate this crowd");
                if (job.equals("nitwit")) moodLines.add("(+) I am a nitwit");
                if (ext.hasMetToday()) moodLines.add("(+) I have been social today");
                if (ext.isIronGolemNearby(32)) moodLines.add("(+) I feel protected by the golem");
                if (!villager.getVillagerData().getProfession().equals(VillagerProfession.NONE) && !villager.getVillagerData().getProfession().equals(VillagerProfession.NITWIT) && !villager.getVillagerData().getProfession().equals(ModProfessions.WORKER))
                    moodLines.add("(+) I am self-employed");
                if (!villager.getVillagerData().getProfession().equals(VillagerProfession.NONE) && !villager.getVillagerData().getProfession().equals(VillagerProfession.NITWIT) && !villager.getVillagerData().getProfession().equals(ModProfessions.WORKER) && villager.getVillagerData().getLevel() >= 4)
                    moodLines.add("(+) Work is going well");

                // 1. Pack everything into your new record
                PollerData pollerData = new PollerData(
                        entity.getId(),
                        ext.getViewpoint().getHappy(),
                        ext.getViewpoint().getIdeology().name(), // Passed as a String to keep it simple
                        lines,
                        moodLines
                );

                // 2. Open the GUI with the new generic factory
                ((ServerPlayerEntity) user).openHandledScreen(new ExtendedScreenHandlerFactory<PollerData>() {
                    @Override
                    public PollerData getScreenOpeningData(ServerPlayerEntity player) {
                        return pollerData;
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.literal("Villager Survey");
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                        return new PollerScreenHandler(syncId, playerInventory, pollerData);
                    }
                });

                return ActionResult.SUCCESS;
            } else {
                user.sendMessage(Text.literal("Not a villager!"));
                return ActionResult.FAIL;
            }
        }
        return ActionResult.PASS;
    }
}