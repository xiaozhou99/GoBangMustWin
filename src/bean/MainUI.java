package bean;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.SQLException;
import java.util.Stack;

public class MainUI extends JFrame {

    public MainUI() {
        initComponents();
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
        startBtn = new JButton();
        undoBtn = new JButton();
        showGamesBtn = new JButton();
        orderBtn = new JCheckBox();
        area = new JTextArea();
        gobangPanel = new GobangPanel(area);
        savaExcelBtn = new JButton("�������׵�Excel");
        VsMode = new ButtonGroup();
        jLabel2 = new JLabel("��սģʽ��");
        Man_ManBtn = new JRadioButton("˫��");
        Man_AIBtn = new JRadioButton("�˻�");
        settingTip = new JDialog(this, "��ս����", true);
        settingTip.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        settingTip.setSize(220, 170);
        settingTip.setLocationRelativeTo(gobangPanel);
        setting_ok = new JButton("ȷ��");


        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("AI�������˻�����");
        setPreferredSize(new java.awt.Dimension(900, 700));
        ImageIcon icon = new ImageIcon("src/image/logo.png");
        setIconImage(icon.getImage());
        setResizable(false);
        setLocation(250, 20);
        setVisible(true);

        GroupLayout panelLayout = new GroupLayout(gobangPanel);
        gobangPanel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 669, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE)
        );

        panel1.setBorder(BorderFactory.createTitledBorder("�������"));

        area.setColumns(15);
        area.setRows(5);
        area.setBorder(null);
        area.setEnabled(false);
        panel1.setViewportView(area);

        panel2.setBorder(BorderFactory.createTitledBorder("����"));

        jLabel1.setText("ѡ�����֣�");
        grp_alg.add(computerBtn);
        computerBtn.setText("����");
        computerBtn.setSelected(true);
        grp_alg.add(humanBtn);
        humanBtn.setText("��");
        VsMode.add(Man_ManBtn);
        VsMode.add(Man_AIBtn);
        Man_AIBtn.setSelected(true);

        startBtn.setText("��ʼ��Ϸ");
        undoBtn.setText("����");
        showGamesBtn.setText("�鿴��ʷ���");
        orderBtn.setText("��ʾ����˳��");
        startBtn.addActionListener(l);
        showGamesBtn.addActionListener(l);
        orderBtn.addActionListener(l);
        undoBtn.addActionListener(l);
        savaExcelBtn.addActionListener(e -> savaExcel());

        setting_ok.addActionListener(evt -> settingOk());


        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(startBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(undoBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(savaExcelBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)

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
                                .addComponent(startBtn)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                                .addComponent(undoBtn)
                                .addGap(18, 18, 18)
                                .addComponent(showGamesBtn)
                                .addGap(18, 18, 18)
                                .addComponent(savaExcelBtn)
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

        GroupLayout settingTipLayout = new GroupLayout(settingTip.getContentPane());
        settingTip.getContentPane().setLayout(settingTipLayout);
        settingTipLayout.setHorizontalGroup(
                settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingTipLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel1)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(computerBtn)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(humanBtn)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Man_AIBtn)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Man_ManBtn)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(25, 25, 25)

                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGap(50, 50, 50)
                                                .addComponent(setting_ok)

                                        ))
                                .addContainerGap())
        );
        settingTipLayout.setVerticalGroup(
                settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingTipLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(Man_AIBtn)
                                        .addComponent(Man_ManBtn))
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(computerBtn)
                                        .addComponent(humanBtn))
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(setting_ok)))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(gobangPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rightPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(rightPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(gobangPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();
    }

    //���������д��Excel��
    private void savaExcel() {
        String path = "src/resources/�������ֱ�ʤ�߷�.xlsx";
        Stack<Chess> data = gobangPanel.history;
        try {
            // ��ȡExcel�ĵ�
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("�ļ������ڣ��Ѵ���");
                file.createNewFile();

            }
            // Excel2003�汾������2003����ǰʹ��HSSFWorkbook�࣬��չ��Ϊ.xls
            // Excel2007�汾������2007���Ժ�ʹ��XSSFWorkbook�࣬��չ��Ϊ.xlsx

            // ������������
            FileInputStream in = new FileInputStream(path);
            XSSFWorkbook workBook = new XSSFWorkbook(in);

            // �������������ñ���
            // sheet ��Ӧһ������ҳ
            XSSFSheet sheet = workBook.getSheet("���ݱ�ע");
            if (sheet == null) {
                sheet = workBook.createSheet("���ݱ�ע");
            }
            int rowNumber = sheet.getLastRowNum();    // ��һ�д�0��ʼ��
            XSSFRow lastRow = sheet.getRow(rowNumber);
            if (!isRowEmpty(lastRow)) {
                rowNumber++;
            }
            // �����У��±�����һ��+1��ʼ
            XSSFRow row = sheet.createRow(rowNumber);

            int i = 0;
            for (Chess chess : gobangPanel.history) {
                String X = String.valueOf((char) (64 + chess.x));
                String Y = (16 - chess.y) + ":";
                String note = "������";
                row.createCell(i++).setCellValue(X + Y + note);
                System.out.println(X + Y + note);
            }

            // �����ļ��������׼��������ӱ����������У���������sheet�������κβ�����������Ч
            FileOutputStream out = new FileOutputStream(path);
            workBook.write(out);// �����ݵ�����Excel���
            out.close();
            workBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("���ݵ����ɹ�");
    }

    // �ж����Ƿ�Ϊ��
    public static boolean isRowEmpty(XSSFRow row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            XSSFCell cell = row.getCell(c);
            if (cell != null && (cell.getCellTypeEnum() != CellType.BLANK)) {//BLANK����հ�����
                return false;
            }
        }
        return true;
    }

    private void settingOk() {
        if (!(Man_ManBtn.isSelected() && computerBtn.isSelected())) {
            int VsMode = -1, initUser = -1;
            VsMode = Man_AIBtn.isSelected() ? GobangPanel.ManAI : GobangPanel.ManMan;
            initUser = humanBtn.isSelected() ? GobangPanel.HUMAN : GobangPanel.COMPUTER;
            gobangPanel.startGame(initUser, VsMode);
            settingTip.dispose();
        }

    }


    private ActionListener l = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == startBtn)//��ʼ��Ϸ
            {
                settingTip.setVisible(true);
            } else if (source == orderBtn) {   //��ʾ����˳��
                gobangPanel.toggleOrder();
            } else if (source == undoBtn) {    //����
                if(GobangPanel.VSMode==GobangPanel.ManAI)
                {
                    gobangPanel.undo();
                    gobangPanel.undo();
                }
                else {
                    gobangPanel.undo();
                }

            } else if (source == showGamesBtn) {   //��ʾ��ʷ���
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
                MainUI mainUI = new MainUI();
                mainUI.setVisible(true);

//                mainUI.settingTip.setVisible(true);//����ģʽ����

            }
        });
    }


    private static JTextArea area;//�������
    private JButton startBtn;//��ʼ��Ϸ
    private JCheckBox orderBtn;//��ʾ����˳��ť
    private JScrollPane panel1;//������̹������
    private JPanel panel2;//����
    private JPanel rightPane;//�ұ����
    private JButton showGamesBtn;//��ʾ���װ�ť
    private JButton undoBtn;//���尴ť
    private GobangPanel gobangPanel;//�������
    private JButton savaExcelBtn;//�������׵�Excel�ļ�

    private JDialog settingTip;
    private ButtonGroup grp_alg;//���ֵ�ѡ��
    private JRadioButton computerBtn;
    private JRadioButton humanBtn;
    private JLabel jLabel1;//ѡ�����ֱ�ǩ
    private ButtonGroup VsMode;//��սģʽ��ѡ��
    private JRadioButton Man_AIBtn;//�˻���ս
    private JRadioButton Man_ManBtn;//˫�˶�ս
    private JLabel jLabel2;//ѡ���սģʽ��ǩ
    private JButton setting_ok;
}

