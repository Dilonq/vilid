package djh.vilid.item;

import djh.vilid.Vilid;
import djh.vilid.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup COM_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Vilid.MOD_ID, "com"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.vilid.vilid"))
                    .icon(() -> new ItemStack(ModItems.POLLER)).entries((displayContext, entries) -> {
                        entries.add(ModItems.POLLER);
                        entries.add(ModItems.LEFTITEM);
                        entries.add(ModItems.RIGHTITEM);
                        entries.add(ModItems.GOODITEM);
                        entries.add(ModItems.BADITEM);
                        entries.add(ModItems.DUMBITEM);
                        entries.add(ModItems.EDUCATORITEM);
                        entries.add(ModBlocks.DESK);
                    }).build());


    public static void registerItemGroups() {
        Vilid.LOGGER.info("Registering Item Groups for " + Vilid.MOD_ID);
    }
}