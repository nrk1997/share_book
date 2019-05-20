package com.example.asus.tushugongxiang;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DBService {
    private Connection conn=null; //打开数据库对象
    private PreparedStatement ps=null;//操作整合sql语句的对象
    private ResultSet rs=null;//查询结果的集合

    //DBService 对象
    public static DBService dbService=null;

    /**
     * 构造方法 私有化
     * */

    private DBService(){

    }

    /**
     * 获取MySQL数据库单例类对象
     * */

    public static DBService getDbService(){
        if(dbService==null){
            dbService=new DBService();
        }
        return dbService;
    }


    /**
     * 获取要发送短信的患者信息    查
     * */

    public List<Map<String,Object>> getData(String[] needDate,String sql0){
        //结果存放集合
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        //MySQL 语句
        String sql=sql0;
        //获取链接数据库对象
        conn= DBOpenHelper.getConn();
        try {
            if(conn!=null&&(!conn.isClosed())){
                ps= (PreparedStatement) conn.prepareStatement(sql);

                if(ps!=null){
                    rs= ps.executeQuery();
                    if(rs!=null){
                        while(rs.next()){
                            Map<String, Object> map = new HashMap<String, Object>();
                            for (int i = 0; i <needDate.length; i++) {
                                Object cols_value = rs.getString(needDate[i]);
                                map.put(needDate[i], cols_value);
                            }
                            list.add(map);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOpenHelper.closeAll(conn,ps,rs);//关闭相关操作
        return list;
    }

    /**
     * 修改数据库中某个对象的状态   改
     * */

    public int updateData(Map<String,Object> map,String[] needDate,String name,String where){
        int result=-1;

        conn= DBOpenHelper.getConn();
        //MySQL 语句
        String sql="update "+name+" set ";//"=? where phone=?";
        for(int i=0;i<needDate.length;i++){
            sql+=(needDate[i]+"=? ");
            if(i!=needDate.length-1){
                sql+=",";
            }
        }
        sql+=("where "+where);
        try {
            boolean closed=conn.isClosed();
            if(conn!=null&&(!closed)){
                ps= (PreparedStatement) conn.prepareStatement(sql);
                for(int i=0;i<needDate.length;i++){
                    ps.setString(i+1,map.get(needDate[i]).toString());
                }
                result=ps.executeUpdate();//返回1 执行成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBOpenHelper.closeAll(conn,ps);//关闭相关操作
        return result;
    }

    /**
     * 批量向数据库插入数据   增
     * */

    public int insertUserData(List<Map<String,Object>> list,String[] needDate,String name){
        int result=-1;
        if((list!=null)&&(list.size()>0)){
            //获取链接数据库对象
            conn= DBOpenHelper.getConn();
            //MySQL 语句
            String sqlEnd=") VALUES (";
            String sql="INSERT INTO "+name+" (";
            for(int i=0;i<needDate.length;i++){
                sql+=needDate[i];
                sqlEnd+="?";
                if(i!=needDate.length-1){
                    sql+=",";
                    sqlEnd+=",";
                }
            }
            sql+=(sqlEnd+")");
            try {
                boolean closed=conn.isClosed();
                if((conn!=null)&&(!closed)){
                    for(Map<String,Object> map:list){
                        ps= (PreparedStatement) conn.prepareStatement(sql);
                        for(int i=0;i<needDate.length;i++){
                            ps.setString(i+1,(String)map.get(needDate[i]));
                        }
                        result=ps.executeUpdate();//返回1 执行成功
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DBOpenHelper.closeAll(conn,ps);//关闭相关操作
        return result;
    }


    /**
     * 删除数据  删
     * */

    public int delData(String name,String idString,String id){
        int result=-1;
        //获取链接数据库对象
        conn= DBOpenHelper.getConn();
        //MySQL 语句
        String sql="delete from "+name+" where "+idString+"=?";
        try {
            boolean closed=conn.isClosed();
            if((conn!=null)&&(!closed)){
                ps= (PreparedStatement) conn.prepareStatement(sql);
                ps.setString(1, id);
                result=ps.executeUpdate();//返回1 执行成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DBOpenHelper.closeAll(conn,ps);//关闭相关操作
        return result;
    }

}