package bean;

//���ݱ����
public class DataAnnotations {

    private int[][] data;

    //��
    boolean GongHuo_Three = false;//����
    boolean GongHuo_Four = false;//����
    boolean GongChong_Four = false;//����
    boolean GongFive=false;//����
    //��
    boolean FangHuoThree = false;//������
    boolean FangChong_Four = false;//������

    public DataAnnotations() {
        //һ��һ����������״̬����ɨ�������ϵ�����
        this.data = new int[GobangPanel.BT][GobangPanel.BT];
        for (int i = 0; i < GobangPanel.BT; i++) {
            for (int j = 0; j < GobangPanel.BT; j++) {
                data[i][j] = GobangPanel.EMPTY;
                if (i == 0 || i == GobangPanel.BT - 1 || j == 0 || j == GobangPanel.BT - 1)
                    data[i][j] = GobangPanel.BORDER;// �߽�
            }
        }
    }



    /**
    *@param isAttack �ж϶Ե�ǰ����chess�ǹ����ж����Ƿ����ж�,trueΪ�����ж�
     */
    public String ScanBoard(Chess chess,boolean isAttack) {
        String note = "";
        if(!isAttack)//����Ƿ����ж�
        {
            chess.color=3-chess.color;//ת��Ϊ�Է����ӣ��жϻ��ʲô����
        }
        data[chess.x][chess.y] = chess.color;
        for (int dir = 1; dir <= 4; dir++) {//dir��1��4���ֱ�����ĸ�ɨ�跽��
            int chessCount = 1;  // �͵�ǰλ��������ͬɫ�������� ###
            int spaceCount1 = 0;//ͬɫ�����ұ�һ�˿�λ��###000(r)
            int spaceCount2 = 0;//ͬɫ�������һ�˿�λ��(l)000###
            int chessRight = 0;//�ұ߸���һ����λ������ͬɫ�������� ### ###(r)
            int chessLeft = 0;  // ��߱߸���һ����λ������ͬɫ�������� (l)### ###
            int chessRightSpace = 0;//��chessRight֮��������λ��### ###(r)000
            int chessLeftSpace = 0;  // ��chessLeft֮��������λ�� 000(l)### ###

            int k, n;

            switch (dir) {
                case 1: //ˮƽ����
                    //���Ҳ�����ͬ��ɫ����������
                    for (k = chess.x + 1; k < GobangPanel.BT - 1; k++) {
                        if (data[k][chess.y] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (data[k][chess.y] == 0)) {
                        spaceCount1++;
                        k++;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (data[k][chess.y] == chess.color)) {
                            chessRight++;
                            k++;
                        }
                        while ((k < GobangPanel.BT - 1) && (data[k][chess.y] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.x - 1; k >= 1; k--) {
                        if (data[k][chess.y] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k > 0) && (data[k][chess.y] == 0)) {
                        spaceCount2++;
                        k--;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (data[k][chess.y] == chess.color)) {
                            chessLeft++;
                            k--;
                        }
                        while ((k >= 1) && (data[k][chess.y] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                        }
                    }
                    break;

                case 2:  //  ��ֱ����
                    //�����ӵķ��������ͬ��ɫ����������
                    for (k = chess.y + 1; k < GobangPanel.BT - 1; k++) {
                        if (data[chess.x][k] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (data[chess.x][k] == 0)) {
                        spaceCount1++;
                        k++;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (data[chess.x][k] == chess.color)) {
                            chessRight++;
                            k++;
                        }
                        while ((k < GobangPanel.BT - 1) && (data[chess.x][k] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.y - 1; k >= 1; k--) {
                        if (data[chess.x][k] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k > 0) && (data[chess.x][k] == 0)) {
                        spaceCount2++;
                        k--;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (data[chess.x][k] == chess.color)) {
                            chessLeft++;
                            k--;
                        }
                        while ((k >= 1) && (data[chess.x][k] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                        }
                    }
                    break;
                case 3:  //  ���ϵ�����
                    //�����ӵķ��������ͬ��ɫ����������
                    for (k = chess.x + 1, n = chess.y + 1; (k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1); k++, n++) {
                        if (data[k][n] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {
                        spaceCount1++;
                        k++;
                        n++;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == chess.color)) {
                            chessRight++;
                            k++;
                            n++;
                        }
                        while ((k < GobangPanel.BT - 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                            n++;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.x - 1, n = chess.y - 1; (k >= 1) && (n >= 1); k--, n--) {
                        if (data[k][n] == chess.color) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k >= 1) && (n >= 1) && (data[k][n] == 0)) {
                        spaceCount2++;
                        k--;
                        n--;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (n >= 1) && (data[k][n] == chess.color)) {
                            chessLeft++;
                            k--;
                            n--;
                        }
                        while ((k >= 1) && (n >= 1) && (data[k][n] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                            n--;
                        }
                    }
                    break;

                case 4:  //  ���ϵ�����
                    for (k = chess.x + 1, n = chess.y - 1; k < GobangPanel.BT - 1 && n >= 1; k++, n--) {  //����������ͬɫ����
                        if (data[k][n] == 1) {
                            chessCount++;
                        } else {
                            break;
                        }
                    }
                    //��ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == 0)) {
                        spaceCount1++;
                        k++;
                        n--;
                    }
                    if (spaceCount1 == 1) {//����һ����λ�ж���ͬɫ����
                        while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == chess.color)) {
                            chessRight++;
                            k++;
                            n--;
                        }
                        while ((k < GobangPanel.BT - 1) && (n >= 1) && (data[k][n] == 0)) {//��chessRight֮���ж��ٿ�λ
                            chessRightSpace++;
                            k++;
                            n--;
                        }
                    }

                    //���෴���������ͬ��ɫ����������
                    for (k = chess.x - 1, n = chess.y + 1; k >= 1 && n < GobangPanel.BT - 1; k--, n++) {  //����������ͬɫ����
                        if (data[k][n] == 1) {
                            chessCount++;
                        } else {
                            if (data[k][n] == 0) {
                                spaceCount2 = 1;
                            }
                            break;
                        }
                    }
                    //�෴���򡪡���ͬɫ���Ӿ�ͷ���������Ŀո���
                    while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {
                        spaceCount2++;
                        k--;
                        n++;
                    }
                    if (spaceCount2 == 1) {//�෴���򡪡�����һ����λ�ж���ͬɫ����
                        while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == chess.color)) {
                            chessLeft++;
                            k--;
                            n++;
                        }
                        while ((k >= 1) && (n < GobangPanel.BT - 1) && (data[k][n] == 0)) {//��chessLeft֮���ж��ٿ�λ
                            chessLeftSpace++;
                            k--;
                            n++;
                        }
                    }
            }
            switch (chessCount) {
                case 5://����ͬɫ����
                    if(!isAttack)//����Ƿ���
                    {
                        FangChong_Four=true;
                    }
                    else {
                        GongFive=true;
                    }
                    break;
                case 4://����ͬɫ����
                    if ((spaceCount1 > 0) && (spaceCount2 > 0)) { //���� 011110
                        if(!isAttack)
                        {
                            FangHuoThree=true;
                        }
                        else {
                            GongHuo_Four = true;
                        }

                    } else if ((spaceCount1 > 0 && spaceCount2 == 0)
                            || (spaceCount1 == 0 && spaceCount2 > 0)) {//����  #11110 01111#
                        if(isAttack)
                        {
                            GongChong_Four = true;
                        }

                    }
                    break;
                case 3://����ͬɫ���� 111
                    break;
                case 2://����ͬɫ���� 11
                    break;

                case 1:
                    break;


                default:
                    System.out.println("�﷨��չ��˺þ�");
            }
            System.out.println(chessCount);

        }
        if(!isAttack){
            chess.color=3-chess.color;//�ָ�������ɫ
        }

        return "��ʤ��";

    }
}
