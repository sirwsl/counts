package db;

import db.entity.Detail;
import org.apache.commons.collections.CollectionUtils;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DetailDB {
    /**
     * 以嵌入式(本地)连接方式连接H2数据库
     */
    private static final String JDBC_URL = "jdbc:h2:file:./target/detail";
    private static final String DRIVER_CLASS = "org.h2.Driver";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection conn;
    Statement statement;

    {
        try {
            Class.forName(DRIVER_CLASS);
            conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            statement = conn.createStatement();
            System.out.println("数据库连接创建成功");
        }catch (Exception e){
            System.err.println("创建数据库连接失败");
        }
    }

    public  boolean saveOrInsertBatchById(List<Detail> detail) throws Exception {
        if (CollectionUtils.isEmpty(detail)){
            return false;
        }
        List<Detail> saveOrUpdate = detail.stream().filter(li -> Objects.nonNull(li.getName())&&li.getName().length() >0).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(saveOrUpdate)){
            return false;
        }
        statement.execute("CREATE TABLE if not exists `detail` (" +
                "  `id` int(10) NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(255) NOT NULL," +
                "  `pid` int(10) DEFAULT NULL," +
                "  `count` int(10) DEFAULT NULL," +
                "  `col` int(10) DEFAULT NULL," +
                "  PRIMARY KEY (`id`))");
        List<Detail> insert = saveOrUpdate.stream().filter(li -> Objects.isNull(li.getId())).collect(Collectors.toList());
        //新增
        StringBuilder sb = new StringBuilder();
        if (CollectionUtils.isNotEmpty(insert)){
            insert.forEach(li -> {
                sb.setLength(0);
                sb.append("INSERT INTO detail(name,pid,count,col) VALUES ")
                        .append("('")
                        .append(li.getName()).append("','")
                        .append(li.getPid()).append("','")
                        .append(li.getCount()).append("','")
                        .append(li.getCol()).append("')");
                try {
                    System.out.println(sb.toString());
                    statement.addBatch(sb.toString());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    System.err.println("数据库插入异常"+throwables.getMessage());
                }
            });
            saveOrUpdate.removeAll(insert);
        }

        //更新
        if (CollectionUtils.isNotEmpty(saveOrUpdate)){
            saveOrUpdate.forEach(li->{
                sb.setLength(0);
                sb.append("UPDATE detail set ");
                if (Objects.nonNull(li.getCount())){
                    sb.append("count = '").append(li.getCount()).append("',");
                }
                if (Objects.nonNull(li.getPid())){
                    sb.append("pid = '").append(li.getPid()).append("',");
                }
                if (Objects.nonNull(li.getCol())){
                    sb.append("col = '").append(li.getCol()).append("',");
                }
                if (Objects.nonNull(li.getName())){
                    sb.append("name = '").append(li.getName()).append("'");
                }
                sb.append(" where id = '").append(li.getId()).append("'");
                try {
                    statement.addBatch(sb.toString());
                    System.out.println(sb.toString());
                } catch (SQLException throwables) {
                    System.err.println("数据库更新异常"+throwables.getMessage());
                }
            });
        }
        statement.executeBatch();
        return true;
    }

    public boolean updateCountById(Map<Integer, Integer> map) throws Exception {
        StringBuilder sb = new StringBuilder();
        map.forEach((k,v)->{
            sb.append("UPDATE detail set count = ").append(v).append(" where id = ").append(k).append(",");
        });
        sb.delete(sb.length()-2,sb.length()-1);
        int i = statement.executeUpdate(sb.toString());
        return i > 0;
    }

    public boolean delete(String id) throws Exception{
        return statement.executeUpdate("DELETE FROM detail WHERE id =" + id) > 0;
    }
    public boolean deleteByPid(String pid) throws Exception{
        return statement.executeUpdate("DELETE FROM detail WHERE pid =" + pid) > 0;
    }

    /**
     * 根据项目id获取对应列表
     * @param pid
     * @return
     */
    public ResultSet list(String pid) {
        try {
            return statement.executeQuery("select * from detail where pid = " + pid);
        }catch (Exception e){
            System.err.println("数据获取异常");
        }
        return null;
    }


    public void close(){
        try {
            statement.close();
            conn.close();
            System.out.println("数据库连接已关闭");
        }catch (Exception e){
            System.err.println("数据库连接关闭失败！");
        }

    }

}
