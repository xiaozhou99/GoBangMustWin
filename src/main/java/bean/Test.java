package bean;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class Test {
    public static ArrayList<ArrayList<String>> readExcel(String filePath) throws Exception {
        File file = new File(filePath);
        System.out.println(file.getAbsolutePath());
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
        return list;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<ArrayList<String>> records = readExcel("src/main/resources/MustWinManual.xlsx");
        Collections.sort(records,new SortByOrder());
        for (ArrayList<String> l : records) {
            for (String s : l) {
                System.out.print(s + " ");
            }
            System.out.println("长度"+l.size());
        }
//        System.out.println("J10".compareTo("J11"));
    }

    public static String getString(XSSFCell cell) {
        if (cell == null) {
            return "";
        }
        return cell.getStringCellValue();
    }
}

