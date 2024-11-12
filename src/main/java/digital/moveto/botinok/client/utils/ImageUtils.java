package digital.moveto.botinok.client.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageUtils {

    public static Color getBottomLeftColor(String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new URL(imageUrl));

        int x = 0;
        int y = image.getHeight() - 1;

        int rgb = image.getRGB(x, y);
        return new Color(rgb);
    }
}