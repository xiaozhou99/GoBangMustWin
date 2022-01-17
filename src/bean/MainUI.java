package bean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class MainUI extends JFrame {

    public MainUI() {
        initComponents();
        int initUser = -1;

        if (humanBtn.isSelected())
            initUser = GobangPanel.HUMAN;
        else if (computerBtn.isSelected())
            initUser = GobangPanel.COMPUTER;

        panel.startGame(initUser);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        grp_alg = new ButtonGroup();
        rightPane = new JPanel();
        panel1 = new JScrollPane();
        area = new JTextArea();
        panel2 = new JPanel();
        jLabel1 = new JLabel();
        computerBtn = new JRadioButton();
        humanBtn = new JRadioButton();
        btn = new JButton();
        undoBtn = new JButton();
        showGamesBtn = new JButton();
        orderBtn = new JCheckBox();
        area = new JTextArea();
        panel = new GobangPanel(area);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AI五子棋人机博弈");
        setPreferredSize(new java.awt.Dimension(900, 700));
        ImageIcon icon = new ImageIcon("src/image/logo.png");
        setIconImage(icon.getImage());
        setResizable(false);
        setLocation(250,20);
        setVisible(true);

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 669, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );

        panel1.setBorder(BorderFactory.createTitledBorder("落棋过程"));

        area.setColumns(15);
        area.setRows(5);
        area.setBorder(null);
        area.setEnabled(false);
        panel1.setViewportView(area);

        panel2.setBorder(BorderFactory.createTitledBorder("操作"));

        jLabel1.setText("选择先手：");

        grp_alg.add(computerBtn);
        computerBtn.setText("电脑");
        computerBtn.setSelected(true);

        grp_alg.add(humanBtn);
        humanBtn.setText("人");
        btn.setText("开始游戏");
        undoBtn.setText("悔棋");
        showGamesBtn.setText("查看历史棋局");
        orderBtn.setText("显示落子顺序");

        btn.addActionListener(l);
        showGamesBtn.addActionListener(l);
        orderBtn.addActionListener(l);
        undoBtn.addActionListener(l);



        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(btn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(undoBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panel2Layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(computerBtn)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(humanBtn)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(showGamesBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(orderBtn)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(computerBtn)
                                        .addComponent(humanBtn))
                                .addGap(18, 18, 18)
                                .addComponent(btn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                                .addComponent(undoBtn)
                                .addGap(18, 18, 18)
                                .addComponent(showGamesBtn)
                                .addGap(18, 18, 18)
                                .addComponent(orderBtn)
                                .addGap(11, 11, 11))
        );

        GroupLayout rightPaneLayout = new GroupLayout(rightPane);
        rightPane.setLayout(rightPaneLayout);
        rightPaneLayout.setHorizontalGroup(
                rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, rightPaneLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rightPaneLayout.setVerticalGroup(
                rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rightPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(rightPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();
    }

    private ActionListener l = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == btn) {    //开始游戏
                int initUser = -1;

                if (humanBtn.isSelected())
                    initUser = GobangPanel.HUMAN;
                else if (computerBtn.isSelected())
                    initUser = GobangPanel.COMPUTER;

                panel.startGame(initUser);

            } else if (source == orderBtn) {   //显示落子顺序
                panel.toggleOrder();
            } else if (source == undoBtn) {    //悔棋
                panel.undo();
                panel.undo();
            } else if (source == showGamesBtn) {   //显示历史棋局
                try {
                    showGames();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    public void showGames() throws SQLException, ClassNotFoundException {

    }

    public static void appendText(String s) {
        area.append(s);
    }

    public static void clearText() {
        area.setText("");
    }


    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }


    private static JTextArea area;
    private JButton btn;
    private JRadioButton computerBtn;
    private ButtonGroup grp_alg;
    private JRadioButton humanBtn;
    private JLabel jLabel1;
    private JCheckBox orderBtn;
    private JScrollPane panel1;
    private JPanel panel2;
    private JPanel rightPane;
    private JButton showGamesBtn;
    private JButton undoBtn;
    private GobangPanel panel;
}

