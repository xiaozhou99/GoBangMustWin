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

//��ʤ�����࣬�����Զ�ά����ķ�ʽ����
public class MustWinGo {
    public static final int BOARD_SIZE = 15;// ���̸���
    public static final int BT = 17; //BOARD + 2
    public static ArrayList<ArrayList<String>> mustWinList; //�����ʤ����
    public static int currentCol;   //��ǰ���е���currentCol��������ֱ���������л�ȡ�߷�
    public static ArrayList<Integer> searchList; //�����������б�����ǰ���µ����ɲ�����������ƥ���������ţ�����ֱ�Ӵ���Щ����������
    public static ArrayList<Integer> previousList; //searchList�ı��ݣ����ڻ����ָ�
    public static int undoFlag = 2;  //����ʱ�õ�
    public static ManualManager manualManager = new ManualManager(); //�������ļ��洢��ȡ��


    public MustWinGo() throws Exception {
        mustWinList = manualManager.readBinFile("src/main/resources/MustWinManual.bin");  //��ȡ�������ļ���ʼ�������б�
//        mustWinList = readExcel("src/main/resources/MustWinManual.xlsx");  //��ȡ�������ļ���ʼ�������б�
        Collections.sort(mustWinList,new SortByOrder());  //�����װ���������
        /*for(ArrayList<String> l : mustWinList){
            for(String s:l)
                System.out.print(s+" ");
            System.out.println();
        }*/
        currentCol = 0;
        searchList = new ArrayList<>();
        for (int i = 0; i < mustWinList.size(); i++) {  //��searchList��ʼ��Ϊ����������ţ����ڵ�һ��Ĭ�϶���������Ԫ
            searchList.add(i);
        }
        previousList = searchList;  //����
    }

    //���ݰ�����һ�����ӡ���ʤ������������غ��ӵ���һ���߷�
    public Chess AIGo() {
        if (searchList.size()!=0){  //���������б�ǿ�
            int row = searchList.get(0);  //Ĭ���������ɿ��������еĵ�һ��
            String result = mustWinList.get(row).get(currentCol);  //��ȡ�õ�n�����ӵ��߷�����ʽ�����ơ�H8��
            int x = result.charAt(0) - 'A' + 1;
            int y = Integer.parseInt(result.substring(1).trim()); //��ȡx y����
            currentCol++;
            return new Chess(x,y,Chess.BLACK);
        }
        else { //�����������б�Ϊ�գ�˵��������ʤ������Ҫ��ɵ���5���߷�
            Chess AIChess =ScanBoard(GobangPanel.boardData); //ɨ�����̻�ȡ�����߷�
            if (AIChess == null) {  //���������������߷���˵���û�û�а��������£��Ҳ���������һ���߷�
                undoFlag = 1; //���û���flag
            }
            return AIChess;
        }
    }

    // ���ݰ��ӵ����ӣ���������һ����
    public void PlayerGo(int x,int y) {
        previousList = searchList;  //����searchList
        ArrayList<Integer> tempList = searchList;  //����ʱ������searchList�������漰��ɾ��Ԫ�أ�ȷ�����������б�Ԫ��
        searchList = new ArrayList<>(); //����Ϊ��
        for (int i : tempList) {  //����ÿһ������������
            if (currentCol < mustWinList.get(i).size()) {  //�����жϸ����׵Ĳ�����û����ô�ಽ
                String temp = mustWinList.get(i).get(currentCol);  //��ȡ�����׵�currentCol���߷�
                int tx = temp.charAt(0) - 'A' + 1;
                int ty = Integer.parseInt(temp.substring(1).trim());  //��ȡ����߷���xy����
                if ( (x==tx) && (y==ty)){ //���û��������ϵ�ƥ�䣬��һ�£���˵�����ӷ��ϸ������߷��������������
                    searchList.add(i);
                }
            }
        }
        currentCol++; //��һ��

    }

    //��ȡexcel�ļ�����ʼ����ʤ�����б�
    public static ArrayList<ArrayList<String>> readExcel(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("�ļ������ڣ�");
        }

        InputStream in = new FileInputStream(file);

        XSSFWorkbook sheets = new XSSFWorkbook(in);  //��ȡ�ļ�
        XSSFSheet sheet = sheets.getSheetAt(0); //��ȡ��һ��������
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { //���ж�ȡ
            XSSFRow row = sheet.getRow(i);
            ArrayList<String> l = new ArrayList<>(); //l��ʾһ�У���һ�����������

            for (int index = 0; index < row.getPhysicalNumberOfCells(); index++) {
                XSSFCell cell = row.getCell(index);
                String t;
                if (!(t=getString(cell)).equals("")) //��ȡ��Ԫ�����ݣ��ǿ��򱣴�
                    l.add(t);
            }
            if (!(l.size() == 0))
                list.add(l); //��������ּ��������б�
        }

//        Collections.sort(list,new SortByOrder());  //�����װ���������
        return list;
    }

    //����Ԫ�����ݲ�����
    public static String getString(XSSFCell cell) {
        if (cell == null) { //�����򷵻ؿ��ַ���
            return "";
        }
        return cell.getStringCellValue().trim(); //�����ַ�����ʽ���ص�Ԫ������
    }

    // ���ֳ�ʼ��������
    public void startGame() {
        currentCol = 0;
        searchList = new ArrayList<>();
        for (int i = 0; i < mustWinList.size(); i++) {
            searchList.add(i);
        }
        previousList = searchList;
    }

    // ����ʱ���ã��������ˣ�searchList���¸�ֵΪpreviousList������ǰ�������б�
    public void goback() {
        currentCol--;
        searchList = previousList;
        undoFlag = 2;  //�ָ�Ĭ��
    }

    //ɨ�����̻�ȡ�ɳ��ġ������㣬�ɹ�������
    public Chess ScanBoard(int[][] data) {
        for (int i = 1; i < BT - 1; i++) {
            for (int j = 1; j < BT - 1; j++) {//�����̵����е�ѭ��
                if (data[i][j] == 0) {  //����ǿ�λ

                    for (int m = 1; m <= 4; m++) {
                        int chessCount = 1;  // �͵�ǰλ��������ͬɫ�������� ###
                        int spaceCount1 = 0;
                        int spaceCount2 = 0;
                        data[i][j] = 1;//����º���
                        switch (m) {
                            case 1: //ˮƽ����
                                //���Ҳ�����ͬ��ɫ����������
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

                                //���෴���������ͬ��ɫ����������
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
                            case 2:  //  ��ֱ����
                                //�����ӵķ��������ͬ��ɫ����������
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
                                //���෴���������ͬ��ɫ����������
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
                            case 3:  //  ���ϵ�����
                                //�����ӵķ��������ͬ��ɫ����������
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

                                //���෴���������ͬ��ɫ����������
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
                            case 4:  //  ���ϵ�����
                                for (int k = i + 1, n = j - 1; k < BT - 1 && n >= 1; k++, n--) {  //����������ͬɫ����
                                    if (data[k][n] == 1) {
                                        chessCount++;
                                    } else {
                                        if (data[k][n] == 0) {
                                            spaceCount1 = 1;
                                        }
                                        break;
                                    }
                                }
                                for (int k = i - 1, n = j + 1; k >= 1 && n < BT - 1; k--, n++) {  //����������ͬɫ����
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
                            case 5:  //����Ѿ���������5�ӣ���Ӯ��  11111
                                return new Chess(i, 16-j);

                            case 4://��������  1111
                                if ((spaceCount1 > 0) && (spaceCount2 > 0)) { //����
                                    return new Chess(i, 16-j);
                                }
//                            default:
//                                System.out.println("�޳��Ļ���");

                        }
                    }
                }
            }
        }
        System.out.println("�޳��Ļ���");
        return null;
    }

    // ��˳���������
    public void insertManual(ArrayList<String> m) {
        mustWinList.add(m);  //���������б���β������
        Collections.sort(mustWinList,new SortByOrder());  //�ٽ�������
    }
}

//�����࣬���װ��������򣬼�����һ����ͬ�߷��������������
class SortByOrder implements Comparator {
    public int compare (Object o1,Object o2) {
        ArrayList<String> l1 = (ArrayList<String>) o1; //��ȡ����������ֶ���Ƚ�ԭ��
        ArrayList<String> l2 = (ArrayList<String>) o2;

        int size = (l1.size()<l2.size())?(l1.size()):(l2.size());  //�Խ��ٲ���������Ϊ�Ƚϱ�׼������Խ��
        for (int i = 0; i < size; i++) {
            String s1 = l1.get(i).trim(),s2 = l2.get(i).trim();  //��ȡ�������׵�i�е�����

            if (s1.charAt(0) != s2.charAt(0)) {  //�ȱȽ���ĸ��Ŀ��������������������������ĸ��ͬ�������ȡ��һ���������Ƚ�
                if(s1.compareTo(s2) > 0)
                    return 1;
                else if(s1.compareTo(s2) < 0)
                    return -1;
                else
                    continue;
            }
            else {    //����ĸ������ͬ����Ƚ����ֲ��ֵĳ��ȣ����ȴ�ı�ʾ�Ǹ����ִ�Ŀ����������
                int length1 = s1.length(),length2 = s2.length();
                if (length1!=length2){
                    if(length1 > length2)
                        return 1;
                    else
                        return -1;
                }
                else {  //�����ֲ��ֳ���Ҳ��ͬ����ֱ�ӱȴ�С��Ŀ����������������ͬ��ȡ��һ��������бȽ�
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
