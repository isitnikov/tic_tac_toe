import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/3/12
 * Time: 12:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameCollection
{
    private List<GameAbstract> list = new ArrayList<GameAbstract>();

    public void add(GameAbstract game)
    {
        list.add(game);
    }

    public int getSize() {
        return list.size();
    }

    public void remove(GameAbstract game) {
        list.remove(game);
    }
}
