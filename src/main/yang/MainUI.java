package main.yang;

import main.*;
import main.Chess;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang3.time.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class MainUI extends JFrame {
    Font f = new Font("����", Font.BOLD,18);
    public MainUI() throws Exception {
        initComponents();
        this.stopWatch = StopWatch.createStarted();     //���������ʱ����
        stopWatch.stop();
    }
    //����˽�г�Ա��������֤�������ݵ�һ����
    public static int iWidth;
    public static int iHeight;

    private JLabel n_timesLabel;        //ѡ������N���ǩ
    private JComboBox<String> n_times_value;    //����n��ѡ��������

    // Variables declaration - do not modify
    private JMenuBar jMenuBar;      //�˵�
    private JMenu jMenu1;           //��������
    private JRadioButtonMenuItem Man_AI;    //�˻���ս
    private JRadioButtonMenuItem Man_Man;   //���˶�ս
    private JRadioButtonMenuItem AI_AI;     //������ս
    private JMenu jMenu2;           //�Ծ�����
    private JMenu jMenu3;           //�������
    private JMenuItem VCFManul;          //VCF���׵�ѡ�˵���
    private JMenuItem historyManul;      //��ʷ���ײ˵���
    private JMenu jMenu4;           //����

    private panel panel1;       //��ʾ�Ծ���Ϣ
    private JLabel playerA;     //��������������ֱ�ǩ
    private JLabel playerB;
    private JLabel showA;       //������ʾ�����Ϣ
    private JLabel showB;
    private JTextField userAName;   //�������A����
    private JTextField userBName;
    private JTextField userATime;   //�������A��B ��ʱ����
    private JTextField userBTime;
    private JButton submitBtn;      //���������Ϣ�ύ��ť
    private JButton backBtn;        //���������Ϣ���ذ�ť
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JTextField jTextField4;
    private StopWatch stopWatch = null;     //ʵ�ּ�ʱ����
    private Timer timer = null;

    private GobangPanel gobangPanel;//�������
    private panel panel2;       //�������
    private panel rightPane;    //�ұ����
    private JButton startBtn;   //��ʼ��Ϸ
    private JButton undoBtn;    //���尴ť
    private JCheckBox orderBtn; //��ʾ����˳��ť
    private JButton saveExcelBtn;//�������׵�Excel�ļ�
    private JButton giveUpBtn;  //����
    private JButton gameOverBtn;//����
    private JButton VCFBtn;        //VCF�ҽ�

    private JDialog settingTip = new JDialog(this, "��������", true);       //�������õ���
    private JFrame settingUser = null;         //�������A��B�û�������
    private JLabel jLabel1;         //���Aѡ��ڰ����ӱ�ǩ
    private ButtonGroup grp_alg;    //ѡ�����ְ�ť��
    private JRadioButton A;         //���A B���ú��壨Ĭ�Ϻ������֣�
    private JRadioButton B;
    private JLabel jLabel2;         //����ѡ���ǩ
    private ButtonGroup VsMode_alg; //���޽��ְ�ť��
    private JRadioButton Jinshou;   //�н��֣�ָ�����֣�
    private JRadioButton Free;      //�޽��֣����ɿ��֣�
    private Label jLabel3;              //ѡ�񿪾ֱ�ǩ
//    private ButtonGroup Begin_alg;      //ָ�����ְ�ť��
//    private JRadioButton FreeBegin;     //���ɿ���
//    private JRadioButton DesignBegin;   //ָ������
    private JButton setting_ok;
    private JButton chooseBtn;


    @SuppressWarnings("unchecked")
    private void initComponents() throws Exception {
        //ҳ�������������
        jMenuBar = new JMenuBar();              //���ò˵����
        jMenu1 = new JMenu();
        jMenu2 = new JMenu();
        jMenu3 = new JMenu();
        jMenu4 = new JMenu();
        Man_AI = new JRadioButtonMenuItem();
        Man_AI.setSelected(true);               //Ĭ��ѡ���˻���ս
        Man_Man = new JRadioButtonMenuItem();
        AI_AI = new JRadioButtonMenuItem();

        VCFManul = new JMenuItem("VCF��ʤ����");
        historyManul = new JMenuItem("��ʷ����");

        playerA = new JLabel("���A");
        playerA.setBorder(BorderFactory.createEtchedBorder());
        playerA.setFont(f);
        playerA.setForeground(Color.black);
        playerB = new JLabel("���B");
        playerB.setBorder(BorderFactory.createEtchedBorder());
        playerB.setFont(f);
        playerB.setForeground(Color.black);

        //��嶥�˴������������ִ�����
        showA = new JLabel("���A");
        ImageIcon image1 = new ImageIcon("src/image/black.png");
        image1.setImage(image1.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));      //������ǩǰ���Ӵ�С
        showA.setIcon(image1);
        showA.setFont(f);
        showB = new JLabel("���B");
        ImageIcon image2 = new ImageIcon("src/image/white.png");//ʵ����ImageIcon ����
        image2.setImage(image2.getImage().getScaledInstance(30, 30,Image.SCALE_DEFAULT ));
        showB.setIcon(image2);
        showB.setFont(f);
        userATime = new JTextField("00:00",10);
        userBTime = new JTextField("00:00",10);
        userATime.setEditable(false);
        userBTime.setEditable(false);

        grp_alg = new ButtonGroup();    //ѡ���Ⱥ��ְ�ť��
        VsMode_alg = new ButtonGroup();     //���޽��ְ�ť��
        //Begin_alg = new ButtonGroup();

        rightPane = new panel();       //�����
        panel2 = new panel();           //�������

        startBtn = new JButton();
        undoBtn = new JButton();
        orderBtn = new JCheckBox();     //���ø�ѡ����ʾ����˳��

        saveExcelBtn = new JButton("�������׵�Excel");
        giveUpBtn = new JButton("����");
        gameOverBtn = new JButton("����");
        VCFBtn = new JButton("VCF�ҽ�");
        n_timesLabel = new JLabel("ѡ������N�������");
        n_times_value = new JComboBox<>();

        //��ť���á���������
        jLabel2 = new JLabel("����ѡ��");
        Free = new JRadioButton("�޽��֣����ɿ��֣�");
        Jinshou = new JRadioButton("�н��֣�ָ�����֣�");
        jLabel3 =  new Label();
//        FreeBegin = new JRadioButton();
//        DesignBegin = new JRadioButton();

        //������õ���
        jLabel1 = new JLabel();
        A = new JRadioButton();     //���A��B��ѡ��ť���,ѡ��ִ�����
        B = new JRadioButton();
        submitBtn = new JButton("�ύ");
        backBtn = new JButton("����");

        //��Ϸ���õ���
        settingTip = new JDialog(this, "��Ϸ����", true);
        settingTip.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        settingTip.setSize(400, 200);
        settingTip.setLocationRelativeTo(gobangPanel);
        setting_ok = new JButton("ȷ��");
        chooseBtn = new JButton("ѡ��ʽ����");
        n_times_value.setModel(new DefaultComboBoxModel<>(new String[]{"2", "3", "4", "5"}));
        //��ʾ�Ծ���Ϣ��������Ĭ����Ϣ����
        panel1 = new panel();
        jTextField1 = new JTextField("�˻���ս");
        jTextField2 = new JTextField("ָ������");
        jTextField3 = new JTextField("�н���");
        jTextField4 = new JTextField("����2��");

        //ͨ����ȡ��Ļ�ֱ�������������С
        Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
        iHeight = (screenSize.height*5)/6;
        iWidth = (screenSize.width*3)/5;
        System.out.println(iWidth);         //�����С�Ŀ���
        System.out.println(iHeight);
        setPreferredSize(new java.awt.Dimension(iWidth, iHeight));     //���ðٷֱȴ�С
        ImageIcon icon = new ImageIcon("src/image/logo.png");   //�������Ͻ���Ϸͼ��
        setIconImage(icon.getImage());
        setResizable(false);        //���ô����С�������ı�
        int h = screenSize.height/2- iHeight /2;     //���ȼ���
        int w = screenSize.width/2- iWidth /2;
        setBounds(w,h, iWidth, iHeight);            //���ô���̶���ʾ������

        gobangPanel = new GobangPanel(this, settingTip);    //�������崰��

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("��������������ϵͳ");
        setLocationRelativeTo(null);
        setVisible(true);

        //����������塢�������
        GroupLayout panelLayout = new GroupLayout(gobangPanel);
        gobangPanel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
                panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addGap(iWidth/8,iWidth/8,iWidth/8)
                                .addComponent(showA)
                                .addGap(iWidth/15,iWidth/15,iWidth/15)
                                .addComponent(userATime,javax.swing.GroupLayout.PREFERRED_SIZE,60,javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, iWidth/16, Short.MAX_VALUE)
                                .addComponent(showB)
                                .addGap(iWidth/15,iWidth/15,iWidth/15)
                                .addComponent(userBTime,javax.swing.GroupLayout.PREFERRED_SIZE,60,javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(58, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
                panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(showA, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(userATime,javax.swing.GroupLayout.PREFERRED_SIZE,50,javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(showB, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(userBTime,javax.swing.GroupLayout.PREFERRED_SIZE,50,javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(220, Short.MAX_VALUE))
        );
        //���öԾ���Ϣ��岼��
        panel1.setBorder(BorderFactory.createTitledBorder("�Ծ���Ϣ"));
        GroupLayout jPanel1Layout = new GroupLayout(panel1);
        panel1.setLayout(jPanel1Layout);
        System.out.println(iWidth);
        System.out.println(w);          //w=308
        System.out.println(h);          //h=72
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(15,15,15)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, iWidth/15,GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, iWidth/15,GroupLayout.PREFERRED_SIZE))
                                                .addGap(iWidth/30,iWidth/30,iWidth/30)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, iWidth/15, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, iWidth/15, GroupLayout.PREFERRED_SIZE))))
//                                        .addGroup(jPanel1Layout.createSequentialGroup()
//                                                .addGap(30, 30, 30)
//                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                                                        .addComponent(playerA)
//                                                        .addComponent(playerB))
//                                                .addGap(18, 18, 18)
//                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                                                        .addComponent(jl2)
//                                                        .addComponent(jl1))
//                                                .addGap(25, 25, 25)
//                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                                                        .addComponent(playerATime, javax.swing.GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
//                                                        .addComponent(playerBTime, javax.swing.GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(10,Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, iWidth/25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, iWidth/25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(iWidth/35,iWidth/35,iWidth/35)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE,iWidth/25 , javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, iWidth/25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(15, 15, 15)
//                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                                        .addComponent(playerA)
//                                        .addComponent(jl1)
//                                        .addComponent(playerATime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
//                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
//                                        .addGroup(jPanel1Layout.createSequentialGroup()
//                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
//                                                        .addComponent(playerB)
//                                                        .addComponent(jl2)
//                                                        .addComponent(playerBTime, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
//                                                .addGap(55, 55, 55))
//                                     )
                        )
        );
        pack();

        //���ò��������ع���
        panel2.setBorder(BorderFactory.createTitledBorder("����"));
        //���ÿ�ѡ��ť ���ֵ��ԡ��ˣ�Ĭ������Ϊ����
        jLabel1.setText("ִ���壺");
        grp_alg.add(A);
        A.setText("���A");
        A.setSelected(true);
        grp_alg.add(B);
        B.setText("���B");
        VsMode_alg.add(Free);
        VsMode_alg.add(Jinshou);
        Jinshou.setSelected(true);

        //�������ɿ��֡�ָ������
        jLabel3 = new Label();
        jLabel3.setText("����ģʽ��");
//        Begin_alg.add(FreeBegin);
//        Begin_alg.add(DesignBegin);
//        FreeBegin.setSelected(true);
//        FreeBegin.setText("���ɿ���");
//        DesignBegin.setText("ָ������");

        startBtn.setText("��ʼ");
        startBtn.setFont(f);
        undoBtn.setText("����");
        undoBtn.setFont(f);
        saveExcelBtn.setText("����");
        saveExcelBtn.setFont(f);
        orderBtn.setText("��ʾ����˳��");
        orderBtn.setFont(f);
        giveUpBtn.setText("����");
        giveUpBtn.setFont(f);
        gameOverBtn.setText("����");
        gameOverBtn.setFont(f);
        VCFBtn.setText("VCF�ҽ�");
        VCFBtn.setFont(f);

        //������
        startBtn.addActionListener(l);
        orderBtn.addActionListener(l);
        undoBtn.addActionListener(l);
        giveUpBtn.addActionListener(e -> giveUpBtn());
        saveExcelBtn.addActionListener(e -> saveExcel());
        setting_ok.addActionListener(evt -> settingOk());   //��������֡�ָ�����ֵ��¼���Ӧ
        chooseBtn.addActionListener(e -> choose());
        gameOverBtn.addActionListener(evt -> gameOver());
        submitBtn.addActionListener(e -> submit());
        Free.addChangeListener(e -> {
            n_timesLabel.setVisible(Free.isSelected()?false:true);  //����ѡ���޽��֣����ɿ��֣�����ʾ����N��
            n_times_value.setVisible(Free.isSelected()?false:true);
        });

        //ʹ����netbeans�����ò�����岼�ֽṹ
        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(startBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(undoBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(saveExcelBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(giveUpBtn,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(gameOverBtn,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(VCFBtn,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(orderBtn)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
                panel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(panel2Layout.createSequentialGroup()
                                .addGap(iHeight/30,iHeight/30,iHeight/30)
                                .addComponent(startBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                //.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                                .addComponent(undoBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(saveExcelBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(giveUpBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(gameOverBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(VCFBtn)
                                .addGap(iHeight/32,iHeight/32,iHeight/32)
                                .addComponent(orderBtn)
                                .addGap(iHeight/30,iHeight/30,iHeight/30))
        );
        GroupLayout rightPaneLayout = new GroupLayout(rightPane);
        rightPane.setLayout(rightPaneLayout);
        rightPaneLayout.setHorizontalGroup(
                rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, rightPaneLayout.createSequentialGroup()
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panel2, iWidth/4,iWidth/4, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(panel1, iWidth/4,iWidth/4, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        rightPaneLayout.setVerticalGroup(
                rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel1, iWidth/4, iWidth/4, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panel2, iWidth*3/4, iWidth*3/4, GroupLayout.PREFERRED_SIZE))
        );
        //��Ϸ���õ��򲼾ֽṹ
        GroupLayout settingTipLayout = new GroupLayout(settingTip.getContentPane());
        settingTip.getContentPane().setLayout(settingTipLayout);
        settingTipLayout.setHorizontalGroup(
                settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingTipLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                                        .addGroup(settingTipLayout.createSequentialGroup()
//                                                .addGap(18, 18, 18)
//                                                .addComponent(jLabel1)
//                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                                                .addComponent(A)
//                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                                                .addComponent(B)
//                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Jinshou)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Free)
                                                .addGap(0, Short.MAX_VALUE, Short.MAX_VALUE))
//                                        .addGroup(settingTipLayout.createSequentialGroup()
//                                                .addGap(8, 8, 8)
//                                                .addComponent(jLabel3)
//                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                                                .addComponent(FreeBegin)
//                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
//                                                .addComponent(DesignBegin)
//                                                .addGap(0, Short.MAX_VALUE, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(n_timesLabel)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(n_times_value)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(settingTipLayout.createSequentialGroup()
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGap(100, 100, 100)
                                                .addComponent(chooseBtn)
                                                .addGap(30, 30, 30)
                                                .addComponent(setting_ok)))
                                .addContainerGap())
        );
        settingTipLayout.setVerticalGroup(
                settingTipLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(settingTipLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
//                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
//                                        .addComponent(jLabel1)
//                                        .addComponent(A)
//                                        .addComponent(B))
//                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(Jinshou)
                                        .addComponent(Free))
                                .addGap(18, 18, 18)
//                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
//                                        .addComponent(jLabel3)
//                                        .addComponent(FreeBegin)
//                                        .addComponent(DesignBegin))
//                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(n_timesLabel)
                                        .addComponent(n_times_value))
                                .addGap(18, 18, 18)
                                .addGroup(settingTipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(chooseBtn)
                                                .addComponent(setting_ok))
                                .addGap(18, 18, 18))
        );

        //�˵���menu����
        jMenu1.setText("�Ծ�����");
        Man_AI.setText("�˻���ս");
        Man_AI.addActionListener(evt -> VsModeSelected(evt));
        jMenu1.add(Man_AI);
        Man_Man.setText("���˶�ս");
        Man_Man.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VsModeSelected(evt);
            }

        });
        jMenu1.add(Man_Man);
        AI_AI.setText("������ս");
        AI_AI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VsModeSelected(evt);
            }
        });
        jMenu1.add(AI_AI);
        jMenuBar.add(jMenu1);

        //��Ϸ���ò˵���
        jMenu2.setText("��������");
        jMenu2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jMenu2MouseClicked(evt);
            }
            private void jMenu2MouseClicked(MouseEvent evt) {
                settingTip.setVisible(true);
            }
        });
        jMenuBar.add(jMenu2);

        //��������
        jMenu3.setText("��������");
        jMenu3.add(VCFManul);
        jMenu3.add(historyManul);
            historyManul.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        showGames();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

            });
        jMenuBar.add(jMenu3);
        //����˵�������
        jMenu4.setText("����");
        jMenu4.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    showHelp();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        jMenuBar.add(jMenu4);
        setJMenuBar(jMenuBar);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(gobangPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rightPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(rightPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(gobangPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );
        pack();
    }

    /*��������˵�����������pdf�ĵ�*/
    private  void showHelp() throws IOException {
        File pdfFile = new File("src/resources/�й������徺������(2013��)-�й�������.pdf");
        try {
            Desktop.getDesktop().open(pdfFile);         //
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //���������д��Excel��
    private void saveExcel() {
        String path = "src/resources/�������ֱ�ʤ�߷�.xlsx";
        Stack<Chess> data = gobangPanel.history;
//        for (Chess c:data)
//            System.out.println(c.x);
        try {
            // ��ȡExcel�ĵ�
            File file = new File(path);
            System.out.println(file.getAbsolutePath());
            if (!file.exists()) {
                //����ļ������ڣ������հױ��
                XSSFWorkbook blankExcel = new XSSFWorkbook();
                FileOutputStream outBlank = new FileOutputStream(file);
                blankExcel.write(outBlank);
                outBlank.close();//�ر������
                System.out.println("�ļ������ڣ��Ѵ���");
            }
            // Excel2003�汾������2003����ǰʹ��HSSFWorkbook�࣬��չ��Ϊ.xls
            // Excel2007�汾������2007���Ժ�ʹ��XSSFWorkbook�࣬��չ��Ϊ.xlsx

            // ������������
            FileInputStream in = new FileInputStream(path);
            XSSFWorkbook workBook = new XSSFWorkbook(in);
            in.close();//�ر�������

            // sheet ��Ӧһ������ҳ
            XSSFSheet sheet = workBook.getSheet("���ݱ�ע");
            if (sheet == null) {//sheetΪ���򴴽�
                sheet = workBook.createSheet("���ݱ�ע");
            }
            sheet.setDefaultColumnWidth(17);//�����п�
            int rowNumber = sheet.getLastRowNum();    // ��һ�д�0��ʼ��
            XSSFRow lastRow = sheet.getRow(rowNumber);
            if (!isRowEmpty(lastRow)) {
                rowNumber++;
            }
            // �����У��±�����һ��+1��ʼ

            XSSFRow row = sheet.createRow(rowNumber);
            row.setHeightInPoints(24);//�����и�
            DataAnnotations dataAnnotations = new DataAnnotations();//�����������ݱ�ע��
            int i = 0;
            for (Chess chess : gobangPanel.history) {
                //��ȡ����
                String X = String.valueOf((char) (64 + chess.x));
                String Y = String.valueOf((16 - chess.y));

                //��ȡ���ݱ�ע
                String note = "-";
                String strGong = dataAnnotations.ScanBoard(chess, true);//�ж��ò���Ĺ�����Ϊ
                String strFang = dataAnnotations.ScanBoard(chess, false);//�ж��ò���ķ�����Ϊ
                note += dataAnnotations.Check(strGong, strFang);//���ݹ���������������յ����ݱ�ע
                
                row.createCell(i++).setCellValue(i + "." + X + Y + note);
                System.out.println(X + Y + note);
            }


            // �����ļ��������׼��������ӱ����������У���������sheet�������κβ�����������Ч
            FileOutputStream out = new FileOutputStream(path);
            workBook.write(out);// �����ݵ�����Excel���
            out.close();
            workBook.close();
            JOptionPane.showMessageDialog(this, "���ݵ����ɹ���");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "��������", 0);
        } catch (EmptyFileException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "��������", 0);
        }
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
    /*��ȡ�û�����ʾ��������*/
    private void submit(){
        if((!userAName.getText().equals("")) && (!userBName.getText().equals(""))){
            showA.setText(userAName.getText());
            showB.setText(userBName.getText());
            settingUser.setVisible(false);
        }
        else{
            JOptionPane.showMessageDialog(gobangPanel,"��������Ϣ", "����", JOptionPane.WARNING_MESSAGE);
            settingUser.setVisible(true);
        }

        showA.setText(A.isSelected()?userAName.getText():userBName.getText());
        showB.setText(A.isSelected()?userBName.getText():userAName.getText());
    }

    /*����������Ϸ����*/
    private void settingOk() {
        if(Free.isSelected()){
            jTextField4.setVisible(false);
        }
        else {
            jTextField4.setText("����"+(String) n_times_value.getSelectedItem()+"��");
            jTextField4.setFont(new Font("����", Font.BOLD,iWidth/70));
            jTextField4.setBackground(new Color(0xF0F6BE06, true));
            jTextField4.setEditable(false);
            jTextField4.setVisible(true);
        }

        jTextField2.setText(Jinshou.isSelected()?"ָ������":"���ɿ���");
        jTextField2.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField2.setBackground(new Color(0xF0F6BE06, true));
        jTextField2.setEditable(false);

        jTextField3.setText(Jinshou.isSelected()?"�н���":"�޽���");
        jTextField3.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField3.setBackground(new Color(0xF0F6BE06, true));
        jTextField3.setEditable(false);

        if (!(Free.isSelected() && A.isSelected())) {
            int VsMode = -1, initUser = -1;
            VsMode = Jinshou.isSelected() ? GobangPanel.ManAI : GobangPanel.ManMan;
            initUser = B.isSelected() ? GobangPanel.HUMAN : GobangPanel.COMPUTER;
            gobangPanel.startGame(initUser, VsMode);
            settingTip.dispose();
            restartCountTime();
            GobangPanel.isGameOver=false;
            startBtn.setEnabled(false);
        } else{
        }
    }
    /*ʵ��24��ָ������*/
    private void choose(){
        try {
            new Choose(this,true);
            setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //ʵ�ֶԾ����ã��ж����˻�/����/���˶�ս���û�������
    private void VsModeSelected(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        //System.out.println(evt);
        String mode = evt.getActionCommand();
        Man_AI.setSelected(Man_AI.getText().equals(mode));
        Man_Man.setSelected(Man_Man.getText().equals(mode));
        AI_AI.setSelected(AI_AI.getText().equals(mode));
        jTextField1.setText(mode);
        jTextField1.setFont(new Font("����", Font.BOLD,iWidth/70));
        jTextField1.setBackground(new Color(0xF0F6BE06, true));
        jTextField1.setEditable(false);
        settingUser = new  JFrame("�������");
        settingUser.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        settingUser.setLocationRelativeTo(gobangPanel);
        settingUser.setResizable(false);
        settingUser.setBounds(iWidth/2,iHeight/2,iWidth/2,iHeight/2);
        //��װ��¼�û���
        Box vBox = Box.createVerticalBox();
        if(userAName==null || userBName==null){
            userAName = new JTextField(10);
            userBName = new JTextField(10);
        }

        //ˮƽ��װ�û���
        Box ABox = Box.createHorizontalBox();
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/6));
        ABox.add(playerA);
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));    //��Ӽ������
        ABox.add(userAName);
        ABox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));


        Box BBox = Box.createHorizontalBox();
        BBox.add(Box.createHorizontalStrut(settingUser.getWidth()/6));
        BBox.add(playerB);
        BBox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));
        BBox.add(userBName);
        BBox.add(Box.createHorizontalStrut(settingUser.getWidth()/8));

        Box CBox = Box.createHorizontalBox();
        CBox.add(jLabel1);
        CBox.add(Box.createHorizontalStrut(settingUser.getWidth()/10));
        CBox.add(A);
        CBox.add(Box.createHorizontalStrut(settingUser.getWidth()/10));
        CBox.add(B);
        //��װ��ť
        Box  btnBox = Box.createHorizontalBox();
        btnBox.add(submitBtn);
        btnBox.add(Box.createHorizontalStrut(80));
        btnBox.add(backBtn);
        //��ֱ��װ
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(ABox);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(BBox);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(CBox);
        vBox.add(Box.createVerticalStrut(30));
        vBox.add(btnBox);
        vBox.add(Box.createVerticalStrut(30));
        settingUser.add(vBox);
        settingUser.pack();
        settingUser.setVisible(true);

//        submitBtn.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                if(A.equals("") &&B.equals("")){
//                    String A = userAName.getText();
//                    jl1.setText(A);
//                    String B = userBName.getText();
//                    jl2.setText(B);
//                    System.out.println(A);
//                    System.out.println(B);
//                    settingUser.setVisible(false);
//
//                }
//                else {
//                    System.out.println(1111);
//                    JOptionPane.showMessageDialog(gobangPanel,"����ȷ�����û�����", "����", JOptionPane.WARNING_MESSAGE);
//                }
//            }
//        });
        backBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                settingUser.setVisible(false);
            }
        });

}

    /*�������*/
    private void gameOver(){
        int n = JOptionPane.showConfirmDialog(gobangPanel,"ȷ�����䣿","ע��",JOptionPane.WARNING_MESSAGE);
        if(n==0){
            timer.cancel();
            GobangPanel.isGameOver=true;
            startBtn.setEnabled(true);
        }
    }
    /*�������*/
    private void giveUpBtn(){
        int n = JOptionPane.showConfirmDialog(gobangPanel,"ȷ�����壿","ע��",JOptionPane.WARNING_MESSAGE);
        if(n==0){
            timer.cancel();
            GobangPanel.isGameOver=true;
            startBtn.setEnabled(true);
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
                if (GobangPanel.VSMode == GobangPanel.ManAI) {  //�˻�����ʱ�����������������
                    if (MustWinGo.undoFlag == 2){ //=2������ǣ��ڱ�ʤ�����а����м��ֱ仯�����������˺���ӻ�Ӧ�Եģ���ʱ�����������
                        gobangPanel.undo();
                        gobangPanel.undo();
                    }
                    else {  //=1������ǣ��������£�ֻ��ڵ�ǰһ��
                        gobangPanel.undo();
                    }
                } else {    //������������������һ�λ���
                    gobangPanel.undo();
                }

            } else if (source == jMenu3) {   //��ʾ��ʷ���
                try {
                    showGames();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    public void restartCountTime() {
        int playerA_color = A.isSelected()?1:2;
        //int playerA_color = jl1.getText().equals("����") ?1:2;
        JTextField start =  playerA_color==gobangPanel.currentPlayer ?userATime : userBTime;
        JTextField stop = playerA_color==gobangPanel.currentPlayer ? userBTime : userATime;
        long stopTime = stopWatch.getTime();
        long stopMinutes = (stopTime / 1000) / 60;
        long stopSeconds = (stopTime / 1000) % 60;
        String str1 = stopMinutes < 10 ? "0" + stopMinutes : stopMinutes + " ";
        String str2 = stopSeconds < 10 ? "0" + stopSeconds : stopSeconds + " ";
        stop.setText(str1 + ":" + str2 + "\n");
        stopWatch.reset();
        stopWatch.start();
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {     //����ʱ�Ӻ���
                long startTime = stopWatch.getTime();
                long startMinutes = (startTime / 1000) / 60;
                long startSeconds = (startTime / 1000) % 60;
                String str1 = startMinutes < 10 ? "0" + startMinutes : startMinutes + " ";
                String str2 = startSeconds < 10 ? "0" + startSeconds : startSeconds + " ";
                start.setText(str1 + ":" + str2 + "\n");
                //System.out.println(stopWatch.getTime() / 1000);
            }
        }, 0, 1 * 1000);
    }

    public void showGames() throws Exception {
        new GameDialog(this,true).setVisible(true);
    }

    public static void appendText(String s) {
        //area.append(s);
    }

    //�Դ�����ʾ����У�Metal��������ɫ��ǳЩ����Nimbus
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
                MainUI mainUI = null;
                try {
                    mainUI = new MainUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mainUI.setVisible(true);

            }
        });
    }
}

