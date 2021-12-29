package gui;

import db.DetailDB;
import db.ProjectDB;
import db.entity.Detail;
import org.apache.commons.collections.CollectionUtils;
import util.JFControls;
import util.MyButton;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.ResultSet;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ViewDetailGui {

    Map<Integer, Map<String, String>> data = new HashMap<>();
    int select = -10;

    ProjectDB projectDB = new ProjectDB();
    DetailDB detailDB = new DetailDB();

    public void setNewDBBtn(JPanel jPanel,JPanel listPanel,JPanel viewPanel) {
        MyButton but = new MyButton("新建统计项目", 1);
        but.setPreferredSize(new Dimension(180, 50));
        but.setBounds(30, 30, 400, 200);
        jPanel.add(but);
        Font font = new Font("微软雅黑", 0, 18);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        but.addActionListener(e -> {
            String input = JFControls.input(but, "请输入您要统计数据的项目名称入", "新建",
                    18, 400, 70, 18, 350, 30);
            //TODO加入数据库数据
            if (Objects.nonNull(input) && input.length() > 0) {
                try {
                    boolean isSave = projectDB.judgeByName(input);
                    if (isSave){
                        JOptionPane.showMessageDialog(jPanel, "项目名称已经存在，请重新输入项目名称或选择已有项目！", "提示", JOptionPane.WARNING_MESSAGE);
                    }else{
                        boolean b = projectDB.create(input);
                        if (b) {
                            JOptionPane.showMessageDialog(jPanel, "创建项目成功！", "提示", JOptionPane.WARNING_MESSAGE);
                            setDBList(listPanel,viewPanel);
                        }
                    }
                } catch (Exception err) {
                    System.err.println("创建数据库失败，失败原因e：" + err.getMessage());
                    JOptionPane.showMessageDialog(jPanel, "创建项目失败，请退出重试或联系管理员", "提示", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        MyButton rmBut = new MyButton("删  除  项  目", 0);
        rmBut.setPreferredSize(new Dimension(180, 50));
        rmBut.setBounds(30, 260, 400, 200);
        jPanel.add(rmBut);
        Font font1 = new Font("微软雅黑", 0, 18);
        UIManager.put("Button.font", font1);
        UIManager.put("Label.font", font1);
        rmBut.addActionListener(e -> {
            if (select >= 0) {
                if (data.containsKey(select)) {
                    Map<String, String> str = data.get(select);
                    Map.Entry entry = str.entrySet().iterator().next();
                    boolean flag = JFControls.output(rmBut, "<html><body><p align=\"left\">您确定要删除以下项目吗？<br/><p style='color:red'>《" + entry.getValue() + "》</p></p></body></html>", "二次确认导入",
                            18, 400, 70, 18, 350, 30);
                    if (flag) {
                        try {
                            boolean delete = projectDB.delete(entry.getKey().toString());
                            if (delete) {
                                try {
                                    detailDB.deleteByPid(entry.getKey().toString());
                                }catch (Exception err){
                                    System.err.println("删除详细数据异常， e："+err.getMessage());
                                }
                                JOptionPane.showMessageDialog(jPanel, "删除成功", "提示", JOptionPane.WARNING_MESSAGE);
                                setDBList(listPanel,viewPanel);
                            }else{
                                JOptionPane.showMessageDialog(jPanel, "删除失败请稍后再试", "提示", JOptionPane.WARNING_MESSAGE);
                            }


                        } catch (Exception err) {
                            System.err.println("项目删除失败，失败原因e：" + err.getMessage());
                            JOptionPane.showMessageDialog(jPanel, "删除项目失败，请退出重试或联系管理员", "提示", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(jPanel, "请选择要删除的数据", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });
        setDBList(listPanel,viewPanel);
        jPanel.setVisible(true);
    }

    /**
     * 设置列表
     *
     * @param jPanel
     */
    public void setDBList(JPanel jPanel,JPanel viewPanel) {
        JPanel listContainer = new JPanel();
        listContainer.setPreferredSize(new Dimension(jPanel.getWidth(), jPanel.getHeight() - 5));
        try {
            ResultSet list = projectDB.list();
            int i = 0;
            try {
                data.clear();
                while (list.next()) {
                    String id = list.getString("id");
                    String name = list.getString("name");
                    Map<String, String> map = new HashMap<>();
                    map.put(id, name);
                    data.put(i, map);
                    i++;
                }
            } catch (Exception e) {
                System.out.println("数据为空");
            }
        } catch (Exception e) {
            System.err.println("获取数据异常");
        }
        ListSelectionModel listSelectionModel;
        List values = new ArrayList<>();
        data.values().forEach(li -> {
            values.addAll(li.values());
        });
        JList list = new JList(values.toArray());
        list.setPreferredSize(new Dimension(jPanel.getWidth()-30, jPanel.getHeight()-40));
        list.setFont(new Font("微软雅黑", 0, 16));
        listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                select = lsm.getLeadSelectionIndex();
                setDetail(viewPanel);

            }
        });
        //创建对象大小
        JScrollPane listPane = new JScrollPane(list);
        listPane.setPreferredSize(new Dimension(jPanel.getWidth(), jPanel.getHeight() - 22));

        //拖动窗口监听
        jPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                list.setBounds(0, 0, jPanel.getWidth(), jPanel.getHeight()-30);
                listContainer.setBounds(0, 0, jPanel.getWidth(), jPanel.getHeight()-25);
                listPane.setBounds(0, 0, jPanel.getWidth(), jPanel.getHeight() - 22);
            }
        });
        listContainer.add(listPane);
        listContainer.repaint();
        listContainer.revalidate();
        jPanel.removeAll();
        jPanel.add(listContainer);
        jPanel.repaint();
        jPanel.validate();
        setDetail(viewPanel);
    }


    public void setDetail(JPanel jPanel) {

        //定义表格数据数组
        List<Detail> detailList = new ArrayList<>();
        if (data.containsKey(select)){
            Map<String, String> str = data.get(select);
            Map.Entry entry = str.entrySet().iterator().next();
            ResultSet list = detailDB.list(entry.getKey().toString());
            if (Objects.nonNull(list)){
                try {
                    while (list.next()){
                        Detail detail = new Detail();
                        detail.setCol(Integer.valueOf(list.getString("col")))
                                .setCount(Integer.valueOf(list.getString("count")))
                                .setName(list.getString("name"))
                                .setId(list.getString("id"))
                                .setPid(list.getString("pid"));
                        detailList.add(detail);
                    }
                }catch (Exception e){
                    System.out.println("解析显示失败！，失败原因："+e.getMessage());
                }
            }
        }
        JPanel jp = new JPanel();
        jp.removeAll();
        if (CollectionUtils.isNotEmpty(detailList)){
            Map<Integer, List<Detail>> collect = detailList.stream().collect(Collectors.groupingBy(Detail::getCol));
            String[] columnNames = {"排名", "名称", "合计"};

            collect.forEach((k,v) ->{
                List<String[]> result = new ArrayList<>();
                v.sort(Comparator.comparingInt(Detail::getCount).reversed());
                AtomicInteger i = new AtomicInteger(1);
                v.forEach(li ->{
                   String[] temp = new String[3];
                   temp[0] = String.valueOf(i.get());
                   temp[1] = li.getName();
                   temp[2] = String.valueOf(li.getCount());
                   result.add(temp);
                   i.incrementAndGet();
                });
                String[][] strings = result.toArray(result.toArray(new String[0][]));
                JTable table = new JTable( strings, columnNames);
                JTableHeader head = table.getTableHeader(); // 创建表格标题对象
                head.setPreferredSize(new Dimension(head.getWidth(), 35));// 设置表头大小
                head.setFont(new Font("楷体", Font.PLAIN, 18));// 设置表格字体
                table.setRowHeight(22);// 设置表格行宽
                table.setFont(new Font("楷体", Font.PLAIN, 18));
                table.getColumnModel().getColumn(0).setPreferredWidth(70);
                table.getColumnModel().getColumn(1).setPreferredWidth(300);
                table.getColumnModel().getColumn(2).setPreferredWidth(70);
                table.repaint();
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(310, jPanel.getHeight()-40));
                scrollPane.validate();
                jp.add(scrollPane);
            });
            jp.repaint();
            jp.revalidate();
        }
        JScrollPane jScrollPane = new JScrollPane(jp);
        jScrollPane.setPreferredSize(new Dimension(jPanel.getWidth(), jPanel.getHeight()-10));
        jPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                jp.setBounds(0, 0, jPanel.getWidth(), jPanel.getHeight());
                for (Component component : jp.getComponents()) {
                    component.setPreferredSize(new Dimension(310, jPanel.getHeight()-40));
                }
                jScrollPane.setBounds(0, 0, jPanel.getWidth(), jPanel.getHeight());
            }
        });
        jPanel.removeAll();
        jPanel.add(jScrollPane);
        jPanel.repaint();
        jPanel.validate();

    }

    public void close() {
        projectDB.close();
        detailDB.close();
    }

}
