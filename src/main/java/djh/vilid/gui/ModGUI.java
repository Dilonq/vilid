package djh.vilid.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.util.Identifier;
import net.fabricmc.loader.api.FabricLoader;

public class ModGUI {
    public static void init() {
        // 1. Common registration (Loads for both Singleplayer and Multiplayer)
        PollerScreenHandler.register();

        // 2. Client-only registration
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            registerClientScreens();
        }
    }

    // By hiding the visual Screen here, the Server never attempts to load it,
    // which completely prevents the NoClassDefFoundError!
    private static void registerClientScreens() {
        HandledScreens.register(PollerScreenHandler.TYPE, PollerScreen::new);
    }
}