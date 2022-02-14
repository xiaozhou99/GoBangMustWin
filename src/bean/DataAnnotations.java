package bean;

//数据标记类
public class DataAnnotations {

    private int[][] data;

    //攻
    boolean GongHuo_Three = false;//活三
    boolean GongHuo_Four = false;//活四
    boolean GongChong_Four = false;//冲四
    boolean GongFive=false;//连五
    //防
    boolean FangHuoThree = false;//防活三
    boolean FangChong_Four = false;//防冲四

    public DataAnnotations() {
        //一步一步复现棋盘状态，以扫描棋盘上的棋型
        this.data = new int[GobangPanel.BT][GobangPanel.BT];
        for (int i = 0; i < GobangPanel.BT; i++) {
            for (int j = 0; j < GobangPanel.BT; j++) {
                data[i][j] = GobangPanel.EMPTY;
                if (i == 0 || i == GobangPanel.BT - 1 || j == 0 || j == GobangPanel.BT - 1)
                    data[i][j] = GobangPanel.BORDER;// 边界
            }
        }
    }



    /**
    *@param isAttack 判断对当前棋子chess是攻击判定还是防守判定,true为攻击判定
     */
    public String ScanBoard(Chess chess,boolean isAttack) {
        String note = "";
        if(!isAttack)//如果是防守判定
        {
            chess.color=3-chess.color;//转换为对方棋子，判断会成什么棋型
        }
        data[chess.x][chess.y] = chess.color;
        for (int dir = 1; dir <= 4; dir++) {//dir从1到4，分别代表四个扫描方向
            int chessCount = 1;  // 和当前位置里连续同色的棋子数 ###
            int spaceCount1 = 0;//同色棋子右边一端空位数###000(r)
            int spaceCount2 = 0;//同色棋子左边一端空位数(l)000###
            int chessRight = 0;//右边隔着一个空位的连续同色的棋子数 ### ###(r)
            int chessLeft = 0;  // 左边边隔着一个空位的连续同色的棋子数 (l)### ###
            int chessRightSpace = 0;//继chessRight之后，连续空位数### ###(r)000
            int chessLeftSpace = 0;  // 继chessLeft之后，连续空位数 000(l)### ###

            int k, n;

            switch (dir) {
                case 1: //水平方向
                    //向右查找相同颜色连续的棋子
                    for (k = chess.x + 1; k < GobangPanel.BT - 1; k++) {
                        if (data[k][chess.y] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //在同色棋子尽头查找连续的空格数
                    while ((k < GobangPanel.BT - 1) && (data[k][chess.y] == 0)) {
                        spaceCount1++;
                        k++;
                    }
                    if (spaceCount1 == 1) {//隔着一个空位有多少同色棋子
                        while ((k < GobangPanel.BT - 1) && (data[k][chess.y] == chess.color)) {
                            chessRight++;
                            k++;
                        }
                        while ((k < GobangPanel.BT - 1) && (data[k][chess.y] == 0)) {//继chessRight之后，有多少空位
                            chessRightSpace++;
                            k++;
                        }
                    }

                    //向相反方向查找相同颜色连续的棋子
                    for (k = chess.x - 1; k >= 1; k--) {
                        if (data[k][chess.y] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //相反方向——在同色棋子尽头查找连续的空格数
                    while ((k > 0) && (data[k][chess.y] == 0)) {
                        spaceCount2++;
                        k--;
                    }
                    if (spaceCount2 == 1) {//相反方向——隔着一个空位有多少同色棋子
                        while ((k >= 1) && (data[k][chess.y] == chess.color)) {
                            chessLeft++;
                            k--;
                        }
                        while ((k >= 1) && (data[k][chess.y] == 0)) {//继chessLeft之后，有多少空位
                            chessLeftSpace++;
                            k--;
                        }
                    }
                    break;

                case 2:  //  垂直方向
                    //向增加的方向查找相同颜色连续的棋子
                    for (k = chess.y + 1; k < GobangPanel.BT - 1; k++) {
                        if (data[chess.x][k] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //在同色棋子尽头查找连续的空格数
                    while ((k < GobangPanel.BT - 1) && (data[chess.x][k] == 0)) {
                        spaceCount1++;
                        k++;
                    }
                    if (spaceCount1 == 1) {//隔着一个空位有多少同色棋子
                        while ((k < GobangPanel.BT - 1) && (data[chess.x][k] == chess.color)) {
                            chessRight++;
                            k++;
                        }
                        while ((k < GobangPanel.BT - 1) && (data[chess.x][k] == 0)) {//继chessRight之后，有多少空位
                            chessRightSpace++;
                            k++;
                        }
                    }

                    //向相反方向查找相同颜色连续的棋子
                    for (k = chess.y - 1; k >= 1; k--) {
                        if (data[chess.x][k] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //相反方向——在同色棋子尽头查找连续的空格数
                    while ((k > 0) && (data[chess.x][k] == 0)) {
                        spaceCount2++;
                        k--;
                    }
                    if (spaceCount2 == 1) {//相反方向——隔着一个空位有多少同色棋子
                        while ((k >= 1) && (data[chess.x][k] == chess.color)) {
                            chessLeft++;
                            k--;
                        }
                        while ((k >= 1) && (data[chess.x][k] == 0)) {//继chessLeft之后，有多少空位
                            chessLeftSpace++;
                            k--;
                        }
                    }
                    break;
                case 3:  //  左上到右下
                    //向增加的方向查找相同颜色连续的棋子
                    for (k = chess.x + 1, n = chess.y + 1; (k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1); k++, n++) {
                        if (data[k][n] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //在同色棋子尽头查找连续的空格数
                    while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {
                        spaceCount1++;
                        k++;
                        n++;
                    }
                    if (spaceCount1 == 1) {//隔着一个空位有多少同色棋子
                        while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == chess.color)) {
                            chessRight++;
                            k++;
                            n++;
                        }
                        while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {//继chessRight之后，有多少空位
                            chessRightSpace++;
                            k++;
                            n++;
                        }
                    }

                    //向相反方向查找相同颜色连续的棋子
                    for (k = chess.x - 1, n = chess.y - 1; (k >= 1) && (n >= 1); k--, n--) {
                        if (data[k][n] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //相反方向——在同色棋子尽头查找连续的空格数
                    while ((k >= 1) && (n >= 1) && (data[k][n] == 0)) {
                        spaceCount2++;
                        k--;
                        n--;
                    }
                    if (spaceCount2 == 1) {//相反方向——隔着一个空位有多少同色棋子
                        while ((k >= 1) && (n >= 1) && (data[k][n] == chess.color)) {
                            chessLeft++;
                            k--;
                            n--;
                        }
                        while ((k >= 1) && (n >= 1) && (data[k][n] == 0)) {//继chessLeft之后，有多少空位
                            chessLeftSpace++;
                            k--;
                            n--;
                        }
                    }
                    break;

                case 4:  //  右上到左下
                    for (k = chess.x + 1, n = chess.y - 1; k < GobangPanel.BT - 1 && n >= 1; k++, n--) {  //查找连续的同色棋子
                        if (data[k][n] == 1) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //在同色棋子尽头查找连续的空格数
                    while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == 0)) {
                        spaceCount1++;
                        k++;
                        n--;
                    }
                    if (spaceCount1 == 1) {//隔着一个空位有多少同色棋子
                        while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == chess.color)) {
                            chessRight++;
                            k++;
                            n--;
                        }
                        while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == 0)) {//继chessRight之后，有多少空位
                            chessRightSpace++;
                            k++;
                            n--;
                        }
                    }

                    //向相反方向查找相同颜色连续的棋子
                    for (k = chess.x - 1, n = chess.y + 1; k >= 1 && n < GobangPanel.BT - 1; k--, n++) {  //查找连续的同色棋子
                        if (data[k][n] == 1) {
                            chessCount++;
                        } else {
                            if (data[k][n] == 0) {
                                spaceCount2 = 1;
                            }
                            break;
                        }
                    }
                    //相反方向——在同色棋子尽头查找连续的空格数
                    while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {
                        spaceCount2++;
                        k--;
                        n++;
                    }
                    if (spaceCount2 == 1) {//相反方向——隔着一个空位有多少同色棋子
                        while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == chess.color)) {
                            chessLeft++;
                            k--;
                            n++;
                        }
                        while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {//继chessLeft之后，有多少空位
                            chessLeftSpace++;
                            k--;
                            n++;
                        }
                    }
            }
            switch (chessCount) {
                case 5://连续同色五子
                    if(!isAttack)//如果是防守
                    {
                        FangChong_Four=true;
                    }
                    else {
                        GongFive=true;
                    }
                    break;
                case 4://连续同色四子
                    if ((spaceCount1 > 0) && (spaceCount2 > 0)) { //活四 011110
                        if(!isAttack)
                        {
                            FangHuoThree=true;
                        }
                        else {
                            GongHuo_Four = true;
                        }

                    } else if ((spaceCount1 > 0 && spaceCount2 == 0)
                            || (spaceCount1 == 0 && spaceCount2 > 0)) {//冲四  #11110 01111#
                        if(isAttack)
                        {
                            GongChong_Four = true;
                        }

                    }
                    break;
                case 3://连续同色三子 111
                    break;
                case 2://连续同色三子 11
                    break;

                case 1:
                    break;


                default:
                    System.out.println("语法填空过了好久");
            }
            System.out.println(chessCount);

        }
        if(!isAttack){
            chess.color=3-chess.color;//恢复棋子颜色
        }

        return "必胜手";

    }
}
