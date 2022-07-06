import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import net.minecraft.client.main.Main;
import org.lwjgl.Sys;

public class Start {
    public static void main(String[] args) {
//        System.out.println(new Color(255, 0, 221, 255).getRGB());
//        System.exit(0);
        Main.main(concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}