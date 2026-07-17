package djh.vilid.gui;

import djh.vilid.ideology.politics.Ideology;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PollerScreenHandler extends ScreenHandler {

    public final int villagerId;
    public final int villagerHappiness;
    public final Ideology ideology;
    public final List<Text> lines = new ArrayList<>();
    public final List<Text> moodLines = new ArrayList<>();


    public static final ScreenHandlerType<PollerScreenHandler> TYPE = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of("vilid", "poller"),
            new ExtendedScreenHandlerType<>(
                    (syncId, inv, data) -> new PollerScreenHandler(syncId, inv, data),
                    PollerData.CODEC
            )
    );


    public PollerScreenHandler(int syncId, PlayerInventory inv, PollerData data) {
        super(TYPE, syncId);

        // 1. Grab primitives directly
        this.villagerId = data.villagerId();
        this.villagerHappiness = data.happiness();

        // 2. Convert the string back into your Ideology enum
        this.ideology = Ideology.valueOf(data.ideology());

        // 3. Convert the list of Strings into a list of Text elements
        for (String line : data.lines()) {
            this.lines.add(Text.literal(line));
        }

        for (String moodLine : data.moodLines()) {
            this.moodLines.add(Text.literal(moodLine));
        }
    }

    public static void register() {
        //dummy
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
