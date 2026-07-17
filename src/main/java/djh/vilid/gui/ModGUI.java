package djh.vilid.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import net.fabricmc.loader.api.FabricLoader;

public class ModGUI {
    public static void init() {
        // Common registration
        PollerScreenHandler.register();

        // Client-only registration
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            HandledScreens.register(PollerScreenHandler.TYPE, PollerScreen::new);
        }
    }
}
