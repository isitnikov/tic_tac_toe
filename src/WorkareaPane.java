import javax.swing.*;
import java.awt.*;

public class WorkareaPane extends JInternalFrame {
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane();
    private GameAbstract _game = null;
    public WorkareaPane(String title) {
        setTitle(title);
        setSize(800, 600);
        setMaximizable(true);
        setFocusable(true);
        setResizable(true);
        setIconifiable(true);
        setClosable(true);
        //scrollPane.getViewport().add(textArea);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
    public void setGame(GameAbstract game) {
        _game = game;
    }
    public GameAbstract getGame() {
        return _game;
    }


}
