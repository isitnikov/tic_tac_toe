import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/26/12
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class Db {
    private static Db instance = null;
    private Connection connection = null;
    private boolean connected = false;
    private int lastInsertId = 0;


    public static Db getInstance()
    {
        if (instance == null) {
            instance = new Db();
        }
        return instance;
    }

    private Db() {
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:game.dat");
        connected = true;
    }

    private boolean isConnected()
    {
        return connected;
    }

    protected void finalize() throws Throwable
    {
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    public ResultSet getPlayers()
    {
        if (!isConnected()) {
            return null;
        }
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Players");
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePlayer(int playerId, String playerColumn, Object value) {
        if(!isConnected()) {
            return;
        }
        try{
            PreparedStatement stmt = connection.prepareStatement("UPDATE Players SET " + playerColumn + "=? WHERE id=?");
            stmt.setObject(1, value);
            stmt.setInt(2, playerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String implode(String separator, String... data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!data[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(data[i]);
                sb.append(separator);
            }
        }
        sb.append(data[data.length - 1]);
        return sb.toString();
    }

    public void insertOrUpdate(String table, HashMap<String, Object> data, Integer id) {
        if(!isConnected()) {
            return;
        }
        String sql = "";
        try{
            int i = 0;
            if (id == 0) {
                sql = "INSERT INTO " + table + " ('id'";
                for (String key : data.keySet()) {
                    sql += ",'" + key + "'";
                }
                sql += ") VALUES (NULL";
                for (Object key : data.values()) {
                    sql += ",?";
                }
                sql += ")";
            } else {
                sql = "UPDATE " + table + " SET ";
                i = 0;
                Set<String> keySet = data.keySet();
                for (String key : keySet) {
                    i++;
                    sql += "'" + key + "'=?";
                    if (i != keySet.size()) {
                        sql += ",";
                    }
                }
                sql += " WHERE id=" + id;
            }
            PreparedStatement stmt = connection.prepareStatement(sql);
            i = 0;
            for (Object value : data.values()) {
                stmt.setObject(++i, value);
            }
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            while (rs != null && rs.next()) {
                lastInsertId = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(sql);
            e.printStackTrace();
        }
    }

    public int getLastInsertId() {
        return lastInsertId;
    }

    public ArrayList<PlayerAbstract> getPlayersAsObjects() {
        ResultSet resultSet = getPlayers();
        ArrayList<PlayerAbstract> players = new ArrayList<PlayerAbstract>();
        try {
            while (resultSet.next()) {
                HumanPlayer player = null;
                if (PlayerCollection.hasPlayer(resultSet.getInt("id"))) {
                    player = PlayerCollection.getPlayer(resultSet.getInt("id"));
                } else {
                    player = new HumanPlayer(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("wins")
                    );
                    PlayerCollection.add(player);
                }
                players.add(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return players;
    }

    public ResultSet getConfiguration()
    {
        if (!isConnected()) {
            return null;
        }
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Configuration");
            ResultSet resultSet = stmt.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,Object> getConfigurationHash() {
        ResultSet resultSet = getConfiguration();
        if (resultSet == null) {
            return null;
        }
        Map<String,Object> hash = new HashMap<String, Object>();
        try {
            ResultSetMetaData md = resultSet.getMetaData();
            int columns = md.getColumnCount();
            while (resultSet.next()) {
                for(int i=1; i<=columns; i++){
                    hash.put(md.getColumnName(i),resultSet.getObject(i));
                }
            }
            return hash;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
