package bean;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;

// 历史棋谱列表对话框的显示类，历史棋谱存储在项目目录下的manual目录中，以保存时间为文件名（后续再根据情况修改）
public class GameDialog extends JDialog {
    // 构造函数，参数parent表示其在MainUI的JFrame上显示
    public GameDialog(JFrame parent, boolean modal) throws SQLException, ClassNotFoundException {
        super(parent,modal);
        initComponents();
    }

    // 各组件及变量初始化
    private void initComponents() {
        // 变量初始化
        jScrollPane = new JScrollPane();
        table = new JTable();
        showManualBtn = new JButton();
        backBtn = new JButton();
        panel = new GobangPanel();
        r = new DefaultTableCellRenderer();
        fileNameList = getFileNameList();  // 通过getFileNameList方法获取所有历史棋谱文件名
        tableData = new Object[fileNameList.length][];
        for (int i = 0; i < fileNameList.length; i++) {  // 初始化表格显示内容，即显示历史棋谱文件名
            tableData[i] = new Object[]{fileNameList[i]};
        }

        // 设置表格内容居中显示
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class,r);
        table.getTableHeader().setDefaultRenderer(r);

        // 窗口、按钮属性设置
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //设置右上角关闭则对话框消失
        setTitle("历史棋谱列表");  //设置对话框标题
        setLocation(400,200);  //设置对话框在电脑屏幕上的显示位置，该坐标为对话框左上角坐标
        showManualBtn.setText("显示棋谱");  //设置按钮显示文字
        backBtn.setText("返回");

        // 给按钮添加事件监听对象
        showManualBtn.addActionListener(l);
        backBtn.addActionListener(l);

        // 设置表格格式
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,  //表格内容
                new String [] {  //表头内容
                        "历史棋谱"
                }
        ) {
            Class[] types = new Class [] {  //表格数据类型
                    java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {  //设置表格不可编辑
                    false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setResizable(false);
        }

        // 设置界面上各组件布局
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(showManualBtn)
                                .addGap(94, 94, 94)
                                .addComponent(backBtn, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane, GroupLayout.Alignment.TRAILING)
        );
        layout.linkSize(SwingConstants.HORIZONTAL, new java.awt.Component[] {backBtn, showManualBtn});
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane, GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(backBtn)
                                        .addComponent(showManualBtn))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    // 事件监听处理
    private ActionListener l = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();  //获取事件源

            if (source == showManualBtn) {    // 显示棋谱按钮
                int index = table.getSelectedRow();  // 获取用户选中的棋谱序号，index从0开始
                if (index == -1){  // -1表示未选中
                    showMessage();  // 显示需选择记录的提示信息
                }
                else {
                    String manualPath = (String) table.getValueAt(index,0);  // 获取选中的棋谱文件名
                    panel.drawManual(manualPath);  // 调用GobangPanel类中的drawManual方法复现棋谱
                    setUnseen();  // 设置对话框消失
                }
            } else if (source == backBtn) {  // 返回按钮
                setUnseen(); // 设置对话框消失
            }
        }
    };

    // 获取历史棋谱文件名列表
    private String[] getFileNameList() {
        String path = "manual";  // manual目录保存棋谱文件
        File dir = new File(path);
        String[] result = null;  //返回结果列表

        if (dir.isDirectory()) {  // 先判断dir是否为目录类型
            result = dir.list();  // File类中的list方法可获取当前目录下的所有文件名，并以String[]形式返回
        }
        return result;
    }

    // 显示显示需选择记录的提示信息
    private void showMessage() {
        JOptionPane.showMessageDialog(this,"请选择一条记录！");
    }

    // 设置对话框消失
    public void setUnseen() {
        this.setVisible(false);
    }

    // 变量定义
    private JButton backBtn;  // 返回按钮
    private JScrollPane jScrollPane;  // 滚动面板
    private JButton showManualBtn;  // 显示棋谱按钮
    private JTable table;   // 显示历史棋谱的表格
    private String[] fileNameList;  // 保存历史棋谱文件名的列表
    private Object [][]tableData;   // 用于加载到表格显示中的表格数据
    private DefaultTableCellRenderer r;  // 设置表格内容格式显示
    private GobangPanel panel;  // 面板对象
}
