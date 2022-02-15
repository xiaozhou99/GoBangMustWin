package bean;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//必胜棋谱类，棋谱以二维数组的方式保存
public class MustWinGo {
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = 17; //BOARD + 2
    public static ArrayList<ArrayList<String>> mustWinList; //保存必胜棋谱
    public static int currentCol;   //当前进行到第currentCol步，后续直接搜索该列获取走法
    public static ArrayList<Integer> searchList; //搜索的棋谱列表，保存前面下的若干步都与棋盘上匹配的棋谱序号，后续直接从这些棋谱中搜索
    public static ArrayList<Integer> previousList; //searchList的备份，便于悔棋后恢复
    public static int undoFlag = 2;  //悔棋时用到
    public static ManualManager manualManager = new ManualManager(); //二进制文件存储读取类


    public MustWinGo() throws Exception {
        mustWinList = manualManager.readBinFile("src/main/resources/MustWinManual.bin");  //读取二进制文件初始化棋谱列表
//        mustWinList = readExcel("src/main/resources/MustWinManual.xlsx");  //读取二进制文件初始化棋谱列表
        Collections.sort(mustWinList,new SortByOrder());  //将棋谱按坐标排序
        /*for(ArrayList<String> l : mustWinList){
            for(String s:l)
                System.out.print(s+" ");
            System.out.println();
        }*/
        currentCol = 0;
        searchList = new ArrayList<>();
        for (int i = 0; i < mustWinList.size(); i++) {  //将searchList初始化为所有棋谱序号，由于第一步默认都是下载天元
            searchList.add(i);
        }
        previousList = searchList;  //备份
    }

    //根据白子上一步落子、必胜棋谱情况，返回黑子的下一步走法
    public Chess AIGo() {
        if (searchList.size()!=0){  //搜索棋谱列表非空
            int row = searchList.get(0);  //默认搜索若干可走棋谱中的第一个
            String result = mustWinList.get(row).get(currentCol);  //获取该第n步黑子的走法，形式是类似“H8”
            int x = result.charAt(0) - 'A' + 1;
            int y = Integer.parseInt(result.substring(1).trim()); //获取x y坐标
            currentCol++;
            return new Chess(x,y,Chess.BLACK);
        }
        else { //若搜索棋谱列表为空，说明黑子已胜，后续要完成到连5的走法
            Chess AIChess =ScanBoard(GobangPanel.boardData); //扫描棋盘获取连五走法
            if (AIChess == null) {  //若上面亦无连五走法，说明用户没有按照棋谱下，找不到黑子下一步走法
                undoFlag = 1; //设置悔棋flag
            }
            return AIChess;
        }
    }

    // 根据白子的落子，棋谱往下一步走
    public void PlayerGo(int x,int y) {
        previousList = searchList;  //备份searchList
        ArrayList<Integer> tempList = searchList;  //建临时变量存searchList，由于涉及到删除元素，确保正常访问列表元素
        searchList = new ArrayList<>(); //重置为空
        for (int i : tempList) {  //遍历每一个可搜索棋谱
            if (currentCol < mustWinList.get(i).size()) {  //首先判断该棋谱的步数有没有这么多步
                String temp = mustWinList.get(i).get(currentCol);  //获取该棋谱第currentCol步走法
                int tx = temp.charAt(0) - 'A' + 1;
                int ty = Integer.parseInt(temp.substring(1).trim());  //获取这个走法的xy坐标
                if ( (x==tx) && (y==ty)){ //与用户在棋盘上的匹配，若一致，则说明白子符合该棋谱走法，保存棋谱序号
                    searchList.add(i);
                }
            }
        }
        currentCol++; //下一步

    }

    //读取excel文件，初始化必胜棋谱列表
    public static ArrayList<ArrayList<String>> readExcel(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("文件不存在！");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);  //获取文件
        XSSFSheet sheet = sheets.getSheetAt(0); //获取第一个工作簿
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { //按行读取
            XSSFRow row = sheet.getRow(i);
            ArrayList<String> l = new ArrayList<>(); //l表示一行，即一个完整的棋局

            for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                XSSFCell cell = row.getCell(index);
                String t;
                if (!(t=getString(cell)).equals("")) //读取单元格内容，非空则保存
                    l.add(t);
            }
            if (!(l.size() == 0))
                list.add(l); //将完整棋局加入棋谱列表
        }

//        Collections.sort(list,new SortByOrder());  //将棋谱按坐标排序
        return list;
    }

    //处理单元格内容并返回
    public static String getString(XSSFCell cell) {
        if (cell == null) { //若空则返回空字符串
            return "";
        }
        return cell.getStringCellValue().trim(); //否则按字符串形式返回单元格内容
    }

    // 开局初始化各变量
    public void startGame() {
        currentCol = 0;
        searchList = new ArrayList<>();
        for (int i = 0; i < mustWinList.size(); i++) {
            searchList.add(i);
        }
        previousList = searchList;
    }

    // 悔棋时调用，步数回退，searchList重新赋值为previousList即操作前的搜索列表
    public void goback() {
        currentCol--;
        searchList = previousList;
        undoFlag = 2;  //恢复默认
    }

    //扫描棋盘获取可冲四、活三点，可构成五连
    public Chess ScanBoard(int[][] data) {
        for (int i = 1; i < BT - 1; i++) {
            for (int j = 1; j < BT - 1; j++) {//对棋盘的所有点循环
                if (data[i][j] == 0) {  //如果是空位

                    for (int m = 1; m <= 4; m++) {
                        int chessCount = 1;  // 和当前位置里连续同色的棋子数 ###
                        int spaceCount1 = 0;
                        int spaceCount2 = 0;
                        data[i][j] = 1;//如果下黑子
                        switch (m) {
                            case 1: //水平方向
                                //向右查找相同颜色连续的棋子
                                for (int k = i + 1; k < BT - 1; k++) {
                                    if (data[k][j] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][j] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }

                                //向相反方向查找相同颜色连续的棋子
                                for (int k = i - 1; k >= 1; k--) {
                                    if (data[k][j] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][j] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 2:  //  垂直方向
                                //向增加的方向查找相同颜色连续的棋子
                                for (int k = j + 1; k < BT - 1; k++) {
                                    if (data[i][k] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[i][k] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }
                                //向相反方向查找相同颜色连续的棋子
                                for (int k = j - 1; k >= 1; k--) {
                                    if (data[i][k] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[i][k] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 3:  //  左上到右下
                                //向增加的方向查找相同颜色连续的棋子
                                for (int k = i + 1, n = j + 1; (k < BT - 1) && (n < BT - 1); k++, n++) {
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }

                                //向相反方向查找相同颜色连续的棋子
                                for (int k = i - 1, n = j - 1; (k >= 1) && (m >= 1); k--, n--) {
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                                break;
                            case 4:  //  右上到左下
                                for (int k = i + 1, n = j - 1; k < BT - 1 && n >= 1; k++, n--) {  //查找连续的同色棋子
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }
                                for (int k = i - 1, n = j + 1; k >= 1 && n < BT - 1; k--, n++) {  //查找连续的同色棋子
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount2 = 1;
                                        }
                                        break;
                                    }
                                }
                        }
                        data[i][j] = 0;
                        switch (chessCount) {
                            case 5:  //如果已经可以连成5子，则赢棋  11111
                                return new Chess(i, 16-j);

                            case 4://连续四子  1111
                                if ((spaceCount1 > 0) && (spaceCount2 > 0)) { //活四
                                    return new Chess(i, 16-j);
                                }
//                            default:
//                                System.out.println("无冲四活三");

                        }
                    }
                }
            }
        }
        System.out.println("无冲四活三");
        return null;
    }

    // 按顺序插入棋谱
    public void insertManual(ArrayList<String> m) {
        mustWinList.add(m);  //先在棋谱列表中尾插棋谱
        Collections.sort(mustWinList,new SortByOrder());  //再进行排序
    }
}

//排序类，棋谱按坐标排序，即按第一个不同走法的坐标进行排序
class SortByOrder implements Comparator {
    public int compare (Object o1,Object o2) {
        ArrayList<String> l1 = (ArrayList<String>) o1; //获取两个完整棋局定义比较原则
        ArrayList<String> l2 = (ArrayList<String>) o2;

        int size = (l1.size()<l2.size())?(l1.size()):(l2.size());  //以较少步数的棋谱为比较标准，以免越界
        for (int i = 0; i < size; i++) {
            String s1 = l1.get(i).trim(),s2 = l2.get(i).trim();  //获取两个棋谱第i列的坐标

            if (s1.charAt(0) != s2.charAt(0)) {  //先比较字母，目的是升序排序，若两个坐标首字母相同，则继续取下一步坐标来比较
                if(s1.compareTo(s2) > 0)
                    return 1;
                else if(s1.compareTo(s2) < 0)
                    return -1;
                else
                    continue;
            }
            else {    //若字母部分相同，则比较数字部分的长度，长度大的表示那个数字大，目的升序排序
                int length1 = s1.length(),length2 = s2.length();
                if (length1!=length2){
                    if(length1 > length2)
                        return 1;
                    else
                        return -1;
                }
                else {  //若数字部分长度也相同，则直接比大小，目的升序排序，若还相同则取下一个坐标进行比较
                    if(s1.compareTo(s2) > 0)
                        return 1;
                    else if(s1.compareTo(s2) < 0)
                        return -1;
                    else
                        continue;
                }
            }

        }
        return 0;
    }
}
