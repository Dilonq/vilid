package djh.vilid.item;

import djh.vilid.item.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import djh.vilid.Vilid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item LEFTITEM = registerItem("left",new LeftItem(new Item.Settings().maxCount(16)));
    public static final Item RIGHTITEM = registerItem("right",new RightItem(new Item.Settings().maxCount(16)));
    public static final Item GOODITEM = registerItem("good",new GoodItem(new Item.Settings().maxCount(16)));
    public static final Item BADITEM = registerItem("bad",new BadItem(new Item.Settings().maxCount(16)));
    public static final Item EDUCATORITEM = registerItem("educator", new EducatorItem(new Item.Settings().maxCount(16)));
    public static final Item DUMBITEM = registerItem("dumbifyer", new DumbItem(new Item.Settings().maxCount(16)));

    public static final Item CHANGENATIONITEM = registerItem("changenationitem",new ChangeNationItem(new Item.Settings().maxCount(1)));
    public static final Item POLLER = registerItem("poller",new PollerItem(new Item.Settings()));
    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries) {
//        entries.add(RUBY);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Vilid.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Vilid.LOGGER.info("Registering Mod Items for " + Vilid.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
