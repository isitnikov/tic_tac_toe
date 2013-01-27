import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: sitnikov
 * Date: 11/3/12
 * Time: 12:18 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class GameAbstract
{
    private WorkareaPane frame = null;
    private String title = Workarea.getString("game");
    private List<HumanPlayer> players = new ArrayList<HumanPlayer>();
    private int currentPlayer = 0;
    private boolean enabled = true;

    private JPanel panel = null;
    private JPanel[] panels = null;
    private JPanel statusPanel = null;
    private JLabel statusLabel = null;

    private HumanPlayer winner = null;

    protected boolean cube = false;
    protected int edge = 0;

    /**
     * matrix[slice][row][column]
     */
    private Field[][][] matrix = null;

    public GameAbstract(boolean _cube, int _edge)
    {
        edge = _edge;
        cube = _cube;

        frame = new WorkareaPane(title);
        frame.setGame(this);
        frame.setFrameIcon(Icons.get("joystick"));
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                WorkareaPane frame = (WorkareaPane) e.getInternalFrame();
                GameAbstract game = frame.getGame();

                if (game.enabled == true) {
                    int answer = JOptionPane.showInternalConfirmDialog(frame, Workarea.getString("do_you_want_close"), Workarea.getString("game"), JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                GameFabric.getCollection().remove(frame.getGame());
            }
        });

        statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        frame.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);

        panel = new JPanel(new GridLayout(1, getType()));
        panels = new JPanel[getType()];

        matrix = new Field[getType()][getEdge()][getEdge()];

        for (int i = 0; i < panels.length; i++) {
            panels[i] = new JPanel(new GridLayout(getEdge(),getEdge()));
            panels[i].setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
            for (int j = i*getSquareEdge(); j < getSquareEdge()*(i+1); j++ ) {
                Field field = new Field();
                field.setGame(this);
                field.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (enabled == true) {
                            Field btn = (Field) e.getSource();
                            GameAbstract game = btn.getGame();
                            HumanPlayer player = game.getCurrentPlayer();

                            player.action();

//                            btn.setText(player.getSymbol());
                            btn.setIcon(player.getIcon());
                            btn.setEnabled(false);
                            Music.play("pencil.wav");
                            btn.setPlayer(player);

                            game.move();
                        }
                    }
                });

                field.setSlice(getSlice(j));
                field.setRow(getRow(j));
                field.setColumn(getColumn(j));
                field.setIndex(j);

                matrix[field.getSlice()][field.getRow()][field.getColumn()] = field;

                panels[i].add(field);
            }
            panel.add(panels[i]);
        }
        frame.add(panel);
    }

    public WorkareaPane getFrame()
    {
        return frame;
    }

    public void setPlayer(HumanPlayer player)
    {
        player.setGame(this);
        players.add(player);
    }

    public boolean move()
    {
        if (enabled == false) {
            return false;
        }
        if (checkFields() == true || outOfMoves() == true) {
            endGame();
            return false;
        }

        statusLabel.setText(String.format(Workarea.getString("player_x_runs"), players.indexOf(getCurrentPlayer())));

        if (getCurrentPlayer().isReady() == true) {
            nextPlayer();
            getCurrentPlayer().setReady(false);
            move();
        }
        return true;
    }

    private boolean outOfMoves() {
        for (int s = 0; s < matrix.length; s++) {
            for (int i = 0; i < matrix[s].length; i++) {
                for (int j = 0; j < matrix[s][i].length; j++) {
                    if (matrix[s][i][j].getPlayer() == null) {
                        return false;
                    }
                }
            }
        }

        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    protected HumanPlayer getCurrentPlayer()
    {
        return players.get(currentPlayer);
    }

    protected void nextPlayer()
    {
        if (++currentPlayer == players.size()) {
            currentPlayer = 0;
        }
    }

    protected boolean checkFields(Field[] fields) {
        HumanPlayer player = null;

        for(int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field.isEnabled()) {
                return false;
            }
            if (player == null) {
                player = field.getPlayer();
            }
            if (!player.equals(field.getPlayer())) {
                return false;
            }
        }

        for(int i = 0; i < fields.length; i++) {
            fields[i].setForeground(new Color(255, 0, 0));
            fields[i].setBackground(new Color(255, 0, 0));
        }

        winner = player;
        winner.setWins(winner.getWins() + 1);
        winner.save();

        return true;
    }

    protected Field[][] getAllColumns()
    {
        Field[][] allColumns = new Field[cube ? getSquareEdge() : getEdge()][getEdge()];

        int columns = 0;

        for (int slice = 0; slice < getType(); slice++) {
            for (int row = 0; row < getEdge(); row++) {
                Field[] columnFields = new Field[getEdge()];
                for (int column = 0; column < getEdge(); column++) {
                    columnFields[column] = matrix[slice][column][row];
                }
                allColumns[columns++] = columnFields;
            }
        }

        return allColumns;
    }

    protected Field[][] getAllRows()
    {
        Field[][] allRows = new Field[cube ? getSquareEdge()*2 : getEdge()][getEdge()];

        int rows = 0;

        for (int slice = 0; slice < getType(); slice++) {
            for (int row = 0; row < getEdge(); row++) {
                allRows[rows] = matrix[slice][row];
                if (cube) {
                    Field[] rowFields = new Field[getEdge()];
                    for (int column = 0; column < getEdge(); column++) {
                        rowFields[column] = matrix[column][row][slice];
                    }
                    allRows[getSquareEdge() + rows] = rowFields;
                }
                rows++;
            }
        }

        return allRows;
    }

    protected boolean checkFields() {
        Field[][] rows = getAllRows();

        for (int i = 0; i < rows.length; i++) {
            if (checkFields(rows[i])) {
                return true;
            }
        }

        Field[][] columns = getAllColumns();

        for (int i = 0; i < columns.length; i++) {
            if (checkFields(columns[i])) {
                return true;
            }
        }

        return false;
    }

    protected int getEdge() {
        return edge;
    }

    protected int getType() {
        return cube == true ? getEdge() : 1;
    }

    protected int getSlice(int index) {
        return getSlice(index, 1);
    }

    protected int getSlice(int index, int deduct) {
        index = index + deduct;
        return (int) Math.ceil(
            (double)index / (double)getSquareEdge()
        ) - deduct;
    }

    protected int getRow(int index) {
        return getRow(index, 1);
    }

    protected int getRow(int index, int deduct) {
        index = index + deduct;
        return (int) Math.ceil(
                ((double)index - (double)getEdge() * ((double)getSlice(index, 0) - 1.0) * (double)getEdge()) / (double)getEdge()
        ) - deduct;
    }

    protected int getColumn(int index) {
        return getColumn(index, 1);
    }

    protected int getColumn(int index, int deduct) {
        index = index + deduct;
        return (getEdge() - (getEdge() * getRow(index, 0) - index) - getSquareEdge() * (getSlice(index, 0)-1)) - deduct;
    }

    protected int getSquareEdge() {
        return getEdge()*getEdge();
    }

    protected void endGame()
    {
        enabled = false;
        if (winner != null) {
            JOptionPane.showInternalMessageDialog(frame, String.format(Workarea.getString("player_x_wins"), winner.getName()), Workarea.getString("game_over"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showInternalMessageDialog(frame, Workarea.getString("winner_is_not_objective"), Workarea.getString("game_over"), JOptionPane.INFORMATION_MESSAGE);
        }
        for (int slice = 0; slice < getType(); slice++) {
            for (int row = 0; row < getEdge(); row++) {
                for (int column = 0; column < getEdge(); column++) {
                    matrix[slice][row][column].setEnabled(false);
                }
            }
        }
        Music.play("applause-1.wav");
    }
}
