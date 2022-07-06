package bobby;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.util.HashMap;

public final class Hook {
    public static final HashMap<String, Mod> map = new HashMap<>();
    private static final Mod[] mods = new Mod[]{
            new Mod("Sprint","sp", Keyboard.KEY_I, true),
            new Mod("Hitbox","hb", Keyboard.KEY_H, true),
            new Mod("Killaura","ka", Keyboard.KEY_K, false),
            new Mod("++ Killaura", "mka",0, false),
            new Mod("Auto Clicker","ac", Keyboard.KEY_C, false),
            new Mod("Click Teleport","ctp", 0, false),
            new Mod("Critical", "cri",0, false),
            new Mod("Reach","re",Keyboard.KEY_R,false),
            new Mod("Target Info","ti",0,true)
    };
    /**
     * Invoke when start game.
     */
    public static void start() {
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
    private static final int red=new Color(255, 0, 0, 152).getRGB();
    private static final int black = new Color(0, 0, 0, 152).getRGB();
    private static final int gray=new Color(26, 26, 26, 211).getRGB();
    private static final int white = 0xFFFFFFFF;
    public static void d() {
        FontRenderer font = Minecraft.theMinecraft.fontRendererObj;
        int y = 3;
        int width = 0;
        Mod mod;
        for (int i = 0; i < mods.length; i++) {
            if (mods[i].wid > width) width = mods[i].wid;
        }
        Gui.drawRect(2, 2, 6 + width, 4 + (font.FONT_HEIGHT + 1) * mods.length, red);
        for (int i = 0; i < mods.length; i++) {
            mod = mods[i];
            if (i == selectI) {
                Gui.drawRect(3, y, 5 + width, y + font.FONT_HEIGHT + 1, black);
            }
            if (mod.isEnable) {
                font.drawString(mod.name, 4, y, white);
            } else {
                font.drawString(mod.name, 4, y, gray);
            }
            y += font.FONT_HEIGHT + 1;
        }

        if (map.get("ti").isEnable){
            if (Minecraft.theMinecraft.objectMouseOver.entityHit instanceof EntityLivingBase){
                EntityLivingBase entityHit = (EntityLivingBase) Minecraft.theMinecraft.objectMouseOver.entityHit;
                GuiInventory.drawEntityOnScreen(120,100,30,0,0,entityHit);
                font.drawString(entityHit.getName(),100,100,0xFFFFFFFF);
                font.drawString(entityHit.getHealth()+"/"+entityHit.getMaxHealth(),100,110,0xFFFFFFFF);
            }
        }
    }

    public static void r() {
        if (map.get("hb").isEnable) {
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
    private static final KeyBinding keyAttack = Minecraft.theMinecraft.gameSettings.keyBindPickBlock;
    private static final KeyBinding keyUse = Minecraft.theMinecraft.gameSettings.keyBindDrop;

    /**
     * Invoke after player updates.
     */
    private static int i = 0;

    public static void u() {
        Minecraft mc = Minecraft.theMinecraft;
        EntityPlayerSP p = mc.thePlayer;
        p.setSprinting(map.get("sp").isEnable && keyForward.isKeyDown() && !(p.isInWater() || p.isInLava())
                && p.getFoodStats().getFoodLevel() > 6);//keyBindLeft -> keyBindForward
        if (map.get("ka").isEnable && keyAttack.pressed&& ++i == 2) {
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e instanceof EntityLivingBase && !(e instanceof EntityPlayerSP)) {
                    if (((EntityLivingBase) e).getHealth() >= 0 && e.getDistanceToEntity(p) < 4.5) {
                        mc.playerController.attackEntity(p, e);
                        p.swingItem();
                        break;
                    }
                }
            }
            i=0;
        }
        if (map.get("mka").isEnable && keyAttack.pressed) {
            int a=0;
            for (Entity e : mc.theWorld.loadedEntityList) {
                if (e instanceof EntityLivingBase && !(e instanceof EntityPlayerSP)) {
                    if (((EntityLivingBase) e).getHealth() >= 0 && e.getDistanceToEntity(p) < 4.5) {
                        mc.playerController.attackEntity(p, e);
                        p.swingItem();
                        if (++a==3) break;
                    }
                }
            }
            if (a!=0) p.swingItem();
        }

        if (map.get("ac").isEnable && ++i == 2) {
            if (keyAttack.pressed) {
                mc.clickMouse();
            }
            if (keyUse.pressed &&p.getHeldItem()!=null&& p.getHeldItem().getItem() instanceof ItemBlock) {
                mc.rightClickMouse();
            }
            i = 0;
        }

        if (rc0 != 0) rc0--;
    }

    /**
     * Invoke before key pressed.
     */
    public static void k(int key) {
        for (int i = 0; i < mods.length; i++) {
            if (mods[i].key == key) {
                mods[i].isEnable = !mods[i].isEnable;
            }
        }
        if (key == Keyboard.KEY_UP) {
            if (selectI > 0) selectI--;
            else selectI = mods.length - 1;
        } else if (key == Keyboard.KEY_DOWN) {
            if (selectI < mods.length - 1) selectI++;
            else selectI = 0;
        }

        if (key == Keyboard.KEY_RETURN) mods[selectI].isEnable = !mods[selectI].isEnable;
    }

    /**
     * Invoke after right click mouse.
     */
    private static int rc0 = 0;
    public static void rc() {
        if (map.get("ctp").isEnable && rc0 == 0) {
            BlockPos pos = Minecraft.theMinecraft.objectMouseOver.getBlockPos();
            if (pos!=null)Minecraft.theMinecraft.thePlayer.setPositionAndUpdate(pos.x, pos.y + 1, pos.z);
            rc0 = 8;
        }
    }

    public static final class Mod {
        public final String name;
        public final String id;
        public int key;
        public boolean isEnable;
        public final int wid;

        public Mod(String name, String id,int key, boolean isEnable) {
            this.name = name;
            this.id=id;
            this.key = key;
            this.isEnable = isEnable;
            wid = Minecraft.theMinecraft.fontRendererObj.getStringWidth(name);
            map.put(id, this);
        }
    }
}