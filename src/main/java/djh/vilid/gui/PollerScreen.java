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
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class PollerScreen extends HandledScreen<PollerScreenHandler> {
    private static final Identifier BG_TEXTURE =
            Identifier.of("vilid", "textures/gui/villagerid.png");

    private static final int BG_WIDTH = 192;
    private static final int BG_HEIGHT = 112;
    private static final int TEXTURE_WIDTH = 1024;
    private static final int TEXTURE_HEIGHT = 256;

    public PollerScreen(PollerScreenHandler handler, PlayerInventory inv, Text title) {
        super(handler, inv, title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // 1. Draw the darkened game background (Now requires mouse and delta in 1.21)
        this.renderBackground(context, mouseX, mouseY, delta);

        // 2. Let HandledScreen draw its default stuff (slots, titles, etc.)
        super.render(context, mouseX, mouseY, delta);

        // 3. Draw your GUI background texture
        int deltaX = 0; // based on ideology, changes card design
        if (this.handler.ideology.alignment.isMostLeftWing()) {
            deltaX += 193;
        } else if (this.handler.ideology.alignment.isMostRightWing()) {
            deltaX += (2 * 193);
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
            context.drawTexture(BG_TEXTURE, smileX, smileY, 0, 224, 32, 32, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else if (this.handler.villagerHappiness >= 20) {
            context.drawTexture(BG_TEXTURE, smileX, smileY, 33, 224, 32, 32, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            context.drawTexture(BG_TEXTURE, smileX, smileY, 66, 224, 32, 32, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        if (mouseX >= smileX && mouseX <= smileX + 32 && mouseY >= smileY && mouseY <= smileY + 32) {
            context.drawTooltip(this.textRenderer, handler.moodLines, mouseX, mouseY);
        }

        // 5. Draw the villager model (The 1.21 JOML Math Update)
        Entity e = MinecraftClient.getInstance().world.getEntityById(this.handler.villagerId);
        if (e instanceof VillagerEntity villager) {
            float xPos = this.x + 38;
            float yPos = this.y + 88;
            float size = 28f;

            // Calculate where the mouse is relative to the entity's face
            float lookX = (float)Math.atan((xPos - mouseX) / 40.0f);
            float lookY = (float)Math.atan((yPos - 40 - mouseY) / 40.0f);

            Quaternionf rotation = new Quaternionf().rotateZ((float)Math.PI);
            Quaternionf pitchRotation = new Quaternionf().rotateX(lookY * 20.0f * ((float)Math.PI / 180.0f));
            rotation.mul(pitchRotation);

            // Save the villager's real rotation data so they don't snap their neck in the real world
            float oldBodyYaw = villager.bodyYaw;
            float oldYaw = villager.getYaw();
            float oldPitch = villager.getPitch();
            float oldPrevHeadYaw = villager.prevHeadYaw;
            float oldHeadYaw = villager.headYaw;

            // Temporarily apply the UI tracking rotations
            villager.bodyYaw = 180.0f + lookX * 20.0f;
            villager.setYaw(180.0f + lookX * 40.0f);
            villager.setPitch(-lookY * 20.0f);
            villager.headYaw = villager.getYaw();
            villager.prevHeadYaw = villager.getYaw();

            // 1.21's new entity rendering method
            InventoryScreen.drawEntity(
                    context,
                    xPos, yPos, size,
                    new Vector3f(),
                    rotation,
                    pitchRotation,
                    villager
            );

            // Restore the real rotation data
            villager.bodyYaw = oldBodyYaw;
            villager.setYaw(oldYaw);
            villager.setPitch(oldPitch);
            villager.prevHeadYaw = oldPrevHeadYaw;
            villager.headYaw = oldHeadYaw;
        }

        // 6. text
        int textY = this.y + 28; // starting Y position
        int leftX = this.x + 66; // left padding
        int rightX = this.x + this.backgroundWidth; // right padding

        float maxScale = 1.0f;
        float minScale = 0.5f; // don't scale below this
        int maxWidthLeft = (rightX - leftX) / 2;
        int maxWidthRight = (rightX - leftX) / 2;

        int currentTextY = textY;

        for (int i = 0; i < handler.lines.size(); i += 2) {
            String leftPart = handler.lines.get(i).getString();
            String rightPart = "";
            if (i + 1 < handler.lines.size()) {
                rightPart = handler.lines.get(i + 1).getString();
            }

            float leftScale = maxScale;
            int leftWidth = this.textRenderer.getWidth(leftPart);
            while (leftWidth * leftScale > maxWidthLeft && leftScale > minScale) {
                leftScale -= 0.05f;
            }

            float rightScale = maxScale;
            int rightWidth = this.textRenderer.getWidth(rightPart);
            while (rightWidth * rightScale > maxWidthRight && rightScale > minScale) {
                rightScale -= 0.05f;
            }

            context.getMatrices().push();
            context.getMatrices().translate(leftX, currentTextY, 0);
            context.getMatrices().scale(leftScale, leftScale, 1f);
            context.drawText(this.textRenderer, leftPart, 0, 0, 0x404040, false);
            context.getMatrices().pop();

            context.getMatrices().push();
            int scaledRightWidth = (int)(rightWidth * rightScale);
            context.getMatrices().translate(rightX - scaledRightWidth + 6, currentTextY, 0);
            context.getMatrices().scale(rightScale, rightScale, 1f);
            context.drawText(this.textRenderer, rightPart, 0, 0, 0x404040, false);
            context.getMatrices().pop();

            currentTextY += 12;
        }

        // 7. political symbol
        int COMM_U = 0;
        int COMM_V = 207;

        switch (this.handler.ideology) {
            case ANARCHIST -> COMM_V -= 17;
            case GREEN -> COMM_U += 17;
            case PROGRESSIVE -> { COMM_U += 17; COMM_V -= 17; }
            case MODERATE -> COMM_U += 34;
            case CONSERVATIVE -> COMM_U += 51;
            case LIBERTARIAN -> { COMM_U += 51; COMM_V -= 17; }
            case FASCIST -> COMM_U += 68;
            case REACTIONARY -> { COMM_U += 68; COMM_V -= 17; }
            default -> {} // communist
        }

        int COMM_W = 16;
        int COMM_H = 16;
        int polX = this.x + BG_WIDTH - 24;
        int polY = this.y + 6;

        context.drawTexture(
                BG_TEXTURE,
                polX, polY,
                COMM_U, COMM_V,
                COMM_W, COMM_H,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        if (mouseX >= polX && mouseX <= polX + 16 && mouseY >= polY && mouseY <= polY + 16) {
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
