package top.vncnliu.event.server.mash.sample.store.sql;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.*;

/**
 * User: vncnliu
 * Date: 2018/8/31
 * Description:
 */
public class TastTwoDB {

    // JDBC 驱动名及数据库 URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://192.168.1.156:4000";

    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "";

    @Test
    void main() {
        Connection conn = null;
        Statement stmt = null;
        try{
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            conn.setAutoCommit(false);
            // 执行查询
            System.out.println("实例化Statement对象...");
            stmt = conn.createStatement();
            String sql1 = "insert into event_mash_inventory.t_symbol (id,name) values (1,'苹果')";
            stmt.execute(sql1);
            String sql2 = "insert into event_mash_order.t_order (id,name) values (3,'买苹果3')";
            stmt.execute(sql2);
            conn.commit();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
            try{
                if(conn!=null) conn.rollback();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }finally{
            // 关闭资源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什么都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
