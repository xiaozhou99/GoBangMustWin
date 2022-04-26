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
    private static final int OFFSET = 70;// 棋盘偏移
    private static int CELL_WIDTH = MainUI.iWidth/25;// 棋格宽度
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = BOARD_SIZE + 2;
    public static final int CENTER = BOARD_SIZE / 2 + 1;// 中心点
    public static final int BLACK = 1;//黑棋
    public static final int WHITE = 2;//白棋
    public static final int BORDER = -1;//边界
    public static final int EMPTY = 0;//棋盘上无子

    public static final int ManMan = 0;// 双人模式
    public static final int ManAI = 1;// 人机模式

    public static final int HUMAN = 3;//人先手
    public static final int COMPUTER = 4;//计算机先手

    public static int N = -10;//获取用户输入的N打
    public static int pp = 0;//五手N打循环

    /**
     * history存储棋盘上所有棋子 画棋子 悔棋时用
     * boardStatus存储当前棋局  判定某位置是否有棋  五子连珠 局面评估时用  （数组可以随机访问）
     */
    public static Stack<Chess> history;// 落子历史记录，储存棋盘上所有棋子
    public static int[][] boardData;//当前的棋盘局面，EMPTY表示无子，BLACK表示黑子，WHITE表示白子，BORDER表示边界
    private int[] lastStep;// 上一个落子点，长度为2的数组，记录上一个落子点的坐标

    public static int currentPlayer = 0;            // 当前玩家执棋颜色，默认为无子
    private static int computerSide = BLACK;        // 默认机器持黑
    private static int humanSide = WHITE;
    private static boolean isShowOrder = false;     // 显示落子顺序
    public static boolean isGameOver = true;
    public static int initUser;     // 先手
    public static int VSMode;       //对战模式,ManMan=0 代表双人对战，ManAI=1代表人机对战

    private JDialog settingTip;
    private MainUI mainUI=null;

    private static boolean isShowManual = false; //是否是画棋谱
    private boolean isAppendText = true;
    private MustWinGo mustWinGo;//必胜棋谱类
    private Chess AIGoChess;//计算机落子点

    private int minx, maxx, miny, maxy; // 当前棋局下所有棋子的最小x，最大x，最小y，最大y，用于缩小搜索落子点的范围

    private int cx = CENTER, cy = CENTER;

    public GobangPanel(){};
    public GobangPanel(MainUI mainUI, JDialog settingTip) throws Exception {
        boardData = new int[BT][BT];    //1-15存储棋子状态，0 16为边界
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// 边界
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

    // 是否显示落子顺序
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
    }

    // 悔棋
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
        //画玩家信息
        //g2d.drawString("玩家A：",100,0);

        // 画背景图
        ImageIcon icon = new ImageIcon("src/image/background.jpg");
        g2d.drawImage(icon.getImage(), 0, 0, getSize().width, getSize().height, this);
        // 画棋盘
        drawBoard(g2d);
        // 画天元和星
        drawStar(g2d, CENTER, CENTER);          //按照棋盘15*15的坐标进行绘制
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) / 4, (BOARD_SIZE + 1) * 3 / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) / 4);
        drawStar(g2d, (BOARD_SIZE + 1) * 3 / 4, (BOARD_SIZE + 1) * 3 / 4);

        // 画数字和字母
        drawNumAndLetter(g2d);
        if (!isShowManual) {   //不是复现棋谱，即正常下棋
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
        } else {   //复现
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
    public void startGame(int initUser, int VSMode) {

        this.initUser = initUser;
        this.VSMode = VSMode;
        this.reset();
        isGameOver = false;
        isShowManual = false;
        isAppendText = true;
        mustWinGo.startGame();
        if (VSMode == ManAI)//人机
        {
            if (initUser == COMPUTER) {//电脑先手
                currentPlayer = Chess.BLACK;// 默认黑子先行
                Chess chess = mustWinGo.AIGo();
                putChess(chess.x, chess.y);// 默认第一步落在中心
                minx = maxx = miny = maxy = chess.x;

            } else {
                currentPlayer = Chess.WHITE;
            }
        } else {
            currentPlayer = Chess.BLACK;
        }
        N = -10;//重置五手N打的FLAG
        pp = 0;

    }

    // 鼠标移动
    private MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        public void mouseMoved(MouseEvent e) {
            int tx = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;       //Math.round函数是默认加上0.5之后，向下取整。
            int ty = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            if (tx != cx || ty != cy) {
                if (tx >= 1 && tx <= (BOARD_SIZE+OFFSET) && ty >= 1
                        && ty <= (BOARD_SIZE+OFFSET)) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));      //Cursor是封装鼠标光标的位图表示形式的类,HAND手状光标种类
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
                settingTip.setVisible(true);
                return;
            }
            int x = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            int y = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;

            if (cx >= 1 && cx <= BOARD_SIZE && cy >= 1 && cy <= BOARD_SIZE) {
                int mods = e.getModifiers();
                if ((mods & InputEvent.BUTTON1_MASK) != 0) {// 鼠标左键
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
     * 落子函数
     *
     * @param x
     * @param y
     * @return
     */
    private boolean putChess(int x, int y) {
        //五手N打
        if (VSMode == ManMan) {//人人模式
            FiveBeat(x, y);
        }

        if (boardData[x][y] == EMPTY) {
            // 棋盘搜索范围限制
            minx = Math.min(minx, x);
            maxx = Math.max(maxx, x);
            miny = Math.min(miny, y);
            maxy = Math.max(maxy, y);
            boardData[x][y] = currentPlayer;
            history.push(new Chess(x, y, currentPlayer));

            if (isAppendText) {
                String str = currentPlayer == WHITE ? "白棋：" : "黑棋：";
                MainUI.appendText(str + "【" + (char) (64 + x) + (16 - y) + "】\n");
                System.out.println(str + " 【" + (char) (64 + x) + (16 - y) + "】");
            }

            lastStep[0] = x;// 保存上一步落子点
            lastStep[1] = y;

            togglePlayer();
            mainUI.restartCountTime();
            //三手交换
            if (VSMode == ManMan) {
                threeChange(x, y);
            }

            int winSide = isGameOver(boardData[x][y]);// 判断终局
            if (winSide > 0) {

                if (winSide == WHITE) {
                    MainUI.appendText("白方赢了！\n");
                    JOptionPane.showMessageDialog(GobangPanel.this, "白方赢了！");
                } else if (winSide == BLACK) {
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
     * @return 0：进行中，1：黑棋赢，2：白棋赢，3：平手
     */
    public int isGameOver(int color) {
        if (!history.isEmpty()) {

            Chess lastStep = history.peek();
            int x = lastStep.getX();
            int y = lastStep.getY();

            //检查先手是否有禁手
            Chess firstStep = history.get(0);
            Chess step = history.get(history.size() - 1);
            if (step.getColor() == firstStep.getColor()) {
                int i = forbid(step.getColor());//判断禁手的类型
                if (step.getColor() == 1) {
                    if (i == 1) {
                        MainUI.appendText("出现三三禁手，黑子失败\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现三三禁手，黑子失败!");
                    }
                    if (i == 2) {
                        MainUI.appendText("出现四四禁手，黑子失败\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现四四禁手，黑子失败!");
                    }
                    if (i == 3) {
                        MainUI.appendText("出现长连禁手，黑子失败\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现长连禁手，黑子失败!");
                        return 777;//结束程序
                    }
                }
                if (step.getColor() == 2) {
                    if (i == 1) {
                        MainUI.appendText("出现三三禁手，白子失败\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现三三禁手，白子失败!");
                    }
                    if (i == 2) {
                        MainUI.appendText("出现四四禁手，白子失败\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现四四禁手，白子失败!");
                    }
                    if (i == 3) {
                        MainUI.appendText("出现长连禁手，白子失败\n");
                        JOptionPane.showMessageDialog(GobangPanel.this, "出现长连禁手，白子失败!");
                        return 777;//结束程序
                    }
                }
            }

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

    //禁手判断
    public int forbid(int color) {//传入先手棋子的颜色
        int k = 0;//计数三连
        int m = 0;//1为三三禁手，2为四四禁手，3为长连禁手
        int i = 0;//计数四连
        int j = 0;//检查是否有长连情况
        //三三禁手
        for (int x = 1; x < 16; x++) {
            for (int y = 1; y < 16; y++) {//循环查找棋盘的每一个棋子
                if (boardData[x][y] == color) {
                    //水平方向，包括三三，四四，长连
                    if (boardData[x - 1][y] == color) {
                        if (boardData[x - 2][y] == color) {
                            if (boardData[x - 3][y] == 0) {
                                if (boardData[x + 1][y] == 0) {
                                    k++;//三三禁手,下面的计数也是，当有两个三三出现，即为禁手
                                }
                            }
                            if (boardData[x - 3][y] == color) {
                                if (boardData[x - 4][y] == 0 && boardData[x + 1][y] == 0) {
                                    i++;//四四禁手，下同，当有两个四四出现，即为禁手
                                }
                                if (boardData[x - 4][y] == color && boardData[x - 5][y] == color) {
                                    j++;//长连禁手

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
                    //垂直方向
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
                    //斜方向1
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
                    //斜方向2
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
                        m = 1;//出现三三禁手
                    }
                    if (i >= 2) {
                        m = 2;//出现四四禁手
                    }
                    if (j >= 1) {
                        m = 3;//出现长连禁手
                    }
                    k = 0;
                    i = 0;//遍历完一个点后重置，再遍历下一个点

                }
            }
        }
        return m;//根据返回值说明三三，四四，或长连禁手
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
        //N打
        if (history.size() == 4) {
            N = Integer.parseInt(JOptionPane.showInputDialog(null, "请输入五手N打的次数！"));
        }
        if (pp < N + 1) {
            currentPlayer = history.get(0).getColor();
            pp++;
            System.out.println(pp);
        }
        if (pp == N + 1) {
            JOptionPane.showMessageDialog(GobangPanel.this, "请选择留下的棋子！");
            pp = 100;
            currentPlayer = history.get(0).getColor();
        }
    }

    //三手交换
    public void exchangeColor(int x, int y) {
        if (history.get(0).getColor() == 1) {
            //1是黑子
            int op = JOptionPane.showConfirmDialog(GobangPanel.this, "请选择是否三手交换", "三手交换", JOptionPane.YES_NO_OPTION);
            //点击是，第三步是黑子下，则一三步变白，二变黑
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
            //2是白子
            int op = JOptionPane.showConfirmDialog(GobangPanel.this, "请选择是否三手交换", "三手交换", JOptionPane.YES_NO_OPTION);
            //点击是，第三步是黑子下，则一三步变白，二变黑
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

    // 复现棋谱，参数num为选中复现的第num局棋
    public void drawManual(boolean isFast,int num) {
        // 第一步，从棋谱中并保存
        File manual = new File("src/resources/黑棋先手必胜走法.xlsx"); //创建对应File对象
        int[][] records = new int[230][3]; //保存棋谱内的落子记录，每条记录以 落子顺序 x y 形式存储
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

                if (Integer.parseInt(s1) % 2 ==0) {  // 根据落子顺序判断黑白棋
                    records[i][0] = 2;  //白棋
                } else {
                    records[i][0] = 1;
                }
                x = s2.charAt(0) - 64;  //将获取到的x坐标转换由字母转换为数字
                y = 16 - Integer.parseInt(s2.substring(1));  //获取y坐标
                records[i][1] = x;
                records[i][2] = y;
            }
            in.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        //第二步，清空棋盘内容
        reset();
        //清空棋盘
        //repaint();

        //第三步，重设各变量数据，刷新界面
        for (int j = 0; j < goList.size(); j++) {  // 根据棋谱内容重新设置boradData的数据，因为棋盘画棋子的根据是boardData的数据
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
            setHistory(records,goList.size()); // 根据棋谱重新设置history内容
        }
        else{
            slowShow(records,goList.size());
        }
        isShowManual = true;   // 设置显示棋谱标记，用于区分刷新界面时，项目会选择复现棋谱而不是正常下棋
        isShowOrder = true;    // 显示落子顺序
    }

    // 根据传入的array即棋谱内容重新赋值落子记录history栈，复现棋谱时用
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

    //处理单元格内容并返回
    public static String getString(XSSFCell cell) {
        if (cell == null) { //若空则返回空字符串
            return "";
        }
        return cell.getStringCellValue().trim(); //否则按字符串形式返回单元格内容
    }

    // 根据传入的array即棋谱内容重新赋值落子记录history栈，复现棋谱时用
    public void setHistory(int[][] array,int length) {
        Chess p;
        for (int i = 0; i < length; i++) {
            int x = array[i][1],y = array[i][2];
            p = new Chess(x,y,boardData[x][y]);
            history.push(p);
        }
    }

}
