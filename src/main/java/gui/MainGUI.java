package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainGUI extends JFrame{
    static JMenuBar jMenu = new JMenuBar ();
    static JPanel jPanel = new JPanel();

    ViewDetailGui viewDetailGui = new ViewDetailGui();
    OutPutDetailGui outPutDetailGui = new OutPutDetailGui();
    InputDetailGui inputDetailGui = new InputDetailGui();
    AboutDetailGui aboutDetailGui = new AboutDetailGui();

    public MainGUI() {
        // 创建 JFrame 实例
        this.setTitle("数据统计（单机版）");
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);// 采用指定的窗口装饰风格
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(null);//清空布局
        this.setIconImage(Toolkit.getDefaultToolkit().createImage("./统计.png"));// 设置标题栏上左上角的图标
        this.setVisible(true);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();//获得屏幕尺寸
        int width = 1200;
        int height = 800;
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(width, height));
        this.setBounds((d.width-width)/2, (d.height-height)/2, width, height);//窗口的坐标和尺寸，这种方式居中
        // 添加面板
        this.setJMenuBar(jMenu);
        planMenu();
        String well = "<html><p style=\"text-align: center;\"><span style=\"font-size: 34px;\"><strong>欢迎使用数据统计（单机版）</strong></span></p>\n" +
                "<p style=\"text-align: center;font-size: 18px;\">V1.0</p></html>";
        JLabel jLabel = new JLabel(well);
        jLabel.setIcon(new ImageIcon("./统计.png"));
        jLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        jLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        jLabel.setBorder(new EmptyBorder(150, 0, 100, 0));
        jPanel.add(jLabel);
        this.getContentPane().add(jPanel,BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) { }

            @Override
            public void windowClosing(WindowEvent e) {
                viewDetailGui.close();
                outPutDetailGui.close();
                inputDetailGui.close();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                viewDetailGui.close();
                outPutDetailGui.close();
                inputDetailGui.close();
            }
            @Override
            public void windowIconified(WindowEvent e) { }

            @Override
            public void windowDeiconified(WindowEvent e) { }

            @Override
            public void windowActivated(WindowEvent e) { }

            @Override
            public void windowDeactivated(WindowEvent e) { }
        });
    }


    private void planMenu() {

        JMenu input = new JMenu("文件导入（I）");
        JMenu output = new JMenu("数据导出（S）");
        JMenu view = new JMenu("可视化（V）");
        JMenu about = new JMenu("关于（O）");

        input.setFont((new Font("", Font.BOLD, 16)));
        input.setBorder(new EmptyBorder(3, 0, 3, 0));
        input.setMnemonic('I');

        output.setFont((new Font("", Font.BOLD, 16)));
        output.setBorder(new EmptyBorder(3, 0, 3, 0));
        output.setMnemonic('S');

        view.setFont((new Font("", Font.BOLD, 16)));
        view.setBorder(new EmptyBorder(3, 0, 3, 0));
        view.setMnemonic('V');

        about.setFont((new Font("", Font.BOLD, 16)));
        about.setBorder(new EmptyBorder(3, 0, 3, 0));
        about.setMnemonic('O');

        // 一级菜单添加到菜单栏
        jMenu.add(input);
        jMenu.add(output);
        jMenu.add(view);
        jMenu.add(about);

        input.addMenuListener(new MenuListener() {

            @Override
            public void menuSelected(MenuEvent e) {
                jPanel.removeAll();
                JPanel inputPanel = new JPanel();
                inputPanel.setPreferredSize(new Dimension(jPanel.getWidth()-20, jPanel.getHeight()-20));
                inputPanel.setBounds(3,3,jPanel.getWidth()-20,jPanel.getHeight()-20);

                addComponentListener(new ComponentAdapter() {//拖动窗口监听
                    public void componentResized(ComponentEvent e) {
                        inputPanel.setBounds(3,3,jPanel.getWidth()-25,jPanel.getHeight()-20);
                    }
                });

                inputDetailGui.createIndex(inputPanel);
                jPanel.add(inputPanel);
                jPanel.repaint();
                jPanel.revalidate();
                jPanel.setVisible(true);
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        output.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                jPanel.removeAll();
                JPanel outputPanel = new JPanel();
                outputPanel.setPreferredSize(new Dimension(jPanel.getWidth()-20, jPanel.getHeight()-20));
                outputPanel.setBounds(3,3,jPanel.getWidth()-20,jPanel.getHeight()-20);
                addComponentListener(new ComponentAdapter() {//拖动窗口监听
                    public void componentResized(ComponentEvent e) {
                        outputPanel.setBounds(3,3,jPanel.getWidth()-25,jPanel.getHeight()-20);
                    }
                });

                outPutDetailGui.createIndex(outputPanel);
                jPanel.add(outputPanel);
                jPanel.repaint();
                jPanel.revalidate();
                jPanel.setVisible(true);
            }
            @Override
            public void menuDeselected(MenuEvent e) {
            }
            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        view.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JPanel indexPanel = new JPanel();
                JPanel btnPanel = new JPanel();
                JPanel listPanel = new JPanel();
                JPanel viewPanel = new JPanel();
                indexPanel.setPreferredSize(new Dimension(200, jPanel.getHeight()-20));
                viewPanel.setPreferredSize(new Dimension(jPanel.getWidth()-215, jPanel.getHeight()-20));
                btnPanel.setPreferredSize(new Dimension(200, 120));
                listPanel.setPreferredSize(new Dimension(200, jPanel.getHeight()-130));

                addComponentListener(new ComponentAdapter() {//拖动窗口监听
                    public void componentResized(ComponentEvent e) {
                        indexPanel.setBounds(3,3,205,jPanel.getHeight()-20);
                        viewPanel.setBounds(212, 3, jPanel.getWidth()-235, jPanel.getHeight()-20);//(起始点x，起始点y，宽地w，高h)  标签设置宽高不明显
                        btnPanel.setBounds(3, 5, 200,120);//(起始点x，起始点y，宽地w，高h)  标签设置宽高不明显
                        listPanel.setBounds(3,130,200,jPanel.getHeight()-135);
                    }
                });
                jPanel.removeAll();

                viewDetailGui.setNewDBBtn(btnPanel,listPanel,viewPanel);
                indexPanel.add(btnPanel);
                indexPanel.add(listPanel);
                jPanel.add(indexPanel);
                jPanel.add(viewPanel);
                pack();
                jPanel.repaint();
                jPanel.revalidate();
                jPanel.setVisible(true);

            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });

        about.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                jPanel.removeAll();
                JPanel aboutJpanel = new JPanel();
                aboutJpanel.setPreferredSize(new Dimension(jPanel.getWidth()-20, jPanel.getHeight()-20));
                aboutJpanel.setBounds(3,3,jPanel.getWidth()-20,jPanel.getHeight()-20);
                addComponentListener(new ComponentAdapter() {//拖动窗口监听
                    public void componentResized(ComponentEvent e) {
                        aboutJpanel.setBounds(3,3,jPanel.getWidth()-25,jPanel.getHeight()-20);
                    }
                });

                aboutDetailGui.createAbout(aboutJpanel);
                jPanel.add(aboutJpanel);
                jPanel.repaint();
                jPanel.revalidate();
                jPanel.setVisible(true);
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
    }

}
