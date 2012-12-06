import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Workarea extends JFrame
{
    private MDIDesktopPane desktop = new MDIDesktopPane();
    private JMenuBar menu = new JMenuBar();
    private JInternalFrame mainFrame = new JInternalFrame();
    private JScrollPane scrollPane = new JScrollPane();
    private JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
    private WorkareaPane results = null;
    private WorkareaPane configuration = null;

    private GameCollection collection;

    private Map<String, Object> configurationHash = null;

    private static Workarea instance;

    private static ResourceBundle _locale = null;

    public Workarea()
    {
        super("Workarea");

        Locale.setDefault(new Locale(
            getConfigurationHash().get("locale_language").toString(),
            getConfigurationHash().get("locale_country").toString()
        ));

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowEventHandler());

        setDefaultLookAndFeelDecorated(true);
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);
        setMinimumSize(new Dimension(800, 600));

        collection = new GameCollection();
        GameFabric.setCollection(collection);

        _initDesktop();
        _initMenu();
        _initToolbar();

        scrollPane.getViewport().add(desktop);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public Map<String, Object> getConfigurationHash()
    {
        if (configurationHash == null) {
            configurationHash = Db.getInstance().getConfigurationHash();
        }
        return configurationHash;
    }

    public void setConfigurationHash(Map<String, Object> _configurationHash) {
        configurationHash = _configurationHash;
    }

    class WindowEventHandler extends WindowAdapter
    {
        public void windowClosing(WindowEvent evt)
        {
            if (collection.getSize() > 0) {
                return;
            }
            System.exit(0);
        }
    }

    public static Workarea getInstance()
    {
        if (instance == null) {
            instance = new Workarea();
            instance.setVisible(true);
        }
        return instance;
    }

    public void createGame()
    {
        Object value = JOptionPane.showInputDialog(desktop, Workarea.getString("select_type_of_game"), Workarea.getString("game_type"), JOptionPane.QUESTION_MESSAGE, null, GameFabric.getTypes(), null);
        int type = -1;
        for (int i = 0; i < GameFabric.getTypes().length; i++) {
            if (GameFabric.getTypes()[i].equals(value)) {
                type = i;
            }
        }

        try {
            GameAbstract game = GameFabric.create(type);
            desktop.add(game.getFrame());
            game.getFrame().setVisible(true);
            // menu.getMenu(1).add;
            // cascade(desktop.getBounds(), 24);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(desktop, Workarea.getString("game_wasnt_selected"), Workarea.getString("game_type"), JOptionPane.WARNING_MESSAGE);
        }
    }

    public void createResults()
    {
        if (results == null) {
            results = new WorkareaPane(getString("results"));
            results.setDefaultCloseOperation(HIDE_ON_CLOSE);
            desktop.add(results);
        };
        results.setVisible(true);
    }

    public void createConfiguration()
    {
        if (configuration == null) {
            configuration = new WorkareaPane(getString("configuration"));
            configuration.setDefaultCloseOperation(HIDE_ON_CLOSE);

            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel content = new JPanel();
            content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            content.setLayout(new BorderLayout());
            content.add(tabbedPane, BorderLayout.CENTER);

            JPanel buttons = new JPanel();
            JButton btnApply = new JButton(getString("apply"));
            buttons.add(btnApply);
            JButton btnOk = new JButton(getString("ok"));
            buttons.add(btnOk);
            JButton btnCancel = new JButton(getString("cancel"));
            buttons.add(btnCancel);
            content.add(buttons, BorderLayout.SOUTH);

            configuration.getContentPane().add(content);

            JPanel tabConfigBorder = new JPanel();
            tabConfigBorder.setLayout(new BorderLayout());
            tabConfigBorder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel tabConfig = new JPanel();
            tabConfig.setLayout(new BorderLayout());
            tabbedPane.add(getString("options"), tabConfigBorder);
            tabConfig.setLayout(new BoxLayout(tabConfig, BoxLayout.Y_AXIS));
            tabConfigBorder.add(tabConfig, BorderLayout.PAGE_START);

            /**
             * LANGUAGE PANEL
             * {
             */
            JPanel cfgPnlLanguage = new JPanel();
            cfgPnlLanguage.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            cfgPnlLanguage.setLayout(new BorderLayout());
            tabConfig.add(cfgPnlLanguage, BorderLayout.PAGE_START);
            JLabel cfgLngLabel = new JLabel(getString("language"), JLabel.LEFT);
            cfgPnlLanguage.add(cfgLngLabel, BorderLayout.WEST);

            HashMap<String,String> languages = new HashMap<String, String>();
            languages.put("ru_RU", "Русский");
            languages.put("en_US", "English");

            JComboBox cfgLng = new JComboBox(languages.values().toArray());
            cfgLng.setSelectedItem(languages.get(Locale.getDefault().toString()));
            cfgPnlLanguage.add(cfgLng, BorderLayout.CENTER);
            /**
             * }
             * PLAYER 1
             * {
             */
            JPanel cfgPnlPlayer1 = new JPanel();
            cfgPnlPlayer1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            cfgPnlPlayer1.setLayout(new BorderLayout());
            tabConfig.add(cfgPnlPlayer1, BorderLayout.PAGE_START);
            JLabel cfgPlr1Label = new JLabel(getString("player1"), JLabel.LEFT);
            cfgPnlPlayer1.add(cfgPlr1Label);
            /**
             * }
             * PLAYER 2
             * {
             */
            JPanel cfgPnlPlayer2 = new JPanel();
            cfgPnlPlayer2.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            cfgPnlPlayer2.setLayout(new BorderLayout());
            tabConfig.add(cfgPnlPlayer2, BorderLayout.PAGE_START);
            JLabel cfgPlr2Label = new JLabel(getString("player2"), JLabel.LEFT);
            cfgPnlPlayer2.add(cfgPlr2Label);
            /**
             * }
             */

            JPanel tabPlayersBorder = new JPanel();
            tabPlayersBorder.setLayout(new BorderLayout());
            tabPlayersBorder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel tabPlayers = new JPanel();
            tabPlayers.setLayout(new BorderLayout());
            tabbedPane.add(getString("players"), tabPlayersBorder);
            tabPlayers.setLayout(new BoxLayout(tabPlayers, BoxLayout.Y_AXIS));
            tabPlayersBorder.add(tabPlayers, BorderLayout.PAGE_START);

            final PlayersTableModel tableModel = new PlayersTableModel(Db.getInstance().getPlayersAsObjects());
            final JTable table = new JTable(tableModel);
            JScrollPane scrollTable = new JScrollPane(table);

            JPanel plrPnlTable = new JPanel();
            plrPnlTable.setLayout(new BorderLayout());
            plrPnlTable.add(scrollTable, BorderLayout.CENTER);
            tabPlayers.add(plrPnlTable, BorderLayout.CENTER);

            JPanel plrPnlControls = new JPanel();
            //plrPnlControls.setLayout(new BorderLayout());

            JButton plrPnlControlsBtnAdd = new JButton(getString("add"));
            plrPnlControlsBtnAdd.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String playerName = JOptionPane.showInputDialog(getString("what_is_players_name"), getString("player_name"));
                    if (playerName.isEmpty()) {
                        return;
                    }
                    HumanPlayer player = new HumanPlayer(0, playerName, 0);
                    player.save();
                    tableModel.add(player);
                    table.updateUI();
                }
            });
            JButton plrPnlControlsBtnRemove = new JButton(getString("remove"));
            plrPnlControlsBtnRemove.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (table.getSelectedRow() > 0) {
                        int answer = JOptionPane.showInternalConfirmDialog(configuration, Workarea.getString("do_you_want_to_remove_player"), Workarea.getString("remove_player"), JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.NO_OPTION) {
                            return;
                        }
                        tableModel.removeByIndex(table.getSelectedRow());
                        table.updateUI();

                    }
                }
            });

            plrPnlControls.add(plrPnlControlsBtnAdd, BorderLayout.SOUTH);
            plrPnlControls.add(plrPnlControlsBtnRemove, BorderLayout.SOUTH);
            plrPnlTable.add(plrPnlControls, BorderLayout.PAGE_END);


            configuration.pack();

            desktop.add(configuration);
        };
        configuration.setVisible(true);
    }

    protected WorkareaPane getConfiguration() {
        return configuration;
    }

    private void _initToolbar()
    {
        JButton newGame = new JButton(Icons.get("joystick"));
        newGame.setToolTipText(Workarea.getString("new"));
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workarea.getInstance().createGame();
            }
        });
        toolbar.add(newGame);

        JButton results = new JButton(Icons.get("report_user"));
        results.setToolTipText(Workarea.getString("results"));
        results.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workarea.getInstance().createResults();
            }
        });
        toolbar.add(results);

        JButton configuration = new JButton(Icons.get("cog"));
        configuration.setToolTipText(Workarea.getString("configuration"));
        configuration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workarea.getInstance().createConfiguration();
            }
        });
        toolbar.add(configuration);

        toolbar.addSeparator();

        JButton exit = new JButton(Icons.get("cross"));
        exit.setToolTipText(Workarea.getString("exit"));
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        toolbar.add(exit);

        toolbar.setFloatable(false);
    }

    private void _initMenu()
    {
        setJMenuBar(menu);

        JMenu fileMenu = new JMenu(Workarea.getString("game"));

        JMenuItem newItem = new JMenuItem(Workarea.getString("new"));
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workarea.getInstance().createGame();
            }
        });
        newItem.setIcon(Icons.get("joystick"));
        newItem.setVisible(true);
        fileMenu.add(newItem);

        JMenuItem resultsItem = new JMenuItem(Workarea.getString("results"));
        resultsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workarea.getInstance().createResults();
            }
        });
        resultsItem.setIcon(Icons.get("report_user"));
        resultsItem.setVisible(true);
        fileMenu.add(resultsItem);

        JMenuItem configurationItem = new JMenuItem(Workarea.getString("configuration"));
        configurationItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workarea.getInstance().createConfiguration();
            }
        });
        configurationItem.setIcon(Icons.get("cog"));
        configurationItem.setVisible(true);
        fileMenu.add(configurationItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem(Workarea.getString("exit"), 27);
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitItem.setIcon(Icons.get("cross"));
        exitItem.setVisible(true);
        fileMenu.add(exitItem);

        fileMenu.setVisible(true);

        menu.add(fileMenu);

        menu.add(new WindowMenu(desktop));
    }

    private void _initDesktop()
    {
        desktop.setVisible(true);
        add(desktop);
    }

    public static String getString(String str)
    {
        if (_locale == null) {
            _locale = ResourceBundle.getBundle("data");
        }
        String return_str = str;
        try {
            return_str = _locale.getString(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return return_str;
    }
}