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

public class MustWinGo {
    public static final int BOARD_SIZE = 15;// 棋盘格数
    public static final int BT = BOARD_SIZE + 2;
    public static ArrayList<ArrayList<String>> mustWinList;
    public static int currentCol;
    public static ArrayList<Integer> selectList;
    public static ArrayList<Integer> previousList;
    public static int undoFlag = 2;

    public MustWinGo() throws Exception {
        mustWinList = readExcel("src/main/resources/MustWinManual.xlsx");
        currentCol = 0;
        selectList = new ArrayList<>();
        for (int i = 0; i < mustWinList.size(); i++) {
            selectList.add(i);
        }
        previousList = selectList;
    }

    public Chess AIGo() {
        if (selectList.size()!=0){
            int row = selectList.get(0);
            String result = mustWinList.get(row).get(currentCol);
            int x = result.charAt(0) - 'A' + 1;
            int y = Integer.parseInt(result.substring(1).trim());
            currentCol++;
            return new Chess(x,y,Chess.BLACK);
        }
        else {
            Chess AIChess =ScanBoard(GobangPanel.boardData);
            if (AIChess == null) {
                undoFlag = 1;
            }
            return AIChess;
        }
    }

    public void PlayerGo(int x,int y) {
        previousList = selectList;
        ArrayList<Integer> tempList = selectList;
        selectList = new ArrayList<>();
        for (int i : tempList) {
            if (currentCol < mustWinList.get(i).size()) {
                String temp = mustWinList.get(i).get(currentCol);
                int tx = temp.charAt(0) - 'A' + 1;
                int ty = Integer.parseInt(temp.substring(1).trim());
                if ( (x==tx) && (y==ty)){
                    selectList.add(i);
                }
            }
        }
        currentCol++;

    }

    public static ArrayList<ArrayList<String>> readExcel(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("文件不存在！");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);
        XSSFSheet sheet = sheets.getSheetAt(0);
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            ArrayList<String> l = new ArrayList<>();

            for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                XSSFCell cell = row.getCell(index);
                String t;
                if (!(t=getString(cell)).equals(""))
                    l.add(t);
            }
            if (!(l.size() == 0))
                list.add(l);
        }

        Collections.sort(list,new SortByOrder());
        for (ArrayList<String> l : list) {
            for (String s : l) {
                System.out.print(s + " ");
            }
            System.out.println("长度"+l.size());
        }
        return list;
    }

    public static String getString(XSSFCell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getStringCellValue();
    }

    public void startGame() {
        currentCol = 0;
        selectList = new ArrayList<>();
        for (int i = 0; i < mustWinList.size(); i++) {
            selectList.add(i);
        }
    }

    public void goback() {
        currentCol--;
        selectList = previousList;
        undoFlag = 2;
    }

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

}

class SortByOrder implements Comparator {
    public int compare (Object o1,Object o2) {
        ArrayList<String> l1 = (ArrayList<String>) o1;
        ArrayList<String> l2 = (ArrayList<String>) o2;
        int size = (l1.size()<l2.size())?(l1.size()):(l2.size());
        for (int i = 0; i < size; i++) {
            String s1 = l1.get(i).trim(),s2 = l2.get(i).trim();

            if (s1.charAt(0) != s2.charAt(0)) {
                if(s1.compareTo(s2) > 0)
                    return 1;
                else if(s1.compareTo(s2) < 0)
                    return -1;
                else
                    continue;
            }
            else {
                int length1 = s1.length(),length2 = s2.length();
                if (length1!=length2){
                    if(length1 > length2)
                        return 1;
                    else
                        return -1;
                }
                else {
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
