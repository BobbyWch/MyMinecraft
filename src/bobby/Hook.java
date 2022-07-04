package bobby;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Hook {
    private static final ArrayList<Mod> mods = new ArrayList<>();
    private static final HashMap<String, Mod> map = new HashMap<>();

    /**
     * Invoke when start game.
     */
    public static void start() {
        mods.add(new Mod("sprint", Keyboard.KEY_I, true));
        mods.add(new Mod("hitbox", Keyboard.KEY_H, true));
        mods.add(new Mod("killaura", Keyboard.KEY_K, false));
    }

    /**
     * Invoke when stop game.
     */
    public static void stop() {
    }

    /**
     * Draw screen after drawing.
     */
    private static int selectI = 0;

    public static void d() {
        FontRenderer font = Minecraft.theMinecraft.fontRendererObj;
        int y = 3;
        int width = 0;
        Mod mod;
        int red = new Color(255, 0, 0, 152).getRGB();
        int black = new Color(0, 0, 0, 152).getRGB();
        int white = 0xFFFFFFFF;
        for (int i = 0; i < mods.size(); i++) {
            if (mods.get(i).wid > width) width = mods.get(i).wid;
        }
        Gui.drawRect(2, 2, 6 + width, 4 + (font.FONT_HEIGHT + 1) * mods.size(), red);
        for (int i = 0; i < mods.size(); i++) {
            mod = mods.get(i);
            if (i == selectI) {
                Gui.drawRect(3, y, 5 + width, y + font.FONT_HEIGHT + 1, black);
            }
            if (mod.isEnable) {
                font.drawString(mod.name, 4, y, white);
            } else {
                font.drawString(mod.name, 4, y, 0xFFFFFF8F);
            }
            y += font.FONT_HEIGHT + 1;
        }
    }

    public static void r() {
        if (map.get("hitbox").isEnable) {
            double x = Minecraft.theMinecraft.renderManager.renderPosX;
            double y = Minecraft.theMinecraft.renderManager.renderPosY;
            double z = Minecraft.theMinecraft.renderManager.renderPosZ;
            AxisAlignedBB box;
            for (Entity entity : Minecraft.theMinecraft.theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer && !(entity instanceof EntityPlayerSP)) {
                    box = entity.getEntityBoundingBox();
                    RenderGlobal.func_181563_a(new AxisAlignedBB(
                            box.minX - x, box.minY - y, box.minZ - z,
                            box.maxX - x, box.maxY - y, box.maxZ - z
                    ), 255, 166, 45, 255);
                }
            }
        }
    }

    /**
     * Remapping for keybinds.
     */
    private static final KeyBinding keyForward = Minecraft.theMinecraft.gameSettings.keyBindLeft;

    /**
     * Invoke after player updates.
     */
    public static void u() {
        Minecraft mc = Minecraft.theMinecraft;
        EntityPlayerSP p = mc.thePlayer;

        p.setSprinting(map.get("sprint").isEnable && keyForward.isKeyDown() && !(p.isInWater() || p.isInLava())
                && p.getFoodStats().getFoodLevel() > 6);//keyBindLeft -> keyBindForward

        if (map.get("killaura").isEnable) {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e instanceof EntityLivingBase && !(e instanceof EntityPlayerSP)) {
                    if (((EntityLivingBase) e).getHealth() >= 0 && e.getDistanceToEntity(p) < 4) {
                        p.swingItem();
                        mc.playerController.attackEntity(p, e);
                    }
                }
            }
        }
    }

    /**
     * Invoke before key pressed.
     */
    public static void k(int key) {
        for (int i = 0; i < mods.size(); i++) {
            if (mods.get(i).key == key) {
                mods.get(i).isEnable = !mods.get(i).isEnable;
            }
        }
        if (key == Keyboard.KEY_UP) {
            if (selectI > 0) selectI--;
            else selectI = mods.size() - 1;
        } else if (key == Keyboard.KEY_DOWN) {
            if (selectI < mods.size() - 1) selectI++;
            else selectI = 0;
        }
        if (key == Keyboard.KEY_RETURN) mods.get(selectI).isEnable = !mods.get(selectI).isEnable;
    }

    private static final class Mod {
        public final String name;
        public int key;
        public boolean isEnable;
        public final int wid;

        public Mod(String name, int key, boolean isEnable) {
            this.name = name;
            this.key = key;
            this.isEnable = isEnable;
            wid = Minecraft.theMinecraft.fontRendererObj.getStringWidth(name);
            map.put(name, this);
        }
    }
}