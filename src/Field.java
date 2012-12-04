import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/20/12
 * Time: 7:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Field extends JButton {
    private GameAbstract _game = null;
    private PlayerAbstract _player = null;
    private int _slice, _row, _column, _index;

    public Field() {
        super();
        setBackground(new Color(255,255,255));
    }

    public void setGame(GameAbstract game) {
        _game = game;
    }

    public GameAbstract getGame() {
        return _game;
    }

    public void setPlayer(PlayerAbstract player) {
        _player = player;
    }

    public PlayerAbstract getPlayer() {
        return _player;
    }

    public int getSlice() {
        return _slice;
    }

    public int getColumn() {
        return _column;
    }

    public int getRow() {
        return _row;
    }

    public int getIndex() {
        return _index;
    }

    public void setSlice(int slice) {
        _slice = slice;
    }

    public void setColumn(int column) {
        _column = column;
    }

    public void setRow(int row) {
        _row = row;
    }

    public void setIndex(int index) {
        _index = index;
    }
}
