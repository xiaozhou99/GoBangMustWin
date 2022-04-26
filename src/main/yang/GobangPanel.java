package main.yang;

import main.Chess;
import main.MustWinGo;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Timer;

public class GobangPanel extends JPanel  {
    private static final long serialVersionUID = 667503661521167626L;
    private static final int OFFSET = 70;// ����ƫ��
    private static int CELL_WIDTH = MainUI.iWidth/25;// �����
    public static final int BOARD_SIZE = 15;// ���̸���
    public static final int BT = BOARD_SIZE + 2;
    public static final int CENTER = BOARD_SIZE / 2 + 1;// ���ĵ�
    public static final int BLACK = 1;//����
    public static final int WHITE = 2;//����
    public static final int BORDER = -1;//�߽�
    public static final int EMPTY = 0;//����������

    public static final int ManMan = 0;// ˫��ģʽ
    public static final int ManAI = 1;// �˻�ģʽ

    public static final int HUMAN = 3;//������
    public static final int COMPUTER = 4;//���������

    public static int N = -10;//��ȡ�û������N��
    public static int pp = 0;//����N��ѭ��

    /**
     * history�洢�������������� ������ ����ʱ��
     * boardStatus�洢��ǰ���  �ж�ĳλ���Ƿ�����  �������� ��������ʱ��  ���������������ʣ�
     */
    public static Stack<Chess> history;// ������ʷ��¼��������������������
    public static int[][] boardData;//��ǰ�����̾��棬EMPTY��ʾ���ӣ�BLACK��ʾ���ӣ�WHITE��ʾ���ӣ�BORDER��ʾ�߽�
    private int[] lastStep;// ��һ�����ӵ㣬����Ϊ2�����飬��¼��һ�����ӵ������

    public static int currentPlayer = 0;            // ��ǰ���ִ����ɫ��Ĭ��Ϊ����
    private static int computerSide = BLACK;        // Ĭ�ϻ����ֺ�
    private static int humanSide = WHITE;
    private static boolean isShowOrder = false;     // ��ʾ����˳��
    public static boolean isGameOver = true;
    public static int initUser;     // ����
    public static int VSMode;       //��սģʽ,ManMan=0 ����˫�˶�ս��ManAI=1�����˻���ս

    private JDialog settingTip;
    private MainUI mainUI=null;

    private static boolean isShowManual = false; //�Ƿ��ǻ�����
    private boolean isAppendText = true;
    private MustWinGo mustWinGo;//��ʤ������
    private Chess AIGoChess;//��������ӵ�

    private int minx, maxx, miny, maxy; // ��ǰ������������ӵ���Сx�����x����Сy�����y��������С�������ӵ�ķ�Χ

    private int cx = CENTER, cy = CENTER;

    public GobangPanel(){};
    public GobangPanel(MainUI mainUI, JDialog settingTip) throws Exception {
        boardData = new int[BT][BT];    //1-15�洢����״̬��0 16Ϊ�߽�
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// �߽�
            }
        }
        history = new Stack<>();
        lastStep = new int[2];
        mustWinGo = new MustWinGo();
        this.settingTip = settingTip;
        this.mainUI=mainUI;
        addMouseMotionListener(mouseMotionListener);
        addMouseListener(mouseListener);
        setPreferredSize(new Dimension(getWidth(), getHeight()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    repaint();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    // �Ƿ���ʾ����˳��
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
    }

    // ����
    public void undo() {
        if (!history.isEmpty()) {
            if (history.size() == 1 && initUser == COMPUTER) {
                return;
            }
            Chess p1 = history.pop();
            boardData[p1.x][p1.y] = EMPTY;
            if (!history.isEmpty()) {
                Chess chess = history.peek();
                lastStep[0] = chess.x;
                lastStep[1] = chess.y;
            }
            togglePlayer();
            if (VSMode == ManAI) {
                mustWinGo.goback();
            }
        } else {
            lastStep[0] = BORDER;
            lastStep[1] = BORDER;
        }
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("April", Font.BOLD, 12));
        //�������Ϣ
        //g2d.drawString("���A��",100,0);

        // ������ͼ
        ImageIcon icon = new ImageIcon("src/image/background.jpg");
        g2d.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);
        // ������
        drawBoard(g2d);
        // ����Ԫ����
        drawStar(g2d, CENTER, CENTER);          //��������15*15��������л���
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) * 3 / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) * 3 / 4);

        // �����ֺ���ĸ
        drawNumAndLetter(g2d);
        if (!isShowManual) {   //���Ǹ������ף�����������
            // ����ʾ��
            drawCell(g2d, cx, cy);
            if (!isGameOver) {
                // ����������
                for (Chess chess : history) {
                    drawChess(g2d, chess.x, chess.y, chess.color);
                }
                // ��˳��
                if (isShowOrder) {
                    drawOrder(g2d);
                } else {    // ����һ�����ӵ������ʾ
                    if (lastStep[0] > 0 && lastStep[1] > 0) {
                        g2d.setColor(Color.RED);
                        g2d.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSET
                                        - CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
                                        + OFFSET - CELL_WIDTH / 10, CELL_WIDTH / 5,
                                CELL_WIDTH / 5);
                    }
                }
            }
        } else {   //����
            // ����������
            for (Chess chess : history) {
                drawChess(g2d, chess.x, chess.y, chess.color);
            }
            drawOrder(g2d);
        }

    }

    // ������
    private void drawBoard(Graphics g2d) {
        for (int x = 0; x < BOARD_SIZE; ++x) {   // ������
            g2d.drawLine(x * CELL_WIDTH + OFFSET, OFFSET, x * CELL_WIDTH
                    + OFFSET, (BOARD_SIZE - 1) * CELL_WIDTH + OFFSET);

        }
        for (int y = 0; y < BOARD_SIZE; ++y) {   // ������
            g2d.drawLine(OFFSET, y * CELL_WIDTH + OFFSET,
                    (BOARD_SIZE - 1) * CELL_WIDTH + OFFSET, y
                            * CELL_WIDTH + OFFSET);

        }
    }

    // ����Ԫ����
    private void drawStar(Graphics g2d, int cx, int cy) {
        g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSET - 4, (cy - 1) * CELL_WIDTH
                + OFFSET - 4, 8, 8);
    }

    // �����ֺ���ĸ
    private void drawNumAndLetter(Graphics g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth, stringAscent;
        stringAscent = fm.getAscent();
        for (int i = 1; i <= BOARD_SIZE; i++) {

            String num = String.valueOf(BOARD_SIZE - i + 1);
            stringWidth = fm.stringWidth(num);
            g2d.drawString(String.valueOf(BOARD_SIZE - i + 1), OFFSET / 4    // ������
                    - stringWidth / 2, OFFSET + (CELL_WIDTH * (i - 1))
                    + stringAscent / 2);

            String letter = String.valueOf((char) (64 + i));
            stringWidth = fm.stringWidth(letter);
            g2d.drawString(String.valueOf((char) (64 + i)), OFFSET    //����ĸ
                    + (CELL_WIDTH * (i - 1)) - stringWidth / 2, OFFSET * 3 / 4
                    + OFFSET + CELL_WIDTH * (BOARD_SIZE - 1)
                    + stringAscent / 2);
        }
    }

    // ������
    private void drawChess(Graphics g2d, int cx, int cy, int color) {
        if (color == 0)
            return;
        int size = CELL_WIDTH * 5 / 6;
        if (color == BLACK) {
            ImageIcon icon1 = new ImageIcon("src/image/black.png");
            Image image1 = icon1.getImage();
            g2d.drawImage(image1, (cx - 1) * CELL_WIDTH + OFFSET - size / 2, (cy - 1) * CELL_WIDTH - size / 2 + OFFSET, size, size, null);
        }
        else if(color == WHITE){
            ImageIcon icon2 = new ImageIcon("src/image/white.png");
            Image image2 = icon2.getImage();
            g2d.drawImage(image2, (cx - 1) * CELL_WIDTH + OFFSET - size / 2, (cy - 1) * CELL_WIDTH - size / 2 + OFFSET, size, size,this);
        }
    }

    // ��Ԥѡ��
    private void drawCell(Graphics g2d, int x, int y) {
        int length = CELL_WIDTH / 4;
        int xx = (x - 1) * CELL_WIDTH + OFFSET;
        int yy = (y - 1) * CELL_WIDTH + OFFSET;
        int x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = x4 = xx - CELL_WIDTH / 2;
        x2 = x3 = xx + CELL_WIDTH / 2;
        y1 = y2 = yy - CELL_WIDTH / 2;
        y3 = y4 = yy + CELL_WIDTH / 2;
        g2d.setColor(Color.RED);
        g2d.drawLine(x1, y1, x1 + length, y1);
        g2d.drawLine(x1, y1, x1, y1 + length);
        g2d.drawLine(x2, y2, x2 - length, y2);
        g2d.drawLine(x2, y2, x2, y2 + length);
        g2d.drawLine(x3, y3, x3 - length, y3);
        g2d.drawLine(x3, y3, x3, y3 - length);
        g2d.drawLine(x4, y4, x4 + length, y4);
        g2d.drawLine(x4, y4, x4, y4 - length);
    }

    // ������˳��
    private void drawOrder(Graphics g2d) {
        if (history.size() > 0) {
            g2d.setColor(Color.RED);
            int i = 0;
            for (Chess chess : history) {
                int x = chess.x;
                int y = chess.y;
                String text = String.valueOf(i + 1);
                // ����
                FontMetrics fm = g2d.getFontMetrics();
                int stringWidth = fm.stringWidth(text);
                int stringAscent = fm.getAscent();
                g2d.drawString(text, (x - 1) * CELL_WIDTH + OFFSET - stringWidth / 2,
                        (y - 1) * CELL_WIDTH + OFFSET + stringAscent / 2);
                i++;
            }
        }
    }


    // ��ʼ��Ϸ
    public void startGame(int initUser, int VSMode) {

        this.initUser = initUser;
        this.VSMode = VSMode;
        this.reset();
        isGameOver = false;
        isShowManual = false;
        isAppendText = true;
        mustWinGo.startGame();
        if (VSMode == ManAI)//�˻�
        {
            if (initUser == COMPUTER) {//��������
                currentPlayer = Chess.BLACK;// Ĭ�Ϻ�������
                Chess chess = mustWinGo.AIGo();
                putChess(chess.x, chess.y);// Ĭ�ϵ�һ����������
                minx = maxx = miny = maxy = chess.x;

            } else {
                currentPlayer = Chess.WHITE;
            }
        } else {
            currentPlayer = Chess.BLACK;
        }
        N = -10;//��������N���FLAG
        pp = 0;

    }

    // ����ƶ�
    private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
            int tx = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;       //Math.round������Ĭ�ϼ���0.5֮������ȡ����
            int ty = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            if (tx != cx || ty != cy) {
                if (tx >= 1 && tx <= (BOARD_SIZE+OFFSET) && ty >= 1
                        && ty <= (BOARD_SIZE+OFFSET)) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));      //Cursor�Ƿ�װ������λͼ��ʾ��ʽ����,HAND��״�������
                } else
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                cx = tx;
                cy = ty;
            }
        }
    };

    // ���������
    private MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (isGameOver) {
                settingTip.setVisible(true);
                return;
            }
            int x = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            int y = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;

            if (cx >= 1 && cx <= BOARD_SIZE && cy >= 1 && cy <= BOARD_SIZE) {
                int mods = e.getModifiers();
                if ((mods & InputEvent.BUTTON1_MASK) != 0) {// ������
                    if (putChess(x, y)) {
                        if (VSMode == ManAI) {
                            mustWinGo.PlayerGo(x, 16 - y);
                            AIGoChess = mustWinGo.AIGo();
                            putChess(AIGoChess.x, 16 - AIGoChess.y);
                        }
                    }
                }
            }
        }
        //}
    };

    /**
     * ���Ӻ���
     *
     * @param x
     * @param y
     * @return
     */
    private boolean putChess(int x, int y) {
        //����N��
        if (VSMode == ManMan) {//����ģʽ
            FiveBeat(x, y);
        }

        if (boardData[x][y] == EMPTY) {
            // ����������Χ����
            minx = Math.min(minx, x);
            maxx = Math.max(maxx, x);
            miny = Math.min(miny, y);
            maxy = Math.max(maxy, y);
            boardData[x][y] = currentPlayer;
            history.push(new Chess(x, y, currentPlayer));

            if (isAppendText) {
                String str = currentPlayer == WHITE ? "���壺" : "���壺";
                MainUI.appendText(str + "��" + (char) (64 + x) + (16 - y) + "��\n");
                System.out.println(str + " ��" + (char) (64 + x) + (16 - y) + "��");
            }

            lastStep[0] = x;// ������һ�����ӵ�
            lastStep[1] = y;

            togglePlayer();
            mainUI.restartCountTime();
            //���ֽ���
            if (VSMode == ManMan) {
                threeChange(x, y);
            }

            int winSide = isGameOver(boardData[x][y]);// �ж��վ�
            if (winSide > 0) {

                if (winSide == WHITE) {
                    MainUI.appendText("�׷�Ӯ�ˣ�\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "�׷�Ӯ�ˣ�");
                } else if (winSide == BLACK) {
                    MainUI.appendText("�ڷ�Ӯ�ˣ�\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "�ڷ�Ӯ�ˣ�");
                } else {
                    MainUI.appendText("˫��ƽ�֣�\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "˫��ƽ��");
                }

                return false;
            }
            return true;
        }
        return false;
    }

    void togglePlayer() {
        currentPlayer = 3 - currentPlayer;
    }

    //��������״̬
    public void reset() {
        for (int i = 1; i < BT - 1; i++)
            for (int j = 1; j < BT - 1; j++) {
                boardData[i][j] = EMPTY;
            }
        history.clear();
    }

    /**
     * �ж�����Ƿ����
     *
     * @return 0�������У�1������Ӯ��2������Ӯ��3��ƽ��
     */
    public int isGameOver(int color) {
        if (!history.isEmpty()) {

            Chess lastStep = history.peek();
            int x = lastStep.getX();
            int y = lastStep.getY();

            //��������Ƿ��н���
            Chess firstStep = history.get(0);
            Chess step = history.get(history.size() - 1);
            if (step.getColor() == firstStep.getColor()) {
                int i = forbid(step.getColor());//�жϽ��ֵ�����
                if (step.getColor() == 1) {
                    if (i == 1) {
                        MainUI.appendText("�����������֣�����ʧ��\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "�����������֣�����ʧ��!");
                    }
                    if (i == 2) {
                        MainUI.appendText("�������Ľ��֣�����ʧ��\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "�������Ľ��֣�����ʧ��!");
                    }
                    if (i == 3) {
                        MainUI.appendText("���ֳ������֣�����ʧ��\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "���ֳ������֣�����ʧ��!");
                        return 777;//��������
                    }
                }
                if (step.getColor() == 2) {
                    if (i == 1) {
                        MainUI.appendText("�����������֣�����ʧ��\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "�����������֣�����ʧ��!");
                    }
                    if (i == 2) {
                        MainUI.appendText("�������Ľ��֣�����ʧ��\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "�������Ľ��֣�����ʧ��!");
                    }
                    if (i == 3) {
                        MainUI.appendText("���ֳ������֣�����ʧ��\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "���ֳ������֣�����ʧ��!");
                        return 777;//��������
                    }
                }
            }

            // �ĸ�����
            if (check(x, y, 1, 0, color) + check(x, y, -1, 0, color) >= 4 ||
                    check(x, y, 0, 1, color) + check(x, y, 0, -1, color) >= 4 ||
                    check(x, y, 1, 1, color) + check(x, y, -1, -1, color) >= 4 ||
                    check(x, y, 1, -1, color) + check(x, y, -1, 1, color) >= 4) {
                return color;
            }

        }

        // ������
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j)
                if (boardData[i][j] == EMPTY) {
                    return 0;
                }
        }
        return 3;
    }

    // ������ͬ��ɫ���ӵ�����
    private int check(int x, int y, int dx, int dy, int color) {
        int sum = 0;
        for (int i = 0; i < 4; ++i) {
            x += dx;
            y += dy;
            if (x < 1 || x > BOARD_SIZE || y < 1 || y > BOARD_SIZE) {
                break;
            }
            if (boardData[x][y] == color) {
                sum++;
            } else {
                break;
            }
        }
        return sum;
    }

    //�����ж�
    public int forbid(int color) {//�����������ӵ���ɫ
        int k = 0;//��������
        int m = 0;//1Ϊ�������֣�2Ϊ���Ľ��֣�3Ϊ��������
        int i = 0;//��������
        int j = 0;//����Ƿ��г������
        //��������
        for (int x = 1; x < 16; x++) {
            for (int y = 1; y < 16; y++) {//ѭ���������̵�ÿһ������
                if (boardData[x][y] == color) {
                    //ˮƽ���򣬰������������ģ�����
                    if (boardData[x - 1][y] == color) {
                        if (boardData[x - 2][y] == color) {
                            if (boardData[x - 3][y] == 0) {
                                if (boardData[x + 1][y] == 0) {
                                    k++;//��������,����ļ���Ҳ�ǣ����������������֣���Ϊ����
                                }
                            }
                            if (boardData[x - 3][y] == color) {
                                if (boardData[x - 4][y] == 0 && boardData[x + 1][y] == 0) {
                                    i++;//���Ľ��֣���ͬ�������������ĳ��֣���Ϊ����
                                }
                                if (boardData[x - 4][y] == color && boardData[x - 5][y] == color) {
                                    j++;//��������

                                }
                            }
                        } else if (boardData[x + 1][y] == color) {
                            if (boardData[x + 2][y] == 0) {
                                if (boardData[x - 2][y] == 0) {
                                    k++;
                                }
                                if (boardData[x - 2][y] == color && boardData[x - 3][y] == 0) {
                                    i++;
                                }
                            }
                            if (boardData[x + 2][y] == color && boardData[x + 3][y] == 0 && boardData[x - 2][y] == 0) {
                                i++;
                            }

                        }
                    }
                    if (boardData[x - 1][y] == 0) {
                        if (boardData[x + 1][y] == color) {
                            if (boardData[x + 2][y] == color) {
                                if (boardData[x + 3][y] == 0) {
                                    k++;
                                }
                                if (boardData[x + 3][y] == color) {
                                    if (boardData[x + 4][y] == 0) {
                                        i++;
                                    }
                                }
                            }
                        }

                    }
                    //��ֱ����
                    if (boardData[x][y - 1] == color) {
                        if (boardData[x][y - 2] == color) {
                            if (boardData[x][y - 3] == 0) {
                                if (boardData[x][y + 1] == 0) {
                                    k++;
                                }
                            }
                            if (boardData[x][y - 3] == color && boardData[x][y - 4] == 0 && boardData[x][y + 1] == 0) {
                                i++;
                            }
                            if (boardData[x][y - 3] == color && boardData[x][y - 4] == color && boardData[x][y - 5] == color) {
                                j++;
                            }
                        } else if (boardData[x][y + 1] == color) {
                            if (boardData[x][y + 2] == 0) {
                                if (boardData[x][y - 2] == 0) {
                                    k++;
                                }
                                if (boardData[x][y - 2] == color && boardData[x][y - 3] == 0) {
                                    i++;

                                }
                            }
                            if (boardData[x][y + 2] == color && boardData[x][y + 3] == 0 && boardData[x][y - 2] == 0) {
                                i++;
                            }
                        }

                    }
                    if (boardData[x][y - 1] == 0) {
                        if (boardData[x][y + 1] == color) {
                            if (boardData[x][y + 2] == color) {
                                if (boardData[x][y + 3] == 0) {
                                    k++;
                                }
                                if (boardData[x][y + 3] == color && boardData[x][y + 4] == 0) {
                                    i++;
                                }
                            }
                        }
                    }
                    //б����1
                    if (boardData[x - 1][y - 1] == color) {
                        if (boardData[x - 2][y - 2] == color) {
                            if (boardData[x - 3][y - 3] == 0) {
                                if (boardData[x + 1][y + 1] == 0) {
                                    k++;
                                }
                            }
                            if (boardData[x - 3][y - 3] == color) {
                                if (boardData[x - 4][y - 4] == 0 && boardData[x + 1][y + 1] == 0) {
                                    i++;
                                }
                                if (boardData[x - 4][y - 4] == color && boardData[x - 5][y - 5] == color) {
                                    j++;
                                }
                            }
                        } else if (boardData[x + 1][y + 1] == color) {
                            if (boardData[x + 2][y + 2] == 0) {
                                if (boardData[x - 2][y - 2] == 0) {
                                    k++;
                                    if (boardData[x - 2][y - 2] == color && boardData[x - 3][y - 3] == 0) {
                                        i++;
                                    }
                                }
                            }
                            if (boardData[x + 2][y + 2] == color && boardData[x + 3][y + 3] == 0 && boardData[x - 2][y - 2] == 0) {
                                i++;
                            }
                        }
                    }
                    if (boardData[x - 1][y - 1] == 0) {
                        if (boardData[x + 1][y + 1] == color) {
                            if (boardData[x + 2][y + 2] == color) {
                                if (boardData[x + 3][y + 3] == 0) {
                                    k++;
                                }
                                if (boardData[x + 3][y + 3] == color && boardData[x + 4][y + 4] == 0) {
                                    i++;
                                }
                            }
                        }
                    }
                    //б����2
                    if (boardData[x - 1][y + 1] == color) {
                        if (boardData[x - 2][y + 2] == color) {
                            if (boardData[x - 3][y + 3] == 0) {
                                if (boardData[x + 1][y - 1] == 0) {
                                    k++;
                                }
                            }
                            if (boardData[x - 3][y + 3] == color && boardData[x - 4][y + 4] == 0 && boardData[x + 1][y - 1] == 0) {
                                i++;
                            }
                            if (boardData[x - 3][y + 3] == color && boardData[x - 4][y + 4] == color && boardData[x - 5][y + 5] == color) {
                                j++;
                            }
                        } else if (boardData[x - 2][y + 2] == 0) {
                            if (boardData[x + 1][y - 1] == color) {
                                if (boardData[x + 2][y - 2] == 0) {
                                    k++;
                                }
                                if (boardData[x + 2][y - 2] == color && boardData[x + 3][y - 3] == 0) {
                                    i++;
                                }
                            }
                        }
                        if (boardData[x - 2][y + 2] == color && boardData[x - 3][y + 3] == 0 && boardData[x + 1][y - 1] == color && boardData[x + 2][y - 2] == 0) {
                            i++;
                        }

                    }
                    if (boardData[x - 1][y + 1] == 0) {
                        if (boardData[x + 1][y - 1] == color) {
                            if (boardData[x + 2][y - 2] == color) {
                                if (boardData[x + 3][y - 3] == 0) {
                                    k++;
                                }
                                if (boardData[x + 3][y - 3] == color && boardData[x + 4][y - 4] == 0) {
                                    i++;
                                }
                            }
                        }

                    }
                    if (k >= 2) {
                        m = 1;//������������
                    }
                    if (i >= 2) {
                        m = 2;//�������Ľ���
                    }
                    if (j >= 1) {
                        m = 3;//���ֳ�������
                    }
                    k = 0;
                    i = 0;//������һ��������ã��ٱ�����һ����

                }
            }
        }
        return m;//���ݷ���ֵ˵�����������ģ���������
    }

    public void FiveBeat(int x, int y) {
        if (pp == 100) {
            while (history.size() > 4) {
                Chess p1 = history.pop();
                boardData[p1.x][p1.y] = EMPTY;
            }
            boardData[x][y] = currentPlayer;
            history.push(new Chess(x, y, currentPlayer));
            pp++;
            togglePlayer();
        }
        //repaint();
    }

    public void threeChange(int x, int y) {
        if (history.size() == 3) {
            exchangeColor(x, y);
        }
        //N��
        if (history.size() == 4) {
            N = Integer.parseInt(JOptionPane.showInputDialog(null, "����������N��Ĵ�����"));
        }
        if (pp < N + 1) {
            currentPlayer = history.get(0).getColor();
            pp++;
            System.out.println(pp);
        }
        if (pp == N + 1) {
            JOptionPane.showMessageDialog(GobangPanel.this, "��ѡ�����µ����ӣ�");
            pp = 100;
            currentPlayer = history.get(0).getColor();
        }
    }

    //���ֽ���
    public void exchangeColor(int x, int y) {
        if (history.get(0).getColor() == 1) {
            //1�Ǻ���
            int op = JOptionPane.showConfirmDialog(GobangPanel.this, "��ѡ���Ƿ����ֽ���", "���ֽ���", JOptionPane.YES_NO_OPTION);
            //����ǣ��������Ǻ����£���һ������ף������
            if (op == 0) {
                history.get(0).setColor(2);
                history.get(1).setColor(1);
                history.get(2).setColor(2);
                boardData[history.get(0).getX()][history.get(0).getY()] = WHITE;
                boardData[history.get(1).getX()][history.get(1).getY()] = BLACK;
                boardData[history.get(2).getX()][history.get(2).getY()] = WHITE;
                currentPlayer = BLACK;
                return;

            }
        }
        if (history.get(0).getColor() == 2) {
            //2�ǰ���
            int op = JOptionPane.showConfirmDialog(GobangPanel.this, "��ѡ���Ƿ����ֽ���", "���ֽ���", JOptionPane.YES_NO_OPTION);
            //����ǣ��������Ǻ����£���һ������ף������
            if (op == 0) {
                history.get(0).setColor(1);
                history.get(1).setColor(2);
                history.get(2).setColor(1);
                boardData[history.get(0).getX()][history.get(0).getY()] = BLACK;
                boardData[history.get(1).getX()][history.get(1).getY()] = WHITE;
                boardData[history.get(2).getX()][history.get(2).getY()] = BLACK;
                currentPlayer = WHITE;

            }
        }

    }

    // �������ף�����numΪѡ�и��ֵĵ�num����
    public void drawManual(boolean isFast,int num) {
        // ��һ�����������в�����
        File manual = new File("src/resources/�������ֱ�ʤ�߷�.xlsx"); //������ӦFile����
        int[][] records = new int[230][3]; //���������ڵ����Ӽ�¼��ÿ����¼�� ����˳�� x y ��ʽ�洢
        ArrayList<String> goList = new ArrayList<>();
        int x,y;
        try {
            InputStream in = new FileInputStream(manual);
            XSSFWorkbook sheets = new XSSFWorkbook(in);
            XSSFSheet sheet = sheets.getSheetAt(0);
            XSSFRow row = sheet.getRow(num-1);
            for (int i = 0; i <row.getPhysicalNumberOfCells(); i++) {
                XSSFCell cell = row.getCell(i);
                String s;
                if (!(s = getString(cell)).equals(""))
                    goList.add(s);
            }

            for (int i = 0; i < goList.size(); i++) {
                String s = goList.get(i);
                int index1 = s.indexOf("."),index2 = s.indexOf("-");
                String s1 = s.substring(0,index1),s2 = s.substring(index1+1,index2);

                if (Integer.parseInt(s1) % 2 ==0) {  // ��������˳���жϺڰ���
                    records[i][0] = 2;  //����
                } else {
                    records[i][0] = 1;
                }
                x = s2.charAt(0) - 64;  //����ȡ����x����ת������ĸת��Ϊ����
                y = 16 - Integer.parseInt(s2.substring(1));  //��ȡy����
                records[i][1] = x;
                records[i][2] = y;
            }
            in.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        //�ڶ����������������
        reset();
        //�������
        //repaint();

        //��������������������ݣ�ˢ�½���
        for (int j = 0; j < goList.size(); j++) {  // ��������������������boradData�����ݣ���Ϊ���̻����ӵĸ�����boardData������
            x = records[j][1];
            y = records[j][2];
            boardData[x][y] = records[j][0];
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(isFast)
        {
            setHistory(records,goList.size()); // ����������������history����
        }
        else{
            slowShow(records,goList.size());
        }
        isShowManual = true;   // ������ʾ���ױ�ǣ���������ˢ�½���ʱ����Ŀ��ѡ�������׶�������������
        isShowOrder = true;    // ��ʾ����˳��
    }

    // ���ݴ����array�������������¸�ֵ���Ӽ�¼historyջ����������ʱ��
    public void slowShow(int[][] array, int length) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                if (i < length) {
                    int x = array[i][1], y = array[i][2];
                    history.push(new Chess(x, y, boardData[x][y]));
                    i++;
                } else {
                    timer.cancel();
                    System.gc();
                }
            }
        }, 0, 1 * 1000);
    }

    //����Ԫ�����ݲ�����
    public static String getString(XSSFCell cell) {
        if (cell == null) { //�����򷵻ؿ��ַ���
            return "";
        }
        return cell.getStringCellValue().trim(); //�����ַ�����ʽ���ص�Ԫ������
    }

    // ���ݴ����array�������������¸�ֵ���Ӽ�¼historyջ����������ʱ��
    public void setHistory(int[][] array,int length) {
        Chess p;
        for (int i = 0; i < length; i++) {
            int x = array[i][1],y = array[i][2];
            p = new Chess(x,y,boardData[x][y]);
            history.push(p);
        }
    }

}
