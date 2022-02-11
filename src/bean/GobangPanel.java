package bean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class GobangPanel extends JPanel {
    private static final long serialVersionUID = 667503661521167626L;
    private static final int OFFSET = 40;// ����ƫ��
    private static final int CELL_WIDTH = 40;// �����
    public static final int BOARD_SIZE = 15;// ���̸���
    public static final int BT = BOARD_SIZE + 2;
    public static final int CENTER = BOARD_SIZE / 2 + 1;// ���ĵ�
    public static final int BLACK = 1;//����
    public static final int WHITE = 2;//����
    public static final int BORDER = -1;//�߽�
    public static final int EMPTY = 0;//����������

    public static final int ManMan = 0;// ˫��ģʽ
    public static final int ManAI = 1;// �˻�ģʽ

    public static final int HUMAN = 3;//
    public static final int COMPUTER = 4;//

    /**
     * history�洢�������������� ������ ����ʱ��
     * boardStatus�洢��ǰ���  �ж�ĳλ���Ƿ�����  �������� ��������ʱ��  ���������������ʣ�
     */
    public Stack<Chess> history;// ������ʷ��¼��������������������
    public static int[][] boardData;//��ǰ�����̾��棬EMPTY��ʾ���ӣ�BLACK��ʾ���ӣ�WHITE��ʾ���ӣ�BORDER��ʾ�߽�
    private int[] lastStep;// ��һ�����ӵ㣬����Ϊ2�����飬��¼��һ�����ӵ������

    private static int currentPlayer = 0;// ��ǰ���ִ����ɫ��Ĭ��Ϊ����
    private static int computerSide = BLACK;// Ĭ�ϻ����ֺ�
    private static int humanSide = WHITE;
    private static boolean isShowOrder = false;// ��ʾ����˳��
    private static boolean isGameOver = true;
    public static int initUser;// ����
    public static int VSMode;//��սģʽ,ManMan=0 ����˫�˶�ս��ManAI=1�����˻���ս

    private JTextArea area;
    private JDialog settingTip;

    private static boolean isShowManual = false; //�Ƿ��ǻ�����
    private boolean isAppendText = true;
    private Win win;//��ʤ������
    private Chess AIGoChess;//��������ӵ�

    private int minx, maxx, miny, maxy; // ��ǰ������������ӵ���Сx�����x����Сy�����y��������С�������ӵ�ķ�Χ

    private int cx = CENTER, cy = CENTER;

    public GobangPanel(JTextArea area,JDialog settingTip) {
        boardData = new int[BT][BT];//1-15�洢����״̬��0 16Ϊ�߽�
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// �߽�
            }
        }
        history = new Stack<>();
        lastStep = new int[2];
        win = new Win();
        this.area = area;
        this.settingTip=settingTip;
        addMouseMotionListener(mouseMotionListener);
        addMouseListener(mouseListener);
        setPreferredSize(new Dimension(650, 700));
    }

    // �Ƿ���ʾ����˳��
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
        repaint();
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
            repaint();
            if(VSMode==ManAI)
            {
                win.goback();
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

        // ������ͼ
        ImageIcon icon = new ImageIcon("src/image/background.jpg");
        g2d.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);

        // ������
        drawBoard(g2d);
        // ����Ԫ����
        drawStar(g2d, CENTER, CENTER);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) * 3 / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) * 3 / 4);

        // �����ֺ���ĸ
        drawNumAndLetter(g2d);
        if (!isShowManual) {   //���ǻ�����
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
        } else {   //������
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
    private static void drawChess(Graphics g2d, int cx, int cy, int color) {
        if (color == 0)
            return;
        int size = CELL_WIDTH * 5 / 6;
        g2d.setColor(color == BLACK ? Color.BLACK : Color.WHITE);
        g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSET - size / 2, (cy - 1)
                * CELL_WIDTH - size / 2 + OFFSET, size, size);
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
        area.setText("");
        isGameOver = false;
        isShowManual = false;
        isAppendText = true;
        win.startGame();
        if (VSMode == ManAI)//�˻�
        {
            if (initUser == COMPUTER) {//��������
                currentPlayer = Chess.BLACK;// Ĭ�Ϻ�������
                Chess chess = win.AIGo();
                putChess(chess.x, chess.y);// Ĭ�ϵ�һ����������
                minx = maxx = miny = maxy = chess.x;

            } else {
                currentPlayer = Chess.WHITE;
            }
        } else {
            currentPlayer = Chess.BLACK;
        }

        repaint();

    }

    // ����ƶ�
    private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
            int tx = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            int ty = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            if (tx != cx || ty != cy) {
                if (tx >= 1 && tx <= BOARD_SIZE && ty >= 1
                        && ty <= BOARD_SIZE) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    repaint();
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
                            win.PlayerGo(x, 16 - y);
                            AIGoChess = win.AIGo();
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
            repaint();
            int winSide = isGameOver(currentPlayer);// �ж��վ�
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
            togglePlayer();
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
     * @return 0��������  1������Ӯ  2������Ӯ  3��ƽ��
     */
    public int isGameOver(int color) {
        if (!history.isEmpty()) {

            Chess lastStep = history.peek();
            int x = lastStep.getX();
            int y = lastStep.getY();

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
}
