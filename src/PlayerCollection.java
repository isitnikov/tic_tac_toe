import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/3/12
 * Time: 12:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerCollection
{
    private static HashMap<Integer, HumanPlayer> hashMap = new HashMap<Integer, HumanPlayer>();

    public static void add(HumanPlayer player)
    {
        hashMap.put(player.getId(), player);
    }

    public static int getSize()
    {
        return hashMap.size();
    }

    public static void remove(HumanPlayer player)
    {
        hashMap.remove(player);
    }

    public static boolean hasPlayer(int playerId)
    {
        return hashMap.containsKey(playerId);
    }

    public static HumanPlayer getPlayer(int playerId)
    {
        return hashMap.containsKey(playerId) ? hashMap.get(playerId) : null;
    }
}
