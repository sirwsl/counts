package util;

import jdk.nashorn.internal.objects.Global;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.prefs.Preferences;

public class JFControls {
    public static void message(JFrame jFrame, String message) {
        JOptionPane.showMessageDialog(jFrame, message);
    }

    public static void messageWorn(JFrame jFrame, String message) {
        JOptionPane.showMessageDialog(jFrame, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }

    public static String input(JButton button, String message, String title, Integer msgSize,
                               Integer width, Integer height, Integer fontSize, Integer w, Integer h) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(width, height));
        JPanel labels = new JPanel(new BorderLayout());
        Font font = new Font("宋体", 0, msgSize);
        UIManager.put("Label.font", font);
        labels.add(new JLabel(message));
        p.add(labels, BorderLayout.WEST);
        JPanel controls = new JPanel(new BorderLayout());
        JTextField name = new JTextField();
        name.setPreferredSize(new Dimension(w, h));
        name.setFont(new Font("宋体", Font.BOLD, fontSize));
        controls.add(name, BorderLayout.NORTH);
        p.add(controls, BorderLayout.CENTER);
        int index = JOptionPane.showConfirmDialog(button, p, title, JOptionPane.OK_CANCEL_OPTION);
        if (index == 0) { // 0 确定  2取消
            return name.getText();
        }
        return "";
    }

    public static boolean output(Component button, String message, String title, Integer msgSize,
                                 Integer width, Integer height, Integer fontSize, Integer w, Integer h) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(width, height));
        JPanel labels = new JPanel(new BorderLayout());
        Font font = new Font("宋体", 0, msgSize);
        UIManager.put("Label.font", font);
        labels.add(new JLabel(message));
        p.add(labels);
        JPanel controls = new JPanel(new BorderLayout());
        p.add(controls, BorderLayout.CENTER);
        int index = JOptionPane.showConfirmDialog(button, p, title, JOptionPane.OK_CANCEL_OPTION);
        if (index == 0) { // 0 确定  2取消
            return true;
        }
        return false;
    }

    public void windowClosing(JFrame f, WindowEvent e) {
        int a = JOptionPane.showConfirmDialog(f, "是否退出系统?");
        if (a == JOptionPane.YES_OPTION) {
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }


    /*
     * 打开文件
     */
    public static File[] showFileOpenDialog(Component parent) {
        // 创建一个默认的文件选取器
        JFileChooser fileChooser = null;
        Preferences pref = Preferences.userRoot().node("/com/register");
        String lastPath = pref.get("lastPath", "");
        if (!lastPath.equals(""))
            fileChooser = new JFileChooser(lastPath);
        else
            fileChooser = new JFileChooser();

        // 设置文件选择的模式（只选文件、只选文件夹、文件和文件均可选）
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // 设置是否允许多选
        fileChooser.setMultiSelectionEnabled(true);
        // 添加可用的文件过滤器（FileNameExtensionFilter 的第一个参数是描述, 后面是需要过滤的文件扩展名 可变参数）
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("excel(*.xlsx)", "xlsx"));
        // 设置默认使用的文件过滤器
        fileChooser.setFileFilter(new FileNameExtensionFilter("excel(*.xlsx)", "xlsx"));
        // 打开文件选择框（线程将被阻塞, 直到选择框被关闭）

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            // 如果点击了"确定", 则获取选择的文件路径
            File[] files = fileChooser.getSelectedFiles();
            return files;
        }
        return null;
    }

    public static void saveFile(File file) {
        //弹出文件选择框
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(file);
        //后缀名过滤器
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Excel(*.xlsx)", "xlsx");
        chooser.setFileFilter(filter);
        //下面的方法将阻塞，直到【用户按下保存按钮且“文件名”文本框不为空】或【用户按下取消按钮】
        int option = chooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
            File files = chooser.getSelectedFile();
            String fname = chooser.getName(files);    //从文件名输入框中获取文件名
            //假如用户填写的文件名不带我们制定的后缀名，那么我们给它添上后缀
            if (fname.indexOf(".xlsx") == -1) {
                System.out.println(chooser.getCurrentDirectory());
                files = new File(chooser.getCurrentDirectory(), fname + ".con");
                System.out.println(files.getName());
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //写文件操作……
            fos.close();

        } catch (IOException e) {
            System.err.println("IO异常");
            e.printStackTrace();
        }
    }

}


