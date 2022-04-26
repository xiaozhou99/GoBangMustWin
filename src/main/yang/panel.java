package main.yang;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

//设置对局信息背景图片的panel类
public class panel extends JPanel {
    ImageIcon icon;
    Image img;
    public panel() {
        //设置面板的背景图片
        icon = new ImageIcon("src/image/panel1.jpg");
        img = icon.getImage();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //设置背景图片可以跟随窗口自行调整大小，可以自己设置成固定大小
        g.drawImage(img, 0, 0, getSize().width, getSize().height, this);
    }
}

