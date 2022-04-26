package main;
import main.yang.GobangPanel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

// 历史棋谱列表对话框的显示类，历史棋谱即在resources目录下的黑棋先手必胜走法.xlsx
public class GameDialog extends JDialog {
    // 构造函数，参数parent表示其在MainUI的JFrame上显示
    public GameDialog(JFrame parent, boolean modal) throws Exception {
        super(parent,modal);
        initComponents();
    }

    // 各组件及变量初始化
    private void initComponents() throws Exception {
        // 变量初始化
        jScrollPane = new JScrollPane();
        table = new JTable();
        showManualBtn = new JButton();      //快速复盘
        slowshowManulBtn = new JButton();   //定时复盘
        backBtn = new JButton();
        panel = new GobangPanel();

        r = new DefaultTableCellRenderer();
        lineNum = getLineNum("src/resources/黑棋先手必胜走法.xlsx");
        tableData = new Object[lineNum][];
        for (int i = 0; i < lineNum; i++) {  // 初始化表格显示内容，即显示历史棋谱文件名
            tableData[i] = new Object[]{i+1};
        }

        // 设置表格内容居中显示
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class,r);
        table.getTableHeader().setDefaultRenderer(r);

        // 窗口、按钮属性设置
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //设置右上角关闭则对话框消失
        setTitle("历史棋谱列表");  //设置对话框标题
        setLocation(400,200);  //设置对话框在电脑屏幕上的显示位置，该坐标为对话框左上角坐标、
        slowshowManulBtn.setText("定时复盘");
        showManualBtn.setText("快速复盘");  //设置按钮显示文字
        backBtn.setText("返回");

        // 给按钮添加事件监听对象
        slowshowManulBtn.addActionListener(l);
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

        // 设置棋盘复现弹框的各组件布局
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(55,55,55)
                                .addComponent(slowshowManulBtn)
                                .addGap(50, 50, 50)
                                .addComponent(showManualBtn)
                                .addGap(55, 55, 55)
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
                                        .addComponent(showManualBtn)
                                        .addComponent(slowshowManulBtn))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }

    // 事件监听处理
    private ActionListener l = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();  //获取事件源
            if(source == backBtn){
                setUnseen();
            }
            else{
                int index = table.getSelectedRow();
                if(index == -1){
                    showMessage();  // 显示需选择记录的提示信息
                }
                else {
                    int chosenLine = (int) table.getValueAt(index,0);  // 获取选中的棋谱索引
                    panel.drawManual(source==showManualBtn,chosenLine);  // 调用GobangPanel类中的drawManual方法复现棋谱
                    setUnseen();  // 设置对话框消失
                }
            }
        }
    };


    //获取excel文件中总行数
    private int getLineNum(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("文件不存在！");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);  //获取文件
        XSSFSheet sheet = sheets.getSheetAt(0); //获取第一个工作簿
        return sheet.getPhysicalNumberOfRows();
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
    private JScrollPane jScrollPane;        // 滚动面板
    private JButton showManualBtn;          // 显示棋谱按钮
    private JButton slowshowManulBtn;       //模拟下棋按钮
    private JTable table;   // 显示历史棋谱的表格
    private int lineNum;  //excel文件总行数
    private Object [][]tableData;   // 用于加载到表格显示中的表格数据
    private DefaultTableCellRenderer r;  // 设置表格内容格式显示
    private GobangPanel panel;  // 面板对象

    private JRadioButtonMenuItem Man_Man;
}
