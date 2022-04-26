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
        //初始化变量
        this.setTitle("26种定式开局");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setBounds(100,100,1100,900);
        this.setLocationRelativeTo(parent);
        this.setResizable(false);
        JPanel jp = new JPanel();
        String str[] = {"寒星局","溪月局","疏星局", "花月局","残月局","雨月局","金星局","松月局","丘月局","新月局","瑞星局","山月局","游星局","长星局","峡月局","恒星局","水月局","流星局","云月局","浦月局","岚月局","银月局","明星局","斜月局","名月局","彗星局"};
        JButton btn[] = new JButton[str.length];       //创建按钮数组
        jp.setLayout(new GridLayout(6,5,10,20));
        for (int i = 0; i <str.length ; i++) {
            btn[i] = new JButton(str[i]);
            btn[i].setSize((this.getWidth()-50)/5,(this.getHeight()-100)/6);
            jp.add(btn[i]);
            setIcon("src/image/26种定式开局/"+str[i]+".png",btn[i]);
            btn[i].setFont(new Font ( "楷体" , Font.PLAIN , 12));//设置按钮字体
        }
        this.add(jp);
        jp.setVisible(true);
        this.setVisible(true);
        pack();                 //自动调整组件大小
    }

    public void setIcon(String file,JButton btn){
        ImageIcon ico=new ImageIcon(file);
        Image temp = ico.getImage().getScaledInstance((btn.getHeight()*4)/5,(btn.getHeight()*4)/5,ico.getImage().SCALE_DEFAULT);
        ico=new ImageIcon(temp);
        btn.setIcon(ico);
    }
}
