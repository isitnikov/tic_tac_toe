import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private HashMap<PlayerAbstract,List<Integer>> moves = new HashMap<PlayerAbstract, List<Integer>>();

    private JButton exportButton = null;

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

        exportButton = new JButton(Workarea.getString("export_results"));
        exportButton.setIcon(Icons.get("report"));
        exportButton.setEnabled(false);

        statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        frame.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 24));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("status");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        statusPanel.add(exportButton,BorderLayout.EAST);

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

                            game.logMove(player,btn.getIndex());
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

    public void logMove(PlayerAbstract player, int position) {
        if (!moves.containsKey(player)) {
            moves.put(player, new ArrayList<Integer>());
        }
        moves.get(player).add(position);
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

    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    protected void endGame()
    {
        enabled = false;
        if (winner != null) {
            JOptionPane.showInternalMessageDialog(frame, String.format(Workarea.getString("player_x_wins"), winner.getName()), Workarea.getString("game_over"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showInternalMessageDialog(frame, Workarea.getString("winner_is_not_objective"), Workarea.getString("game_over"), JOptionPane.INFORMATION_MESSAGE);
        }
        statusLabel.setText(Workarea.getString("game_over"));
        exportButton.setEnabled(true);
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new File(Workarea.getString("results") + ".html"));
                fc.setDialogTitle(Workarea.getString("save_results"));
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        if (f.isDirectory()) {
                            return true;
                        }

                        String extension = getExtension(f);
                        if (extension != null) {
                            if (extension.equals("html")) {
                                return true;
                            } else {
                                return false;
                            }
                        }

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return "HTML " + Workarea.getString("documents");  //To change body of implemented methods use File | Settings | File Templates.
                    }
                });
                int returnVal = fc.showSaveDialog(getFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new FileWriter(file));
                        bw.write("<html>");
                        bw.write("<head>");
                        bw.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
                        bw.write("</head>");
                        bw.write("<body>");
                        bw.write("<h1>" + Workarea.getString("game_results") + "</h1>");

                        int i = 0;
                        for (PlayerAbstract player : moves.keySet()) {
                            i++;
                            bw.write("<h2>" + Workarea.getString("player" + i) + ": " + player.getName() + "</h2>");
                            bw.write("<div style='overflow:hidden;'>");
                            List<Integer> list = moves.get(player);
                            bw.write("<h3>" + Workarea.getString("moves") + "</h3><ul>");
                            for (int key : list) {
                                bw.write("<li>" + (key + 1) + "</li>");
                            }
                            bw.write("</ul>");
                            for (int slice = 0; slice < matrix.length; slice++) {
                                bw.write("<table border='1' cellpadding='5px' cellspacing='1px' align='left' style='margin-right: 5px;'>");
                                for (int m = 0; m < matrix[slice].length; m++) {
                                    bw.write("<tr>");
                                    for (int n = 0; n < matrix[slice][m].length; n++) {
                                        Field f = matrix[slice][m][n];
                                        String color = "#FFFFFF";
                                        if (f.getPlayer() != null && f.getPlayer().equals(player)) {
                                            color = "#DDDDDD";
                                        }
                                        bw.write("<td bgcolor='"+ color +"' width='25px' height='25px'>");
                                        if (f.getPlayer() != null && f.getPlayer().equals(player)) {
                                            bw.write("<b>" + (matrix[slice][m][n].getIndex()+1) + "</b>");
                                        } else {
                                            bw.write(String.valueOf(matrix[slice][m][n].getIndex()+1));
                                        }
                                        bw.write("</td>");
                                    }
                                    bw.write("</tr>");
                                }
                                bw.write("</table>");
                            }
                            bw.write("</div>");
                        }
                        bw.write("<div style='overflow:hidden;'>");
                        if (winner != null) {
                            bw.write("<h2>" + Workarea.getString("result_of_game") + ": " + String.format(Workarea.getString("player_x_wins"), winner.getName()) + "</h2>");
                        } else {
                            bw.write("<h2>" + Workarea.getString("result_of_game") + ": " + Workarea.getString("winner_is_not_objective") + "</h2>");
                        }
                        bw.write("</div>");
                        bw.write("</body>");
                        bw.write("</html>");
                        bw.close();

                        Desktop.getDesktop().browse(file.toURI());
                    } catch (IOException ex) {
                        ex.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });
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
