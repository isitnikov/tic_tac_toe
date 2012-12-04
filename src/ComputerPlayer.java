import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/21/12
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class ComputerPlayer extends PlayerAbstract {
    boolean action() {
        return true;
    }

    public boolean isReady() {
        /*
        for (Field field : _game.getEnabledFields()) {
            field.setPlayer(this);
            field.setText(getSymbol());
            field.setEnabled(false);
            break;
        }
        */
        setReady(true);
        return ready;
    }

}
