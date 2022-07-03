package bobby;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;

public class Hook {
    private static final HashMap<String,Boolean> map=new HashMap<>();
    private static final HashMap<Integer,String> keys=new HashMap<>();
    /**
     * Invoke when start game.
     */
    public static void start(){
        map.put("sprint",true);
        map.put("modA",true);
        map.put("modB",true);
        keys.put(Keyboard.KEY_I,"sprint");
    }

    /**
     * Invoke when stop game.
     */
    public static void stop(){}

    /**
     * Invoke after render.
     */
    public static void r() {
        FontRenderer font=Minecraft.theMinecraft.fontRendererObj;
        int y=0;
        for (String n:map.keySet()){
            if (map.get(n)){
                font.drawString(n,0,y,0xFFFFFFFF);
                y+=font.FONT_HEIGHT+1;
            }
        }
    }

    /**
     * Invoke after player updates.
     */
    public static void u() {
        Minecraft mc=Minecraft.theMinecraft;
        EntityPlayerSP p=mc.thePlayer;
        GameSettings s=mc.gameSettings;

        p.setSprinting(map.get("sprint")&&s.keyBindLeft.isKeyDown()&&!(p.isInWater()||p.isInLava())
                &&p.getFoodStats().getFoodLevel()>6);//keyBindLeft -> keyBindForward

    }

    /**
     * Invoke before key pressed.
     */
    public static void k(int key){
        String s=keys.get(key);
        if (s!=null){
            map.put(s,!map.get(s));
        }
    }
}
