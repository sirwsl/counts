package db;

import db.entity.Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class ProjectDB {
    /**
     * 以嵌入式(本地)连接方式连接H2数据库
     */
    private static final String JDBC_URL = "jdbc:h2:file:./target/project";
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

    //创建项目
    public boolean create(String name) throws Exception{
        statement.execute("CREATE TABLE if not exists `project` (" +
                "  `id` int(10) NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(255) NOT NULL," +
                "  PRIMARY KEY (`id`)" +
                ")");
        int i = statement.executeUpdate("INSERT INTO project VALUES( null ,'" + name + "')");
        return i > 0;
    }

    public boolean delete(String id) throws Exception{
        return statement.executeUpdate("DELETE FROM project WHERE id = " + id) > 0;
    }

    public ResultSet list() throws Exception{
        return statement.executeQuery("select * from project");
    }

    /**
     * 判断名称是否存在
     * @param name
     * @return : 存在返回true
     */
    public boolean judgeByName(String name){
        try {
            ResultSet resultSet = statement.executeQuery("select * from project where name = '" + name+"'");
            if (Objects.isNull(resultSet)){
                return false;
            }else{
                return resultSet.next();
            }
        }catch (Exception e){
            System.out.println("判断项目名称是否存在，出现异常e："+e.getMessage());
        }
        return false;
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
