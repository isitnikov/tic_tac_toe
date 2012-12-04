import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/2/12
 * Time: 11:44 PM
 * To change this template use File | Settings | File Templates.
 */

public class Application
{
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Workarea.getInstance();
            }
        });
    }
}
