package util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.JFrame;
/**
 * 自定义带有圆角的按钮工具类
 * @author admin
 *
 */
public class MyButton extends JButton {
    /* 决定圆角的弧度 */
    public static int radius = 20;
    public static Color COLOR1, COLOR2;
    public static int pink = 3, ashen = 2, sky = 1, stone = 0;
    /* 粉红 */
    public static Color pink1 = new Color(220, 220, 220);
    public static Color pink2 = new Color(39, 121, 181);
    /* 灰白 */
    public static Color ashen1 = new Color(39, 121, 181);
    public static Color ashen2 = new Color(39, 121, 181);
    /* 深宝石蓝 */
    public static Color stone1 = new Color(220, 220, 220);
    public static Color stone2 = new Color(128,138,135);
    /* 淡天蓝色 */
    public static Color sky1 = new Color(39, 121, 181);
    public static Color sky2 = new Color(39, 121, 181);
    /* 光标进入按钮判断 */
    private boolean hover;

    public MyButton() {
        this("", stone);
    }

    public MyButton(String name) {
        this(name, stone);
    }

    public MyButton(String name, int style) {
        super.setText(name);
        if (style == pink) {
            COLOR1 = pink1;
            COLOR2 = pink2;
        }
        if (style == ashen) {
            COLOR1 = ashen1;
            COLOR2 = ashen2;
        }
        if (style == stone) {
            COLOR1 = stone1;
            COLOR2 = stone2;
        }
        if (style == sky) {
            COLOR1 = sky1;
            COLOR2 = sky2;
        }
        paintcolor(COLOR1, COLOR2);
    }

    private void paintcolor(Color COLOR1, Color COLOR2) {
        setMargin(new Insets(0, 0, 0, 0));
        setFont(new Font("微软雅黑", 0, 22));
        setBorderPainted(false);
        setForeground(Color.white);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int height = getHeight();
        int with = getWidth();
        float tran = 0.9F;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        /* GradientPaint是颜色渐变类 */
        GradientPaint p1;
        GradientPaint p2;
        if (getModel().isPressed()) {
            p1 = new GradientPaint(0, 0, new Color(0, 0, 0), 0, height, new Color(220, 220, 220), true);
            p2 = new GradientPaint(0, 1, new Color(0, 0, 0, 50), 0, height, new Color(255, 255, 255, 100), true);
        } else {
            p1 = new GradientPaint(0, 0, new Color(220, 220, 220), 0, height, new Color(0, 0, 0), true);
            p2 = new GradientPaint(0, 1, new Color(255, 255, 255, 100), 0, height, new Color(0, 0, 0, 50), true);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, tran));
        RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, with - 1, height - 1, radius, radius);
        // 最后两个参数数值越大，按钮越圆，以下同理
        Shape clip = g2d.getClip();
        g2d.clip(r2d);
        GradientPaint gp = new GradientPaint(0.0F, 0.0F, COLOR1, 0.0F, height / 2, COLOR2, true);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, with, height);
        g2d.setClip(clip);
        g2d.setPaint(p1);
        g2d.drawRoundRect(0, 0, with - 3, height - 3, radius, radius);
        g2d.setPaint(p2);
        g2d.drawRoundRect(1, 1, with - 3, height - 3, radius, radius);
        g2d.dispose();
        super.paintComponent(g);
    }

}