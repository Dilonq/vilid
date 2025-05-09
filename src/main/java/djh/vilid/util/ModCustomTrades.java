package djh.vilid.util;

import djh.vilid.villager.ModProfessions;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;

public class ModCustomTrades {
    public static void registerCustomTrades(){
        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 1,
                factories -> {
                    factories.add(((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD,7),
                            new ItemStack(Items.BOOK,1),
                            1,1,0.05f
                    )));
                });
    }
}
