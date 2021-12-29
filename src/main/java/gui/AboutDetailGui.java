package gui;

import db.DetailDB;
import db.ProjectDB;

import javax.swing.*;
import java.awt.*;

public class AboutDetailGui {


    public void createAbout(JPanel jPanel) {

        JTextArea jTextArea = new JTextArea(5,60);
        String text = "程序名称：项目统计（单机版）\n" +
                        "版本： V1.0.0\n" +
                        "语言：中文(简体,中国)\n" +
                        "类型：windows应用程序\n" +
                        "功能说明：对Excel文件中大量数据进行按列统计，统计后数据进行存储与可视化，同是支持对多个项目，每个项目中多个子项进行统计。\n" +
                        "开发者：王世磊\n" +
                        "联系方式：18314263373";
        jTextArea.setText(text);
        jTextArea.setLineWrap(true);
        jTextArea.setFont(new Font("微软雅黑", 0, 20));
        jTextArea.setEditable(false);
        jTextArea.setBackground (null);
        jTextArea.setDisabledTextColor(Color.BLACK);
        JLabel textLable = new JLabel("<html> <span style=\"font-family: 黑体, SimHei; font-size: 24px;\"><strong><span style=\"font-family: 黑体, SimHei;\">关于</span></strong></html>");
        jPanel.add(textLable);
        jPanel.add(jTextArea);
    }
}
