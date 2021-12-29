package gui;

import db.DetailDB;
import db.ProjectDB;
import db.entity.Detail;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.MyButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class OutPutDetailGui {

    ProjectDB projectDB = new ProjectDB();
    DetailDB detailDB = new DetailDB();

    Map<String, String> data = new HashMap<>();
    String option = null;

    public void createIndex(JPanel jPanel) {
        JPanel readMe = new JPanel();
        JPanel comp = new JPanel();

        readMe.setPreferredSize(new Dimension(jPanel.getWidth() - 10, jPanel.getHeight() - 300));
        readMe.setBounds(3, 3, jPanel.getWidth() - 10, jPanel.getHeight() - 300);
        comp.setPreferredSize(new Dimension(jPanel.getWidth() - 10, 280));
        comp.setBounds(3, readMe.getHeight() + 9, jPanel.getWidth() - 10, 280);

        //添加提示
        JTextArea jTextArea = new JTextArea(5,60);
        String text =
                "欢迎使用此数据统计程序（单机版），使用之前请注意阅读以下注意事项，以确保您能够正常导出数据\n" +
                        "1、导出前请先选择要导出的项目，且确保要导出的项目已有统计好的数据。\n" +
                        "2、点击导出时，会让您选择保存的目录，请记住您选择的保存目录，防止导出后无法找到文件。\n" +
                        "3、选择项目导出后会形成一个Excel文件，若该项目存在多个统计项，则会创建多个sheet，请打开Excel后点击左下角页面进行查看。\n" +
                        "4、统计项目若存在误差，请多次进行数据解析进行核验。\n" +
                        "5、导出Excel采用\"项目名\"+统计+时间构成，例如项目为 \"姓名\"则Excel文件名为：姓名统计(202112311200).xlsx，非必要不建议修改。";
        jTextArea.setText(text);
        jTextArea.setLineWrap(true);
        jTextArea.setFont(new Font("微软雅黑", 0, 20));
        jTextArea.setEditable(false);
        jTextArea.setBackground (null);
        jTextArea.setDisabledTextColor(Color.BLACK);
        JLabel textLable = new JLabel("<html><p style=\"text-align: center;\"><strong><span style=\"font-size: 18px;\">导出说明</span></strong></p></html>");
        readMe.add(textLable);
        readMe.add(jTextArea);
        //数据获取
        try {
            ResultSet list = projectDB.list();
            data.clear();
            while (list.next()) {
                String id = list.getString("id");
                String name = list.getString("name");
                data.put(name, id);
            }
        } catch (Exception e) {
            System.out.println("数据为空");
        }

        //数据获取
        try {
            ResultSet list = projectDB.list();
            data.clear();
            while (list.next()) {
                String id = list.getString("id");
                String name = list.getString("name");
                data.put(name, id);
            }
        } catch (Exception e) {
            System.out.println("数据为空");
        }

        //创建下拉框
        JLabel label = new JLabel("项目选择：");    //创建标签
        label.setFont(new Font("微软雅黑", Font.BOLD, 22));
        Object[] objects = data.keySet().toArray();
        JComboBox cmb = new JComboBox(objects); //创建JComboBox
        cmb.setSelectedIndex(-1);
        cmb.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        cmb.setPreferredSize(new Dimension(500, 40));
        // 添加条目选中状态改变的监听器
        cmb.addItemListener(e -> {
            // 只处理选中的状态
            if (e.getStateChange() == ItemEvent.SELECTED) {
                option = Objects.requireNonNull(cmb.getSelectedItem()).toString();
            }
        });
        label.setBorder(new EmptyBorder(20, 0, 20, 0));
        //创建文件导入按钮
        MyButton openBtn = new MyButton("开        始        导        出", 0);
        openBtn.setPreferredSize(new Dimension(630, 50));

        openBtn.addActionListener(e -> {
            if (Objects.nonNull(option) && option.length() > 0) {
                DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime now = LocalDateTime.now();
                String format = dtf2.format(now);
                String path = "D:\\" + option + "统计(" + format + ").xlsx";
                File file = new File(path);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(file);
                //后缀名过滤器
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Excel(*.xlsx)", "xlsx");
                chooser.setFileFilter(filter);
                //下面的方法将阻塞，直到【用户按下保存按钮且“文件名”文本框不为空】或【用户按下取消按钮】
                int options = chooser.showSaveDialog(null);
                if (options == JFileChooser.APPROVE_OPTION) {    //假如用户选择了保存
                    File files = chooser.getSelectedFile();
                    String fname = chooser.getName(files);    //从文件名输入框中获取文件名
                    if (fname.indexOf(".xlsx") == -1) {
                        file = new File(chooser.getCurrentDirectory(), fname + ".xlsx");
                    } else {
                        file = new File(chooser.getCurrentDirectory(), fname);
                    }
                }

                //获取需要写入的数据
                ResultSet list = detailDB.list(data.get(option));
                List<Detail> detailList = new ArrayList<>();
                if (Objects.nonNull(list)) {
                    try {
                        while (list.next()) {
                            Detail detail = new Detail();
                            detail.setCol(Integer.valueOf(list.getString("col")))
                                    .setCount(Integer.valueOf(list.getString("count")))
                                    .setName(list.getString("name"))
                                    .setId(list.getString("id"))
                                    .setPid(list.getString("pid"));
                            detailList.add(detail);
                        }
                    } catch (Exception err) {
                        System.out.println("解析显示失败！，失败原因：" + err.getMessage());
                    }
                }

                if (CollectionUtils.isNotEmpty(detailList)) {
                    //创建excel工作簿
                    XSSFWorkbook workbook = new XSSFWorkbook();
                    Map<Integer, List<Detail>> collect = detailList.stream().collect(Collectors.groupingBy(Detail::getCol));
                    //插入第一行的表头

                    AtomicInteger col = new AtomicInteger();
                    String[] title = {"排名", "名称", "总数"};
                    XSSFSheet sheet1 = workbook.createSheet();
                    collect.values().forEach(li -> {
                        //创建工作表sheet
                        workbook.createSheet();
                        workbook.setSheetName(col.get(), "第" + (col.get()+1)+"项");
                        XSSFSheet sheet = workbook.getSheetAt(col.get());
                        XSSFRow row = sheet.createRow(0);
                        XSSFCell cell = null;
                        // 定义表头
                        for (int i = 0; i < title.length; i++) {
                            cell = row.createCell(i);
                            cell.setCellValue(title[i]);
                        }
                        AtomicInteger index = new AtomicInteger(1);
                        li.sort(Comparator.comparingInt(Detail::getCount).reversed());
                        li.forEach(o -> {
                            XSSFRow nrow = sheet.createRow(index.get());
                            XSSFCell ncell = nrow.createCell(0);
                            ncell.setCellValue(index.get());
                            ncell = nrow.createCell(1 );
                            ncell.setCellValue(o.getName());
                            ncell = nrow.createCell(2);
                            ncell.setCellValue(o.getCount());
                            index.getAndIncrement();
                        });
                        col.incrementAndGet();
                    });

                    try {
                        file.getParentFile().mkdir();
                        file.createNewFile();
                        FileOutputStream stream = new FileOutputStream(file);
                        workbook.write(stream);
                        stream.close();
                        JOptionPane.showMessageDialog(jPanel, "数据保存成功", "提示", JOptionPane.WARNING_MESSAGE);
                    } catch (IOException err) {
                        err.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(jPanel, "您所选择的项目暂无数据", "警告", JOptionPane.WARNING_MESSAGE);

                }
            } else {
                JOptionPane.showMessageDialog(jPanel, "请选择您要保存的项目", "警告", JOptionPane.WARNING_MESSAGE);
            }


        });

        //拖动窗口监听
        jPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                readMe.setBounds(3, 3, jPanel.getWidth() - 10, jPanel.getHeight() - 300);
                comp.setBounds(3, readMe.getHeight() + 9, jPanel.getWidth() - 10, 280);
            }
        });
        comp.add(label);
        comp.add(cmb);
        comp.add(openBtn);
        jPanel.add(readMe);
        jPanel.add(comp);
    }

    public void close() {
        projectDB.close();
        detailDB.close();
    }
}
