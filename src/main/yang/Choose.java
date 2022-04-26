package main.yang;

import javax.swing.*;
import java.awt.*;

public class Choose extends JDialog {
    private JFrame parent=null;
    public Choose(JFrame parent, boolean modal) throws Exception {
        super(parent,modal);
        this.parent=parent;
        initComponents();
    }

    private void initComponents() throws Exception{
        //��ʼ������
        this.setTitle("26�ֶ�ʽ����");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds(100,100,1100,900);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        JPanel jp = new JPanel();
        String str[] = {"���Ǿ�","Ϫ�¾�","���Ǿ�", "���¾�","���¾�","���¾�","���Ǿ�","���¾�","���¾�","���¾�","���Ǿ�","ɽ�¾�","���Ǿ�","���Ǿ�","Ͽ�¾�","���Ǿ�","ˮ�¾�","���Ǿ�","���¾�","���¾�","��¾�","���¾�","���Ǿ�","б�¾�","���¾�","���Ǿ�"};
        JButton btn[] = new JButton[str.length];       //������ť����
        jp.setLayout(new GridLayout(6,5,10,20));
        for (int i = 0; i <str.length ; i++) {
            btn[i] = new JButton(str[i]);
            btn[i].setSize((this.getWidth()-50)/5,(this.getHeight()-100)/6);
            jp.add(btn[i]);
            setIcon("src/image/26�ֶ�ʽ����/"+str[i]+".png",btn[i]);
            btn[i].setFont(new Font ( "����" , Font.PLAIN , 12));//���ð�ť����
        }
        this.add(jp);
        jp.setVisible(true);
        this.setVisible(true);
        pack();                 //�Զ����������С
    }

    public void setIcon(String file,JButton btn){
        ImageIcon ico=new ImageIcon(file);
        Image temp = ico.getImage().getScaledInstance((btn.getHeight()*4)/5,(btn.getHeight()*4)/5,ico.getImage().SCALE_DEFAULT);
        ico=new ImageIcon(temp);
        btn.setIcon(ico);
    }
}
