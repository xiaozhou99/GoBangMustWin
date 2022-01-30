package bean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class MainUI extends JFrame {

    // 构造函数
    public MainUI() throws Exception {
        initComponents();  //界面初始化

        int initUser = -1; //设置先手
        if (humanBtn.isSelected())  //根据界面选择的先手按相应先手开局，目前暂都设电脑先手
            initUser = GobangPanel.HUMAN;
        else if (computerBtn.isSelected())
            initUser = GobangPanel.COMPUTER;

        panel.startGame(initUser); //调用GobangPanel中的startGame开始游戏，默认界面一显示就自动开局
    }

    // 初始化界面及各组件
    private void initComponents() throws Exception {
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
        saveGameBtn = new JButton();
        orderBtn = new JCheckBox();
        area = new JTextArea();
        panel = new GobangPanel(area);  //面板对象初始化

        // 界面属性设置
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AI五子棋人机博弈");
        setPreferredSize(new Dimension(900, 700));
        ImageIcon icon = new ImageIcon("src/main/resources/image/logo.png");
        setIconImage(icon.getImage());
        setResizable(false);
        setLocation(250,20);
        setVisible(true);

        //组件布局设置
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
        saveGameBtn.setText("保存当前棋局");
        showGamesBtn.setText("查看历史棋局");
        orderBtn.setText("显示落子顺序");

        //给按钮添加事件监听
        btn.addActionListener(l);
        saveGameBtn.addActionListener(l);
        showGamesBtn.addActionListener(l);
        orderBtn.addActionListener(l);
        undoBtn.addActionListener(l);

        //布局
        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
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
                                                        .addComponent(showGamesBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(panel2Layout.createSequentialGroup()
                                                .addGap(39, 39, 39)
                                                .addComponent(orderBtn)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(panel2Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(saveGameBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(computerBtn)
                                        .addComponent(humanBtn))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                                .addComponent(undoBtn)
                                .addGap(12, 12, 12)
                                .addComponent(saveGameBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
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

        //布局
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

    // 按键事件监听
    private ActionListener l = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();  //获取事件源
            if (source == btn) {    //开始游戏
                int initUser = -1;
                if (humanBtn.isSelected())
                    initUser = GobangPanel.HUMAN;
                else if (computerBtn.isSelected())
                    initUser = GobangPanel.COMPUTER;

                panel.startGame(initUser);
            }
            else if (source == orderBtn) {   //显示落子顺序
                panel.toggleOrder();
            }
            else if (source == undoBtn) {    //悔棋
                if (MustWinGo.undoFlag == 2){ //=2的情况是，在必胜棋谱中白子有几种变化，即白子下了后黑子会应对的，此时悔棋需悔两步
                    panel.undo();
                    panel.undo();
                }
                else {  //=1的情况是，白子乱下，只需悔当前一步
                    panel.undo();
                }
            }
            else if (source == saveGameBtn) {   // 保存当前棋局
                try {
                    panel.writeManual();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else if (source == showGamesBtn) {   //显示历史棋局
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

    // 在界面右侧文本域中添加文本内容
    public static void appendText(String s) {
        area.append(s);
    }

    // 清空界面右侧文本域
    public static void clearText() {
        area.setText("");
    }

    // 显示历史棋谱对话框
    public void showGames() throws SQLException, ClassNotFoundException {
        new GameDialog(this,true).setVisible(true);
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
                try {
                    new MainUI().setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static JTextArea area; //右侧文本域
    private JButton btn; //开始游戏按钮
    private ButtonGroup grp_alg; //先手选择
    private JRadioButton computerBtn;
    private JRadioButton humanBtn;
    private JLabel jLabel1;
    public static JCheckBox orderBtn; //是否显示落子顺序复选框
    private JScrollPane panel1;
    private JPanel panel2;
    private JPanel rightPane;
    private JButton saveGameBtn;  //保存当前棋局按钮
    private JButton showGamesBtn; //显示历史棋谱按钮
    private JButton undoBtn;  //悔棋按钮
    private GobangPanel panel; //GobangPanel对象
}

