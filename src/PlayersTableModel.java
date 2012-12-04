import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class PlayersTableModel extends AbstractTableModel {
    protected ArrayList<PlayerAbstract> players = null;
    protected int columns = 3;
    PlayersTableModel(ArrayList<PlayerAbstract> _players) {
        super();
        players = _players;
//        addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                System.out.print(e);
//            }
//        });
    }

    public void add(PlayerAbstract _player) {
        players.add(_player);
    }

    public void removeByIndex(int index) {
        PlayerAbstract player = players.get(index);
        players.remove(player);
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        PlayerAbstract player = players.get(rowIndex);
        switch (columnIndex) {
            case 1:
                player.setName(String.valueOf(aValue));
                break;
        }
        player.save();
    }

    @Override
    public int getRowCount() {
        return players.size();
    }

    @Override
    public int getColumnCount() {
        return columns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return players.get(rowIndex).getId();
            case 1:
                return players.get(rowIndex).getName();
            case 2:
                return players.get(rowIndex).getWins();
        }
        return null;
    }

    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return "Name";
            case 2:
                return "Wins";
        }
        return null;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return true;
            case 2:
                return false;
        }
        return false;
    }
}
