package gui;

import db.DetailDB;
import db.ProjectDB;

import db.entity.Detail;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.JFControls;
import util.MyButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InputDetailGui {

    ProjectDB projectDB = new ProjectDB();
    DetailDB detailDB = new DetailDB();
     Map<String, String> data = new HashMap<>();
    String option = null;
    /**
     * 创建主面板
     * @param jPanel
     */
    public void createIndex(JPanel jPanel) {
        JPanel readMe = new JPanel();
        JPanel comp = new JPanel();
        readMe.setPreferredSize(new Dimension(jPanel.getWidth() - 10, jPanel.getHeight() - 300));
        readMe.setBounds(3, 3, jPanel.getWidth() - 10, jPanel.getHeight() - 300);


        comp.setPreferredSize(new Dimension(jPanel.getWidth() - 10, 280));
        comp.setBounds(3, readMe.getHeight() + 9, jPanel.getWidth() - 10, 280);

        //添加提示说明
        JTextArea jTextArea = new JTextArea(5,60);
        String text =
                "欢迎使用此数据统计程序（单机版），使用之前请注意阅读以下注意事项，防止导致程序崩溃、自动退出、统计数据错乱、数据统计错误等情况。\n" +
                "1、该程序对Excel文件数据按列进行统计，需要注意每一列作为一个统计项目。例如“张三”与“张   三”视为同一数据（自动忽略空格）\n" +
                "2、该程序可对多个Excel文件进行统计，但需要保证每列数据属于相同设定。例如：一共由3个excel文件A、B、C，解析结果统计为：统计第一项=A的第一列+B的第一列+C的第一列，以此类推\n" +
                "3、每个Excel文件仅对第一个sheet页面进行解析，其余数据会被忽略。\n" +
                "4、请在解析前将数据进行整理，按要统计的项目按列进行排列。\n" +
                "5、在解析数据前请确保每一列数据中没有空的数据，需保证同一列数据连续不中断。\n" +
                "6、在解析前请先选择需要解析的项目。若没有项目，请先创建项目。";
        jTextArea.setText(text);
        jTextArea.setLineWrap(true);
        jTextArea.setFont(new Font("微软雅黑", 0, 20));
        jTextArea.setEditable(false);
        jTextArea.setBackground (null);
        jTextArea.setDisabledTextColor(Color.BLACK);
        JLabel textLable = new JLabel("<html><p style=\"text-align: center;\"><strong><span style=\"font-size: 18px;\">使用说明</span></strong></p></html>");
        readMe.add(textLable);
        readMe.add(jTextArea);
        //获取数据
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
        JLabel label=new JLabel("项目选择：");    //创建标签
        label.setFont(new Font("微软雅黑", Font.BOLD, 22));
        Object[] objects = data.keySet().toArray();
        JComboBox cmb=new JComboBox(objects); //创建JComboBox
        cmb.setSelectedIndex(-1);
        cmb.setFont(new Font("微软雅黑",Font.PLAIN,20));
        cmb.setPreferredSize(new Dimension(500, 40));
        // 添加条目选中状态改变的监听器
        cmb.addItemListener(e -> {
            // 只处理选中的状态
            if (e.getStateChange() == ItemEvent.SELECTED) {
                option =  Objects.requireNonNull(cmb.getSelectedItem()).toString();
            }
        });
        label.setBorder(new EmptyBorder(20, 0, 20, 0));
        //创建文件导入按钮
        MyButton openBtn = new MyButton("请 选 择 要 解 析 的 文 件", 0);
        openBtn.setPreferredSize(new Dimension(630, 50));

        openBtn.addActionListener(e -> {
            if(Objects.nonNull(option)&&data.containsKey(option)){
                StringBuilder str = new StringBuilder();
                File[] files = JFControls.showFileOpenDialog(comp);
                int height = 50;
                for (File file : files) {
                    str.append(file.getAbsolutePath()).append("<br/>");
                    height+=20;
                }
                boolean flag = JFControls.output(openBtn, "<html><body><p align=\"left\">您将要导入的文件有：<br/>" + str.toString()+"</p></body></html>", "二次确认导入",
                        18, 400, height, 18, 350, 30);
                if (flag){
                    String id = data.get(option);
                    int count = 0;
                    AtomicInteger sum = new AtomicInteger();
                    for (File file : files) {
                        List<List<String>> lists = dataAnailysis(file);
                        if(CollectionUtils.isNotEmpty(lists)){
                            //输入数据先进行统计
                            Map<Integer,Map<String,Detail>> allData= new HashMap();

                            AtomicInteger i = new AtomicInteger();
                            lists.forEach(li ->{
                                Map<String,Detail> countNum = new HashMap<>(64);
                                li.forEach( o ->{
                                    if (o.length() > 0){
                                        sum.getAndIncrement();
                                        o = o.replaceAll(" ", "");
                                        if (countNum.containsKey(o)) {
                                            Detail detail = countNum.get(o);
                                            countNum.put(o, detail.setCount(detail.getCount()+1));
                                        }else{
                                            Detail detail = new Detail();
                                            detail.setCount(1).setCol(i.get()).setName(o).setPid(id);
                                            countNum.put(o,detail);
                                        }
                                    }
                                });
                                allData.put(i.get(),countNum);
                                i.getAndIncrement();
                            });
                            //获取数据库数据
                            ResultSet list = detailDB.list(id);

                            if (Objects.nonNull(list)){
                                //如果数据库数据不为空
                               try {
                                   Map<String, Detail> countAll = new HashMap<>();
                                   List<Detail> listTemp = new ArrayList<>();
                                   while (list.next()){
                                       Detail detail = new Detail();
                                       detail.setCol(Integer.valueOf(list.getString("col")))
                                               .setCount(Integer.valueOf(list.getString("count")))
                                               .setName(list.getString("name"))
                                               .setId(list.getString("id"))
                                               .setPid(list.getString("pid"));
                                       listTemp.add(detail);
                                   }
                                   Map<Integer, List<Detail>> collect = listTemp.stream().collect(Collectors.groupingBy(Detail::getCol));
                                   Map<Integer,Map<String,Detail>> old = new HashMap<>();
                                   AtomicInteger j = new AtomicInteger();
                                   collect.forEach((k,v)->{
                                       Map<String, Detail> collect1 = v.stream().collect(Collectors.toMap(Detail::getName, o1 -> o1, (k1, k2) -> k1));
                                       old.put(j.get(),collect1);
                                       j.incrementAndGet();
                                   });
                                   allData.forEach((k,v)->{
                                       if (old.containsKey(k)) {
                                           Map<String, Detail> temp = old.get(k);
                                           v.forEach((nk,nv)->{
                                               if (temp.containsKey(nk)) {
                                                   nv.setCount(temp.get(nk).getCount()+nv.getCount());
                                                   nv.setId(temp.get(nk).getId());
                                               }
                                           });
                                       }
                                   });
                               }catch (Exception err){
                                   System.err.println("数据解析异常");
                               }
                            }
                            if (!allData.isEmpty()&&CollectionUtils.isNotEmpty(allData.values())){
                                List endAll = new ArrayList();
                                allData.values().forEach(li ->{
                                    if (CollectionUtils.isNotEmpty(li.values())){
                                        endAll.addAll(li.values());
                                    }
                                });
                                try {
                                    boolean b = detailDB.saveOrInsertBatchById(endAll);
                                    if (b){
                                        count++;
                                    }
                                }catch (Exception err){
                                    System.err.println("数据解析失败，没有插入数据库");
                                }
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(jPanel, "数据解析成功："+count+" 个Excel文件，失败："+(files.length-count)+" 个,共解析数据："+sum+"条",
                            "提示", JOptionPane.WARNING_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(jPanel, "导入数据前请先查看所属项目是否存在，并且选择数据所属项目已被选中", "提示", JOptionPane.WARNING_MESSAGE);
            }

        });

        jPanel.addComponentListener(new ComponentAdapter() {//拖动窗口监听
            public void componentResized(ComponentEvent e) {
                readMe.setBounds(3,3,jPanel.getWidth()-10,jPanel.getHeight()-300);
                comp.setBounds(3,readMe.getHeight()+9,jPanel.getWidth()-10,280);
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

    public List<List<String>> dataAnailysis(File file) {
        List<List<String>> result = new ArrayList<>(32);
        Workbook workbook = null;
        try {
            try {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }catch (Exception e){
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
            Sheet sheet = workbook.getSheet("Sheet1");
            int RowCells=sheet.getRow(0).getPhysicalNumberOfCells();
            for(int col = 0; col < RowCells;col++){
                List<String> temp = new ArrayList<>(512);
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow((short) i);
                    if (null == row) {
                        continue;
                    } else {
                        Cell cell = row.getCell((short) col);
                        if (null == cell) {
                            continue;
                        } else {
                            temp.add(cell.getStringCellValue());
                        }
                    }
                }
                result.add(temp);
            }
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
