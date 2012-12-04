import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/25/12
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Icons {
    private static Map<String, Icon> icons = new HashMap<String, Icon>();

    public static Icon get(String iconName) {
        if (icons.containsKey(iconName)) {
            return icons.get(iconName);
        }

        Icon icon = loadIcon(iconName);
        icons.put(iconName, icon);
        return icon;
    }

    private static Icon loadIcon (String iconName) {
        String path = getPath(iconName);
        BufferedImage image;
        try {
            image = ImageIO.read(ClassLoader.getSystemResource(path));
        } catch (IOException e) {
            return getDefault();
        }
        return new ImageIcon(image);
    }

    private static String getPath(String iconName) {
        return "icons/" + iconName + ".png";
    }


    private static Icon getDefault() {
        return UIManager.getIcon("OptionPane.errorIcon");
    }
}
