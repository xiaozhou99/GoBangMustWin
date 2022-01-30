package bean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

// MainUI显示了整体界面，GobangPanel类负责界面左边部分的棋盘显示，及各种逻辑功能处理
public class GobangPanel extends JPanel {
    private static final long serialVersionUID = 667503661521167626L;
    private JTextArea area; // 界面右边的文本域框，需在上面显示提示信息时用
    private static final int OFFSET = 40;// 棋盘偏移
    private static final int CELL_WIDTH = 40;// 棋格宽度
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = 17; //棋盘格数+2，确保棋盘上的1-15坐标就按1-15来访问，也保证了边界情况可判断
    public static final int CENTER = 8; //中心点BOARD_SIZE / 2 + 1
    //以下四条为棋盘数组即boarddata中各值的意义
    public static final int BLACK = 1;//黑棋
    public static final int WHITE = 2;//白棋
    public static final int BORDER = -1;//边界
    public static final int EMPTY = 0;//棋盘上无子

    //以下为系统可选择的下棋模式，由于当前项目是设置按棋谱下，即类似人机，故将其余模式先注释，后续也可考虑恢复拓展
//    public static final int MANUAL = 0;// 双人模式
//    public static final int HALF = 1;// 人机模式
//    public static final int AUTO = 2;// 双机模式
    public static final int HUMAN = 3;//人先手
    public static final int COMPUTER = 4;//电脑先手，该项目中默认电脑先手即第一步下天元，故界面上的先手选择暂时失效


    //history存储棋盘上的下棋记录，以栈形式存储，画棋子、悔棋时用，需设为static类型，确保创建的所有对象的history数据一致
    // boardData存储当前棋局，判定某位置是否有棋、五子连珠、局面评估时用  （数组可以随机访问）
    private static Stack<Chess> history ;
    public static int[][] boardData; //当前的棋盘局面，EMPTY表示无子，BLACK表示黑子，WHITE表示白子，BORDER表示边界
    private int[] lastStep; //保存上一个落子点，长度为2的数组，记录上一个落子点的坐标，悔棋时用

    private static int currentPlayer = 0; // 当前玩家执棋颜色，默认为无子
    private static int computerSide = BLACK;// 默认机器持黑
    private static int humanSide = WHITE;  //人持白
    private static boolean isShowOrder = false; // 显示落子顺序，默认不显示
    private static boolean isGameOver = true;   //游戏是否结束
    public static int initUser;// 先手
    private static boolean isShowManual = false; //是否是画棋谱，复现棋谱时用，由于复现棋谱和正常下棋的画棋子方式不同
    private boolean isAppendText = true;   //界面右边的文本域框是否允许显示

    private Chess AIGoChess;  //计算机下一步落子点
    private MustWinGo mustWinGo;  //必胜棋谱类对象
    private int minx, maxx, miny, maxy; // 当前棋局下所有棋子的最小x，最大x，最小y，最大y，用于缩小搜索落子点的范围
    private int cx = CENTER, cy = CENTER;  // 中心点

    //空构造函数
    public GobangPanel(){}
    //以area即显示信息文本域为参数，绑定area，构造函数中主要初始化各变量
    public GobangPanel(JTextArea area) throws Exception {
        boardData = new int[BT][BT];//初始化棋盘数组，索引为1-15存储棋子状态，0 16为边界
        for (int i = 0; i < BT; i++) {
            for (int j = 0; j < BT; j++) {
                boardData[i][j] = EMPTY;
                if (i == 0 || i == BT - 1 || j == 0 || j == BT - 1)
                    boardData[i][j] = BORDER;// 边界
            }
        }
        history = new Stack<Chess>(); //初始化落子历史记录
        lastStep = new int[2];   //初始化上一步落子点
        mustWinGo = new MustWinGo();  //初始化必胜棋谱对象
        this.area = area;
        addMouseMotionListener(mouseMotionListener); //给面板增加事件监听器
        addMouseListener(mouseListener);
        setPreferredSize(new Dimension(650, 700));  //设置面板的显示大小
    }

    // 是否显示落子顺序，根据用户在界面上的勾选复选框操作，对isShowOrder相应取反，并刷新界面显示
    public void toggleOrder() {
        isShowOrder = !isShowOrder;
        repaint();
    }

    // 悔棋
    public void undo() {
        if (!history.isEmpty()) {  //先判断是否有落子历史记录
            if(history.size()==1&&initUser==COMPUTER) //若只有默认的电脑下在天元处，直接返回
            {
                return;
            }
            Chess p1 = history.pop();  //从栈中弹出最新的落子记录
            boardData[p1.x][p1.y] = EMPTY;   //设置该位置为空
            if (!history.isEmpty()) {
                Chess chess = history.peek();
                lastStep[0] = chess.x;  //更新上一步落子记录lastStep
                lastStep[1] = chess.y;
            }
        } else {  //若history为空，设置上一步落子记录lastStep为边界
            lastStep[0] = BORDER;
            lastStep[1] = BORDER;
        }
        togglePlayer();  //转换当前持子角色
        repaint();  //刷新界面
        mustWinGo.goback();   //由于项目是按必胜棋谱走，悔棋后必胜棋谱对象也要进行相应的返回处理
    }


    // 界面及各组件初始化
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("April", Font.BOLD, 12));

        // 画背景图
        ImageIcon icon = new ImageIcon("src/main/resources/image/background.jpg");
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
        if (!isShowManual) {   //若不是复现棋谱，即正常下棋
            // 画提示框，即鼠标移动的红色预选框
            drawCell(g2d, cx, cy);
            if (!isGameOver) {  // 游戏未结束
                for (Chess chess : history) {  // 画所有棋子
                    drawChess(g2d, chess.x, chess.y, chess.color);
                }

                if (isShowOrder) {  // 画顺序
                    drawOrder(g2d);
                }
                else {    // 将上一个落子点标红点显示
                    if (lastStep[0] > 0 && lastStep[1] > 0) {
                        g2d.setColor(Color.RED);
                        g2d.fillRect((lastStep[0] - 1) * CELL_WIDTH + OFFSET
                                        - CELL_WIDTH / 10, (lastStep[1] - 1) * CELL_WIDTH
                                        + OFFSET - CELL_WIDTH / 10, CELL_WIDTH / 5,
                                CELL_WIDTH / 5);

                    }
                }
            }
        }
        else {   //复现棋谱
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

    // 复现棋谱，参数manualPath为复现的棋谱路径
    public void drawManual(String manualPath) {
        // 第一步，读取棋谱并保存
        File manual = new File("manual/" + manualPath); //创建对应File对象
        int[][] records = new int[230][3]; //保存棋谱内的落子记录，每条记录以 落子顺序 x y 形式存储
        int lineNum = 0,x,y; //lineNum用于定位文件中到哪一行，文件读取结束的lineNum即表示棋谱步数
        try {
            BufferedReader br = new BufferedReader(new FileReader(manual));
            String line;
            String[] content;

            while ((line = br.readLine()) != null) {  //读取文件，文件中每行以 落子顺序 xy 的形式存储，如“1 H8”
                content = line.split(" "); // 将每行内容以空格分离，获取落子顺序与落子坐标
                int order = Integer.parseInt(content[0]); //落子顺序
                if (order % 2 ==0) {  // 根据落子顺序判断黑白棋
                    records[lineNum][0] = 2;
                } else {
                    records[lineNum][0] = 1;
                }
                x = content[1].charAt(0) - 64;  //将获取到的x坐标转换由字母转换为数字
                y = 16 - Integer.parseInt(content[1].substring(1));  //获取y坐标
                records[lineNum][1] = x;
                records[lineNum][2] = y;
                lineNum++;
            }
            br.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        //第二步，清空棋盘内容
        reset(); //清空棋盘
        repaint();

        //第三步，重设各变量数据，刷新界面
        for (int j = 0; j < lineNum; j++) {  // 根据棋谱内容重新设置boradData的数据，因为棋盘画棋子的根据是boardData的数据
            x = records[j][1];
            y = records[j][2];
            boardData[x][y] = records[j][0];
        }
        setHistory(records,lineNum); // 根据棋谱重新设置history内容
        isShowManual = true;   // 设置显示棋谱标记，用于区分刷新界面时，项目会选择复现棋谱而不是正常下棋
        isShowOrder = true;    // 显示落子顺序
        MainUI.clearText();    // 清空界面右侧文本域的显示内容

        repaint();
    }


    // 开始游戏，参数为先手，项目默认先手为电脑
    public void startGame(int initUser) {
        this.initUser = initUser;   //初始化先手
        this.reset();   //重置各变量内容
        area.setText("");  //清空右侧文本域显示内容
        isGameOver = false;  //设置变量
        if (!(MainUI.orderBtn.isSelected()))  //根据界面上是否勾选显示落子顺序来恢复
            isShowOrder = false;
        isShowManual = false;  //设置非复现棋谱
        isAppendText = true;   //设置右边文本域可显示

        mustWinGo.startGame();  //必胜棋谱对象中也进行相关初始化

        if (initUser == 4) {  // 先手为电脑，该项目默认电脑先行
            currentPlayer = Chess.BLACK; // 默认黑子先行
            Chess chess = mustWinGo.AIGo(); //从必胜棋谱中获取黑子的下子位置
            putChess(chess.x, chess.y);  //返回的是天元坐标，默认第一步落在中心
            minx = maxx = miny = maxy = chess.x;  //设置搜索范围，当前从中心开始
            MainUI.appendText("黑棋： 【" + (char) (64 + 8) + (16 - 8) + "】\n");  //右侧文本域显示内容
            System.out.println("----黑棋完毕----");
        } else {
            currentPlayer = Chess.WHITE;
        }
        repaint();

    }

    // 鼠标移动事件监听处理，即根据鼠标的移动位置，显示预选框
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

    // 鼠标点击事件处理，即落子
    private MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if (isGameOver) {
                JOptionPane.showMessageDialog(GobangPanel.this, "请开始新游戏！");
                return;
            }
            // 获取落子坐标
            int x = Math.round((e.getX() - OFFSET) * 1.0f / CELL_WIDTH) + 1;
            int y = Math.round((e.getY() - OFFSET) * 1.0f / CELL_WIDTH) + 1;

            if (cx >= 1 && cx <= BOARD_SIZE && cy >= 1 && cy <= BOARD_SIZE) {
                if (putChess(x, y)) {  //落子
                    if (isAppendText) {
                        MainUI.appendText("白棋： 【" + (char) (64 + x) + (16 - y) + "】\n");
                    }
                    mustWinGo.PlayerGo(x, 16 - y);  //必胜棋谱类进行下一步操作
                                                        //进行16-y的操作是由于java棋盘上原点在左上角，而比赛中原点在左下角
                    System.out.println("----白棋完毕----");

                    AIGoChess = mustWinGo.AIGo(); //根据白子的落子位置，从必胜棋谱中获取黑子的下一步走法
                    if (AIGoChess != null){
                        putChess(AIGoChess.x, 16 - AIGoChess.y);
                    }
                    else { //根据返回结果判断白棋可能下在了棋谱上未记录的位置，即乱下，由于项目目前是根据棋谱走法来操作的，故显示提示信息引导用户重下（后续加入传统搜索算法后再改进）
                        System.out.println("您可能下错棋了，请悔棋");
                        MainUI.appendText("您可能下错棋了，请悔棋");
                    }

                    if (isAppendText) {
                        MainUI.appendText("黑棋： 【" + (char) (64 + AIGoChess.x) + (AIGoChess.y) + "】\n");
                    }
                    System.out.println("----黑棋完毕----");
                }
            }
        }
        //}
    };

    // 落子函数
    private boolean putChess(int x, int y) {
        if (boardData[x][y] == EMPTY) {  //首先判断下子位置是否为空
            // 棋盘搜索范围限制
            minx = Math.min(minx, x); //更新搜索范围限制
            maxx = Math.max(maxx, x);
            miny = Math.min(miny, y);
            maxy = Math.max(maxy, y);
            boardData[x][y] = currentPlayer;  //设置为当前玩家所下棋
            history.push(new Chess(x, y, currentPlayer));  //压入历史记录栈
            togglePlayer();  //转换下棋角色
            System.out.println("【" + (char) (64 + x) + (16 - y) + "】");

            lastStep[0] = x;// 保存上一步落子点
            lastStep[1] = y;
            repaint();
            int winSide = isGameOver(initUser);// 判断终局
            if (winSide > 0) {  //>0表示已终局
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

    // 转换黑白棋
    void togglePlayer() {
        currentPlayer = 3 - currentPlayer;
    }

    //重置棋盘状态
    public void reset() {
        for (int i = 1; i < BT - 1; i++)
            for (int j = 1; j < BT - 1; j++) {
                boardData[i][j] = EMPTY;  // 重设boardData
            }
        history.clear();  // 清空落子记录栈
    }

    //判断棋局是否结束  return 0：进行中  1：黑棋赢  2：白棋赢  3：平手
    public int isGameOver(int initUser) {
        if (!history.isEmpty()) {
            int color;  //判断当前持子方

            if (initUser == 4) {   //电脑先手
                color = (history.size() % 2 == 1) ? Chess.BLACK : Chess.WHITE;
            } else {
                color = (history.size() % 2 == 1) ? Chess.WHITE : Chess.BLACK;
            }
            Chess lastStep = history.peek();  //获取上一步落子记录
            int x = lastStep.getX();
            int y = lastStep.getY();

            // 判断四个方向是否形成五连及以上，是则返回当前赢家
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
        return 3;  //平局
    }

    // 计算相同颜色连子的数量，xy为当前坐标，dx，dy表示相应方向，如dx1dy0，往右，dx0dy1往上，dx1dy1右上等
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
        return sum;  //返回连子数量
    }

    // 将落子记录以二维数组的形式，并以落子的正序顺序返回，保存棋谱时用
    public int[][] getHistory() {
        int length = history.size();
        int[][] array = new int[length][2];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < 2; j++) { //将落子记录逐个弹出并存到二维数组中
                Chess p = history.get(i);
                array[i][0] = p.getX();
                array[i][1] = p.getY();
            }
        return array;
    }


    //保存当前棋局到棋谱文件中
    public void writeManual () throws IOException {
        int[][] temp = getHistory();  //获取落子历史记录
        SimpleDateFormat ma = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  //棋谱文件名以保存时间为名
        Date time = new Date();
        String path =  "manual/" + ma.format(time) + ".txt";
        File file = new File(path);  // 创建棋谱文件

        if(!file.exists()){
            //若没有manual目录，则先得到文件的上级目录，并创建上级目录，再创建文件
            file.getParentFile().mkdir();
            try {
                //创建文件
                file.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                for (int i = 0; i < temp.length; i++) {    //以 落子顺序 x y 方式保存 落子顺序为单数表示黑棋，反之白棋
                    bw.write((i+1) + " " + (char)(64 + temp[i][0]) + (16 - temp[i][1]) + "\n");
                }
                bw.flush();
                bw.close();
                JOptionPane.showMessageDialog(this,"保存成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
