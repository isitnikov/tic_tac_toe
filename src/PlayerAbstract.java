import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/21/12
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class PlayerAbstract {
    protected int id = 0;
    protected String name = null;
    protected int wins = 0;
    protected Icon icon = null;
    protected GameAbstract _game = null;
    protected boolean ready = true;

    public String toString() {
        return id + ") " + name;
    }

    public PlayerAbstract() {

    }

    public PlayerAbstract(int _id, String _name, int _wins) {
        id = _id;
        name = _name;
        wins = _wins;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int _wins) {
        wins = _wins;
    }

    public void setName(String _name) {
        name = _name;
    }

    public void setIcon(Icon _icon) {
        icon = _icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setGame(GameAbstract game) {
        _game = game;
    }

    public void setReady(boolean isReady) {
        ready = isReady;
    }

    public boolean isReady() {
        return ready;
    }

    public int getColumns() {
        return 3;
    }

    public void save() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", name);
        data.put("wins", wins);
        Db.getInstance().insertOrUpdate("Players", data, id);
        if (id == 0) {
            id = Db.getInstance().getLastInsertId();
        }
    }

    abstract boolean action();
}
