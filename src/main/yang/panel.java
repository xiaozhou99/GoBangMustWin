package main.yang;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

//���öԾ���Ϣ����ͼƬ��panel��
public class panel extends JPanel {
    ImageIcon icon;
    Image img;
    public panel() {
        //�������ı���ͼƬ
        icon = new ImageIcon("src/image/panel1.jpg");
        img = icon.getImage();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //���ñ���ͼƬ���Ը��洰�����е�����С�������Լ����óɹ̶���С
        g.drawImage(img, 0, 0, getSize().width, getSize().height, this);
    }
}

