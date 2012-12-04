import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/21/12
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class HumanPlayer extends PlayerAbstract {
    public HumanPlayer() {}
    public HumanPlayer(int _id, String _name, int _wins) {
        super(_id, _name, _wins);
    }
    boolean action() {
        setReady(true);
        return true;
    }
}
