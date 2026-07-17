package djh.vilid.gui;

import djh.vilid.ideology.politics.Ideology;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class PollerScreen extends HandledScreen<PollerScreenHandler> {
    private static final Identifier BG_TEXTURE =
            new Identifier("vilid", "textures/gui/villagerid.png");

    private static final int BG_WIDTH = 192;
    private static final int BG_HEIGHT = 112;
    private static final int TEXTURE_WIDTH = 1024;
    private static final int TEXTURE_HEIGHT = 256;

    public PollerScreen(PollerScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Draw the darkened game background
        this.renderBackground(context);

        // 2. Let HandledScreen draw its default stuff (slots, titles, etc.)
        super.render(context, mouseX, mouseY, delta);

        // 3. Draw your GUI background texture
        int deltaX = 0;//based on ideology, changes card design
        if (this.handler.ideology.alignment.isMostLeftWing()){
            deltaX+=193;
        }else if (this.handler.ideology.alignment.isMostRightWing()){
            deltaX+=(2*193);
        }
        context.drawTexture(
                BG_TEXTURE,
                this.x, this.y,
                deltaX, 0,
                BG_WIDTH, BG_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        // 4. Draw the smiley overlay depending on villager happiness
        int smileX = this.x + BG_WIDTH - 32 - 5;
        int smileY = this.y + BG_HEIGHT - 32 - 4;

        if (this.handler.villagerHappiness >= 80) {
            // happy
            context.drawTexture(BG_TEXTURE, smileX, smileY, 0, 224, 32, 32,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else if (this.handler.villagerHappiness >= 20) {
            // content
            context.drawTexture(BG_TEXTURE, smileX, smileY, 33, 224, 32, 32,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            // sad
            context.drawTexture(BG_TEXTURE, smileX, smileY, 66, 224, 32, 32,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        if (mouseX >= smileX && mouseX <= smileX + 32 &&
                mouseY >= smileY && mouseY <= smileY + 32) {

            context.drawTooltip(
                    this.textRenderer,
                    handler.moodLines, // List<Text>
                    mouseX, mouseY
            );
        }


        // 5. Draw the villager model
        Entity e = MinecraftClient.getInstance().world.getEntityById(this.handler.villagerId);
        if (e instanceof VillagerEntity villager) {
            InventoryScreen.drawEntity(
                    context,
                    this.x + 38, // screen X
                    this.y + 84, // screen Y
                    28, // scale
                    (float)(this.x + 38 - mouseX), // mouse X offset
                    (float)(this.y + 90 - mouseY), // mouse Y offset
                    villager
            );
        }

        //6. text
        int textY = this.y + 28; // starting Y position
        int leftX = this.x + 66; // left padding
        int rightX = this.x + this.backgroundWidth; // right padding

        float maxScale = 1.0f;
        float minScale = 0.5f; // don't scale below this
        int maxWidthLeft = (rightX - leftX) / 2;  // max width for left text (adjust as needed)
        int maxWidthRight = (rightX - leftX) / 2; // max width for right text

        int currentTextY = textY;

        for (int i = 0; i < handler.lines.size(); i += 2) {
            String leftPart = handler.lines.get(i).getString();
            String rightPart = "";
            if (i + 1 < handler.lines.size()) {
                rightPart = handler.lines.get(i + 1).getString();
            }

            // Calculate scale for left text
            float leftScale = maxScale;
            int leftWidth = this.textRenderer.getWidth(leftPart);
            while (leftWidth * leftScale > maxWidthLeft && leftScale > minScale) {
                leftScale -= 0.05f;
            }

            // Calculate scale for right text
            float rightScale = maxScale;
            int rightWidth = this.textRenderer.getWidth(rightPart);
            while (rightWidth * rightScale > maxWidthRight && rightScale > minScale) {
                rightScale -= 0.05f;
            }

            // Draw left text with leftScale
            context.getMatrices().push();
            context.getMatrices().translate(leftX, currentTextY, 0);
            context.getMatrices().scale(leftScale, leftScale, 1f);
            context.drawText(this.textRenderer, leftPart, 0, 0, 0x404040, false);
            context.getMatrices().pop();

            // Draw right text with rightScale, right-aligned
            context.getMatrices().push();
            int scaledRightWidth = (int)(rightWidth * rightScale);
            context.getMatrices().translate(rightX - scaledRightWidth + 6, currentTextY, 0);
            context.getMatrices().scale(rightScale, rightScale, 1f);
            context.drawText(this.textRenderer, rightPart, 0, 0, 0x404040, false);
            context.getMatrices().pop();

            currentTextY += 12;
        }




        // 7. political symbol
        int COMM_U = 0;    // left pixel of the symbol
        int COMM_V = 207;  // top pixel of the symbol

        switch (this.handler.ideology){
            case ANARCHIST -> COMM_V-=17;
            default -> {break;}//communist
            case GREEN -> COMM_U+=17;
            case PROGRESSIVE -> {COMM_U+=17;COMM_V-=17;}
            case MODERATE -> COMM_U+=34;
            case CONSERVATIVE -> COMM_U+=51;
            case LIBERTARIAN -> {COMM_U+=51;COMM_V-=17;}
            case FASCIST -> COMM_U+=68;
            case REACTIONARY -> {COMM_U+=68;COMM_V-=17;}
        }

        int COMM_W = 16;
        int COMM_H = 16;

        int polX = this.x+BG_WIDTH-24;
        int polY = this.y+6;

        context.drawTexture(
                BG_TEXTURE,
                polX,polY,      // where you want it on the screen
                COMM_U, COMM_V,    // where it is in the texture
                COMM_W, COMM_H,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        if (mouseX >= polX && mouseX <= polX + 16 &&
                mouseY >= polY && mouseY <= polY + 16) {

            context.drawTooltip(
                    this.textRenderer,
                    List.of(Text.literal(this.handler.ideology.toString().toUpperCase())),
                    mouseX, mouseY
            );
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // We already draw our background in render(), so nothing here
    }
}
