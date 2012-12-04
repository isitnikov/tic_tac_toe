import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Music {
    public static Map<String, Clip> music = new HashMap<String, Clip>();
    public static Clip clip = null;

    public static void play(String fileName) {
        clip = null;
        if (music.containsKey(fileName)) {
            clip = music.get(fileName);
        } else {
            try {
                URL url = getPath(fileName);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioIn);
            } catch (Exception e) {
                e.printStackTrace();
            }
            music.put(fileName, clip);
        }

        if (clip != null) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    clip.stop();
                    clip.setFramePosition(0);
                    clip.start();
                }
            });
        }
    }

    private static URL getPath(String fileName) throws MalformedURLException {
        return new File("music/" + fileName).toURI().toURL();
    }
}
