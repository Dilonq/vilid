package djh.vilid.trade;

import djh.vilid.item.ModItems;
import djh.vilid.villager.ModProfessions;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

public class ModCustomTrades {
    public static void registerCustomTrades(){

        //capitalist trades (depends on level, determined by # of employees)
        addTrade(ModProfessions.CAPITALIST, 1, new ItemStack(Items.EMERALD, 1), new ItemStack(Items.WRITABLE_BOOK, 1), 12, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 1, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.PAPER, 12), 16, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 2, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.GLASS, 4), 12, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 2, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.REDSTONE, 2), 20, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 2, new ItemStack(Items.EMERALD, 4), new ItemStack(Items.CLOCK, 1), 3, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 2, new ItemStack(Items.EMERALD, 10), new ItemStack(Items.NAME_TAG, 1), 4, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 2, new ItemStack(Items.EMERALD, 2), new ItemStack(Items.COAL, 8), 16, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 2, new ItemStack(Items.EMERALD, 4), new ItemStack(Items.GOLDEN_CARROT, 3), 3, 0, 1);

        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 4), new ItemStack(Items.GOLD_INGOT, 1), 8, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 10), new ItemStack(Items.MINECART, 1), 3, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.IRON_BLOCK, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 4), new ItemStack(Items.ENDER_PEARL, 1), 2, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 14), new ItemStack(Items.IRON_CHESTPLATE, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 12), new ItemStack(Items.IRON_LEGGINGS, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 8), new ItemStack(Items.IRON_BOOTS, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 10), new ItemStack(Items.IRON_HELMET, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 3, new ItemStack(Items.EMERALD, 36), new ItemStack(Items.BELL, 1), 1, 0, 1);

        addTrade(ModProfessions.CAPITALIST, 4, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.GOLDEN_APPLE, 1), 2, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 4, new ItemStack(Items.EMERALD, 8), new ItemStack(Items.DIAMOND, 1), 4, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 4, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.DIAMOND_PICKAXE, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 4, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.DIAMOND_SWORD, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 4, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.DIAMOND_SHOVEL, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 4, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.DIAMOND_AXE, 1), 1, 0, 1);


        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 48), new ItemStack(Items.TRIDENT, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 64), new ItemStack(Items.DIAMOND_BLOCK, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 64), new ItemStack(Items.TOTEM_OF_UNDYING, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.SPONGE, 1), 3, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 3), new ItemStack(Items.EXPERIENCE_BOTTLE, 8), 2, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 20), new ItemStack(Items.DIAMOND_CHESTPLATE, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 16), new ItemStack(Items.DIAMOND_LEGGINGS, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 14), new ItemStack(Items.DIAMOND_BOOTS, 1), 1, 0, 1);
        addTrade(ModProfessions.CAPITALIST, 5, new ItemStack(Items.EMERALD, 12), new ItemStack(Items.DIAMOND_HELMET, 1), 1, 0, 1);

        //old teacher content
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 1,
//                factories -> {
//                    factories.add(((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD,7),
//                            new ItemStack(Items.BOOK,1),
//                            1,1,0.05f
//                    )));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 1,
//                factories -> {
//                    factories.add(((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD,12),
//                            new ItemStack(ModItems.POLLER,1),
//                            1,1,0.05f
//                    )));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 2,
//                factories -> {
//                    factories.add((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD, 12),
//                            new ItemStack(ModItems.BADITEM, 1),
//                            3, 5, 0.1f
//                    ));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 2,
//                factories -> {
//                    factories.add((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD, 12),
//                            new ItemStack(ModItems.GOODITEM, 1),
//                            3, 5, 0.1f
//                    ));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 3,
//                factories -> {
//                    factories.add((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD, 16),
//                            new ItemStack(ModItems.LEFTITEM, 1),
//                            3, 5, 0.1f
//                    ));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 3,
//                factories -> {
//                    factories.add((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD, 16),
//                            new ItemStack(ModItems.RIGHTITEM, 1),
//                            3, 5, 0.1f
//                    ));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 4,
//                factories -> {
//                    factories.add((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD, 48),
//                            new ItemStack(ModItems.DUMBITEM, 1),
//                            3, 5, 0.1f
//                    ));
//                });
//        TradeOfferHelper.registerVillagerOffers(ModProfessions.TEACHER, 4,
//                factories -> {
//                    factories.add((entity, random) -> new TradeOffer(
//                            new ItemStack(Items.EMERALD, 20),
//                            new ItemStack(ModItems.EDUCATORITEM, 1),
//                            3, 5, 0.1f
//                    ));
//                });
    }

    private static void addTrade(VillagerProfession profession, int level, ItemStack price, ItemStack product, int maxUses, int merchantExperience, float priceMultiplier) {
        TradeOfferHelper.registerVillagerOffers(profession, level,
                factories -> {
                    factories.add(((entity, random) -> new TradeOffer(
                            // Extract the item and count into the new 1.21 TradedItem format
                            new TradedItem(price.getItem(), price.getCount()),
                            product,
                            maxUses,
                            merchantExperience,
                            priceMultiplier
                    )));
                });
    }
}
