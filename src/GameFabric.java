import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/3/12
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
final public class GameFabric {
    public static final int CUBE4_GAME = 0;
    public static final int CUBE3_GAME = 1;
    public static final int STANDARD_GAME = 2;

    private static GameCollection _collection;

    private static String[] types = null;

    public static GameAbstract create (int gameCode) throws Exception {
        GameAbstract game = null;

        switch (gameCode) {
            /**
             * Game 4x4
             */
            case CUBE4_GAME:
                game = new CubeGame(4);
                break;

            /**
             * Game 3x3
             */
            case CUBE3_GAME:
                game = new CubeGame(3);
                break;

            /**
             * Standard
             */
            case STANDARD_GAME:
                game = new StandardGame(3);
                break;

            /**
             * If type of game is not found, then throws exception
             */
            default:
                throw new Exception("Wrong type of game");
        }

        /**
         * Add created game to collection
         */
        getCollection().add(game);

        /**
         * @TODO refactor
         */
        game.getFrame().setTitle(game.getFrame().getTitle() + ' ' + getCollection().getSize());

        PlayerAbstract player1 = new HumanPlayer();
        player1.setIcon(Icons.get("cross-lines"));
        player1.setReady(false);
        game.setPlayer(player1);

        PlayerAbstract player2 = new HumanPlayer();
        player2.setIcon(Icons.get("circle"));
        game.setPlayer(player2);

        game.move();

        return game;
    }

    public static void setCollection(GameCollection collection)
    {
        _collection = collection;
    }

    public static GameCollection getCollection()
    {
        return _collection;
    }

    public static String[] getTypes() {
        if (types == null) {
            types = new String[3];
            types[CUBE4_GAME] = new String(Workarea.getString("ttt_cube4x4"));
            types[CUBE3_GAME] = new String(Workarea.getString("ttt_cube3x3"));
            types[STANDARD_GAME] = new String(Workarea.getString("ttt_standard"));
        }
        return types;
    }
}