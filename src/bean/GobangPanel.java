package bean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class GobangPanel extends JPanel {
    private static final long serialVersionUID = 667503661521167626L;
    private static final int OFFSET = 40;// 棋盘偏移
    private static final int CELL_WIDTH = 40;// 棋格宽度
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = BOARD_SIZE + 2;
    public static final int CENTER = BOARD_SIZE / 2 + 1;// 中心点
    public static final int BLACK = 1;//黑棋
    public static final int WHITE = 2;//白棋
    public static final int BORDER = -1;//边界
    public static final int EMPTY = 0;//棋盘上无子

    public static final int MANUAL = 0;// 双人模式
    public static final int HALF = 1;// 人机模式
    public static final int AUTO = 2;// 双机模式
    public static final int HUMAN = 3;//
    public static final int COMPUTER = 4;//

    /**
     * history存储棋盘上所有棋子 画棋子 悔棋时用
     * boardStatus存储当前棋局  判定某位置是否有棋  五子连珠 局面评估时用  （数组可以随机访问）
     */
    private Stack<Chess> history;// 落子历史记录，储存棋盘上所有棋子
    public static int[][] boardData;//当前的棋盘局面，EMPTY表示无子，BLACK表示黑子，WHITE表示白子，BORDER表示边界
    private int[] lastStep;// 上一个落子点，长度为2的数组，记录上一个落子点的坐标

    private static int currentPlayer = 0;// 当前玩家执棋颜色，默认为无子
    private static int computerSide = BLACK;// 默认机器持黑
    private static int humanSide = WHITE;
    private static boolean isShowOrder = false;// 显示落子顺序
    private static boolean isGameOver = true;
    public static int initUser;// 先手

    private JTextArea area;
    private static boolean isShowManual = false; //是否是画棋谱
    private boolean isAppendText = true;
    private Win win;//必胜棋谱类
    private Chess AIGoChess;//计算机落子点

    private int minx, maxx, miny, maxy; // 当前棋局下所有棋子的最小x，最大x，最小y，最大y，用于缩小搜索落子点的范围

    private int cx = CENTER, cy = CENTER;

    public GobangPanel(JTextArea area) {
        boardData = new int[BT][BT];//1-15存储棋子状态，0 16为边界
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// 边界
            }
        }
        history = new Stack<>();
        lastStep = new int[2];
        win = new Win();
        this.area = area;
        addMouseMotionListener(mouseMotionListener);
        addMouseListener(mouseListener);
        setPreferredSize(new Dimension(650, 700));
    }

    // 是否显示落子顺序
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
        repaint();
    }

    // 悔棋
    public void undo() {
        if (!history.isEmpty()) {
            if(history.size()==1&&initUser==COMPUTER)
            {
                return;
            }
            Chess p1 = history.pop();
            boardData[p1.x][p1.y] = EMPTY;
            if (!history.isEmpty()) {
                Chess chess = history.peek();
                lastStep[0] = chess.x;
                lastStep[1] = chess.y;
            }
        } else {
            lastStep[0] = BORDER;
            lastStep[1] = BORDER;
        }
        togglePlayer();
        repaint();
        win.goback();
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("April", Font.BOLD, 12));

        // 画背景图
        ImageIcon icon = new ImageIcon("src/image/background.jpg");
        g2d.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);

        // 画棋盘
        drawBoard(g2d);
        // 画天元和星
        drawStar(g2d, CENTER, CENTER);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) * 3 / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) * 3 / 4);

        // 画数字和字母
        drawNumAndLetter(g2d);
        if (!isShowManual) {   //不是画棋谱
            // 画提示框
            drawCell(g2d, cx, cy);
            if (!isGameOver) {
                // 画所有棋子
                for (Chess chess : history) {
                    drawChess(g2d, chess.x, chess.y, chess.color);
                }
                // 画顺序
                if (isShowOrder) {
                    drawOrder(g2d);
                } else {    // 将上一个落子点标红点显示
                    if (lastStep[0] > 0 && lastStep[1] > 0) {
                        g2d.setColor(Color.RED);
                        g2d.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSET
                                        - CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
                                        + OFFSET - CELL_WIDTH / 10, CELL_WIDTH / 5,
                                CELL_WIDTH / 5);

                    }
                }
            }
        } else {   //画棋谱
            // 画所有棋子
            for (Chess chess : history) {
                drawChess(g2d, chess.x, chess.y, chess.color);
            }
            drawOrder(g2d);
        }

    }

    // 画棋盘
    private void drawBoard(Graphics g2d) {
        for (int x = 0; x < BOARD_SIZE; ++x) {   // 画竖线
            g2d.drawLine(x * CELL_WIDTH + OFFSET, OFFSET, x * CELL_WIDTH
                    + OFFSET, (BOARD_SIZE - 1) * CELL_WIDTH + OFFSET);

        }
        for (int y = 0; y < BOARD_SIZE; ++y) {   // 画横线
            g2d.drawLine(OFFSET, y * CELL_WIDTH + OFFSET,
                    (BOARD_SIZE - 1) * CELL_WIDTH + OFFSET, y
                            * CELL_WIDTH + OFFSET);

        }
    }

    // 画天元和星
    private void drawStar(Graphics g2d, int cx, int cy) {
        g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSET - 4, (cy - 1) * CELL_WIDTH
                + OFFSET - 4, 8, 8);
    }

    // 画数字和字母
    private void drawNumAndLetter(Graphics g2d) {
        FontMetrics fm = g2d.getFontMetrics();
        int stringWidth, stringAscent;
        stringAscent = fm.getAscent();
        for (int i = 1; i <= BOARD_SIZE; i++) {

            String num = String.valueOf(BOARD_SIZE - i + 1);
            stringWidth = fm.stringWidth(num);
            g2d.drawString(String.valueOf(BOARD_SIZE - i + 1), OFFSET / 4    // 画数字
                    - stringWidth / 2, OFFSET + (CELL_WIDTH * (i - 1))
                    + stringAscent / 2);

            String letter = String.valueOf((char) (64 + i));
            stringWidth = fm.stringWidth(letter);
            g2d.drawString(String.valueOf((char) (64 + i)), OFFSET    //画字母
                    + (CELL_WIDTH * (i - 1)) - stringWidth / 2, OFFSET * 3 / 4
                    + OFFSET + CELL_WIDTH * (BOARD_SIZE - 1)
                    + stringAscent / 2);
        }
    }

    // 画棋子
    private static void drawChess(Graphics g2d, int cx, int cy, int color) {
        if (color == 0)
            return;
        int size = CELL_WIDTH * 5 / 6;
        g2d.setColor(color == BLACK ? Color.BLACK : Color.WHITE);
        g2d.fillOval((cx - 1) * CELL_WIDTH + OFFSET - size / 2, (cy - 1)
                * CELL_WIDTH - size / 2 + OFFSET, size, size);
    }

    // 画预选框
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

    // 画落子顺序
    private void drawOrder(Graphics g2d) {
        if (history.size() > 0) {
            g2d.setColor(Color.RED);
            int i = 0;
            for (Chess chess : history) {
                int x = chess.x;
                int y = chess.y;
                String text = String.valueOf(i + 1);
                // 居中
                FontMetrics fm = g2d.getFontMetrics();
                int stringWidth = fm.stringWidth(text);
                int stringAscent = fm.getAscent();
                g2d.drawString(text, (x - 1) * CELL_WIDTH + OFFSET - stringWidth / 2,
                        (y - 1) * CELL_WIDTH + OFFSET + stringAscent / 2);
                i++;
            }
        }
    }


    // 开始游戏
    public void startGame(int initUser) {

        this.initUser = initUser;
        this.reset();
        area.setText("");
        isGameOver = false;
        isShowManual = false;
        isAppendText = true;
        win.startGame();
        if (initUser == 4) {
            currentPlayer = Chess.BLACK;// 默认黑子先行
            Chess chess = win.AIGo();
            putChess(chess.x, chess.y);// 默认第一步落在中心
            minx = maxx = miny = maxy = chess.x;
            MainUI.appendText("黑棋： 【" + (char) (64 + 8) + (16 - 8) + "】\n");

        } else {
            currentPlayer = Chess.WHITE;
        }
        repaint();

    }

    // 鼠标移动
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

    // 鼠标点击落子
    private MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (isGameOver) {
                JOptionPane.showMessageDialog(GobangPanel.this, "请开始新游戏！");
                return;
            }
            int x = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            int y = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;

            if (cx >= 1 && cx <= BOARD_SIZE && cy >= 1 && cy <= BOARD_SIZE) {
                int mods = e.getModifiers();
                if ((mods & InputEvent.BUTTON1_MASK) != 0) {// 鼠标左键
                    if (putChess(x, y)) {
                        if (isAppendText) {
                            MainUI.appendText("白棋： 【" + (char) (64 + x) + (16 - y) + "】\n");
                        }
                        win.PlayerGo(x, 16 - y);
                        System.out.println("\n----白棋完毕----");

                        AIGoChess = win.AIGo();
                        putChess(AIGoChess.x, 16 - AIGoChess.y);
                        if (isAppendText) {
                            MainUI.appendText("黑棋： 【" + (char) (64 + AIGoChess.x) + (AIGoChess.y) + "】\n");
                        }
                        System.out.println("\n----黑棋完毕----");
                    }
                }
            }
        }
        //}
    };

    /**
     * 落子函数
     *
     * @param x
     * @param y
     * @return
     */
    private boolean putChess(int x, int y) {
        if (boardData[x][y] == EMPTY) {
            // 棋盘搜索范围限制
            minx = Math.min(minx, x);
            maxx = Math.max(maxx, x);
            miny = Math.min(miny, y);
            maxy = Math.max(maxy, y);
            boardData[x][y] = currentPlayer;
            history.push(new Chess(x, y, currentPlayer));
            togglePlayer();
            System.out.printf(" 【" + (char) (64 + x) + (16 - y) + "】");

            lastStep[0] = x;// 保存上一步落子点
            lastStep[1] = y;
            repaint();
            int winSide = isGameOver(initUser);// 判断终局
            if (winSide > 0) {

                if (winSide == humanSide) {
                    MainUI.appendText("白方赢了！\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "白方赢了！");
                } else if (winSide == computerSide) {
                    MainUI.appendText("黑方赢了！\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "黑方赢了！");
                } else {
                    MainUI.appendText("双方平手！\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "双方平手");
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

    //重置棋盘状态
    public void reset() {
        for (int i = 1; i < BT - 1; i++)
            for (int j = 1; j < BT - 1; j++) {
                boardData[i][j] = EMPTY;
            }
        history.clear();
    }

    /**
     * 判断棋局是否结束
     *
     * @return 0：进行中  1：黑棋赢  2：白棋赢  3：平手
     */
    public int isGameOver(int initUser) {
        if (!history.isEmpty()) {
            int color;
            if (initUser == 4) {   //电脑先手
                color = (history.size() % 2 == 1) ? Chess.BLACK : Chess.WHITE;
            } else {
                color = (history.size() % 2 == 1) ? Chess.WHITE : Chess.BLACK;
            }
            Chess lastStep = history.peek();
            int x = lastStep.getX();
            int y = lastStep.getY();

            // 四个方向
            if (check(x, y, 1, 0, color) + check(x, y, -1, 0, color) >= 4 ||
                    check(x, y, 0, 1, color) + check(x, y, 0, -1, color) >= 4 ||
                    check(x, y, 1, 1, color) + check(x, y, -1, -1, color) >= 4 ||
                    check(x, y, 1, -1, color) + check(x, y, -1, 1, color) >= 4) {
                return color;
            }

        }

        // 进行中
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j)
                if (boardData[i][j] == EMPTY) {
                    return 0;
                }
        }
        return 3;
    }

    // 计算相同颜色连子的数量
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
