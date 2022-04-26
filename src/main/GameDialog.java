package main;
import main.yang.GobangPanel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

// ��ʷ�����б�Ի������ʾ�࣬��ʷ���׼���resourcesĿ¼�µĺ������ֱ�ʤ�߷�.xlsx
public class GameDialog extends JDialog {
    // ���캯��������parent��ʾ����MainUI��JFrame����ʾ
    public GameDialog(JFrame parent, boolean modal) throws Exception {
        super(parent,modal);
        initComponents();
    }

    // �������������ʼ��
    private void initComponents() throws Exception {
        // ������ʼ��
        jScrollPane = new JScrollPane();
        table = new JTable();
        showManualBtn = new JButton();      //���ٸ���
        slowshowManulBtn = new JButton();   //��ʱ����
        backBtn = new JButton();
        panel = new GobangPanel();

        r = new DefaultTableCellRenderer();
        lineNum = getLineNum("src/resources/�������ֱ�ʤ�߷�.xlsx");
        tableData = new Object[lineNum][];
        for (int i = 0; i < lineNum; i++) {  // ��ʼ�������ʾ���ݣ�����ʾ��ʷ�����ļ���
            tableData[i] = new Object[]{i+1};
        }

        // ���ñ�����ݾ�����ʾ
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class,r);
        table.getTableHeader().setDefaultRenderer(r);

        // ���ڡ���ť��������
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //�������Ͻǹر���Ի�����ʧ
        setTitle("��ʷ�����б�");  //���öԻ������
        setLocation(400,200);  //���öԻ����ڵ�����Ļ�ϵ���ʾλ�ã�������Ϊ�Ի������Ͻ����ꡢ
        slowshowManulBtn.setText("��ʱ����");
        showManualBtn.setText("���ٸ���");  //���ð�ť��ʾ����
        backBtn.setText("����");

        // ����ť����¼���������
        slowshowManulBtn.addActionListener(l);
        showManualBtn.addActionListener(l);
        backBtn.addActionListener(l);

        // ���ñ���ʽ
        table.setModel(new javax.swing.table.DefaultTableModel(
                tableData,  //�������
                new String [] {  //��ͷ����
                        "��ʷ����"
                }
        ) {
            Class[] types = new Class [] {  //�����������
                    java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {  //���ñ�񲻿ɱ༭
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

        // �������̸��ֵ���ĸ��������
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

    // �¼���������
    private ActionListener l = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();  //��ȡ�¼�Դ
            if(source == backBtn){
                setUnseen();
            }
            else{
                int index = table.getSelectedRow();
                if(index == -1){
                    showMessage();  // ��ʾ��ѡ���¼����ʾ��Ϣ
                }
                else {
                    int chosenLine = (int) table.getValueAt(index,0);  // ��ȡѡ�е���������
                    panel.drawManual(source==showManualBtn,chosenLine);  // ����GobangPanel���е�drawManual������������
                    setUnseen();  // ���öԻ�����ʧ
                }
            }
        }
    };


    //��ȡexcel�ļ���������
    private int getLineNum(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("�ļ������ڣ�");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);  //��ȡ�ļ�
        XSSFSheet sheet = sheets.getSheetAt(0); //��ȡ��һ��������
        return sheet.getPhysicalNumberOfRows();
    }

    // ��ʾ��ʾ��ѡ���¼����ʾ��Ϣ
    private void showMessage() {
        JOptionPane.showMessageDialog(this,"��ѡ��һ����¼��");
    }

    // ���öԻ�����ʧ
    public void setUnseen() {
        this.setVisible(false);
    }

    // ��������
    private JButton backBtn;  // ���ذ�ť
    private JScrollPane jScrollPane;        // �������
    private JButton showManualBtn;          // ��ʾ���װ�ť
    private JButton slowshowManulBtn;       //ģ�����尴ť
    private JTable table;   // ��ʾ��ʷ���׵ı��
    private int lineNum;  //excel�ļ�������
    private Object [][]tableData;   // ���ڼ��ص������ʾ�еı������
    private DefaultTableCellRenderer r;  // ���ñ�����ݸ�ʽ��ʾ
    private GobangPanel panel;  // ������

    private JRadioButtonMenuItem Man_Man;
}
