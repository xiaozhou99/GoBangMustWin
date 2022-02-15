package bean;

import java.io.*;
import java.util.ArrayList;

// ��ʤ�߷��Զ������ļ��洢����ȡ�������ļ��Ĳ�����
public class ManualManager {
    // ����ʤ�߷�����������ļ��У�����Ϊ��ʤ�߷��Ķ�ά������ʽ
    public void saveBinManual(ArrayList<ArrayList<String>> list) throws FileNotFoundException {
        File file = new File( "src/main/resources/MustWinManual.bin" ); //�������ļ�·��
        OutputStream os = new FileOutputStream(file);   //�ļ����������
        try{
            for (ArrayList<String> line : list) {   //line����ά�����е�һ�У���һ�������ı�ʤ�߷����
                byte[] temp = new byte[line.size() + 1];    //�����Ӧ�ֽ�������ʱ����������+1��ԭ��������ȡ����β�����0����ʾ���н���
                for (int i = 0;i<line.size();i++) {     //line.get(i)����ȡ�����еĵ�i���߷�����
                    temp[i] = merge2BytesTo1Byte(line.get(i));  //merge2BytesTo1ByteΪ��������ת��Ϊ��Ӧ���ֽ���ʽ�����崦������ڸú���������
                }
                temp[line.size()] = 0;  //��β��0��ʾ

                os.write(temp);  //�����ֽ�����д���ļ�
            }
            os.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // ��ȡ�������ļ�����ȡ��ʤ�߷���������ʽ�Ƕ�ά������ʽ
    public ArrayList<ArrayList<String>> readBinFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file); //�ļ�������
        ArrayList<ArrayList<String>> result = new ArrayList<>(); //���صĽ������

        while (true) {  // ��ȡÿһ�б�ʤ���
            int i = 0; //��ȡ����i������
            byte[] temp = new byte[225];  //��ʼ��һ���ֽ�������ʱ���������ڱ���һ��������֣���С225�������225��
            ArrayList<String> list = new ArrayList<>(); //һ��
            boolean breakFlag = false; //�ж��Ƿ��ȡ���ļ�ĩβ��flag

            while (true) {  //��ȡһ�ֱ�ʤ����е�ÿ���߷�����
                if((fis.read(temp,i,1)) == -1){  //������-1����ʾ���ļ�ĩβ����ȡ����
                                                    //read(temp,i,1)��ʾ����ȡ�ļ��г���Ϊ1���ֽ����ݣ����浽temp�ֽ��������±�Ϊi��λ��
                                                    //�������ֶ�ȡ��ʽ��ԭ���ǣ�����read���ص����޷��ŵ�byte��ֵ���ܻᷢ���仯��read(temp,i,1)ʱ���൱�ڰ��ļ��е�i���ֽڶ��뵽��b[i]�У�������̲�û�н���ת��Ϊ�޷�����ֵ�Ĺ���
                    breakFlag = true;
                    break;
                };
                if (temp[i]!= 0) {
                    list.add(binToStringCoo(temp[i]));  //binToStringCoo�ǽ�byte���͵�����ת����String���͵����꣬�硰H8��
                    i++;
                }
                else {  //���������ļ��ж�ȡ��0����ʾ���ж�ȡ����
                    result.add(list); //����������ּ����ʤ�����б�
                    break; //����ѭ��������һ��������ֶ�ȡ
                }
            }

            if (breakFlag == true) //����flagΪ�棬��ʾ�ļ���ȡ�����������ļ���ȡ
                break;

        }
        return result;
    }

    //��������ת��Ϊ��Ӧ���ֽ���ʽ����"H8"�У���ĸ����Ҳ��������������������A-O��Ӧ1-15�����ֲ�������
    //���������硰A1�������ơ�11����ת�ɶ����Ƽ���00010001����ͬ��H8������01000100�� ������byte���ͣ�����8λ
    public byte merge2BytesTo1Byte(String str){ //str��������
        byte b = 0;
        char s;
        s = str.charAt(0); //s���������ĸ����
        String e = str.substring(1);  //e����������ֲ���
        b = (byte)(((s-64)<<4) + Integer.parseInt(e)); //s-64����ƫ��������H��8��������λ�������ڸ���λ��ʾ����
                                                        // Integer.parseInt(e)�������ֲ���ת��Ϊ��������������Ӻ�ת����byte��ʽ
        return b;
    }

    //��byte���͵�����ת����String���͵����꣬��8bitת�ɡ�H8������ʽ
    public String binToStringCoo (byte i){
        String s = "";
        for (int j = 7; j >= 0; j--) { //������byte�����Բ���ķ�ʽ�洢���ʽ�ÿλ��1������ܵõ�����������Ҫ�Ķ����ƴ���ʽ
                                        // �硰H8���Ķ����ƴ�Ϊ��01000100������������Ҫ��
            if (((1 << j) & i) != 0)
                s += "1";
            else
                s += "0";
        }

        String sx = s.substring(0,4),sy = s.substring(4),result = ""; //����λ���룬����λ��ʾx���꣬����λ��ʾy����
        char x;
        String y="";

        int xvalue = (sx.charAt(0)-48) * 8 + (sx.charAt(1)-48) * 4 + (sx.charAt(2)-48) * 2 + (sx.charAt(3)-48);//��������ʮ����ֵ��-48�ǽ��ַ��͵�"1/0"ת�������ֵ�"1/0"
        x = (char)(64 + xvalue); //����64����ֵ�õ���Ӧ��д��ĸ��ASC���룬ǿ��ת����char���ͼ���ȡ��x����

        int yvalue = (sy.charAt(0)-48) * 8 + (sy.charAt(1)-48) * 4 + (sy.charAt(2)-48) * 2 + (sy.charAt(3)-48);//��������ʮ����ֵ
        y = Integer.toString(yvalue); //�������ֵ����y����

        result = x + y; //����xy����
        return result;
    }

}
