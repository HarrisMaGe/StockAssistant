package Tools;

import StockUtil.StockData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 马大侠 on 2017/7/6.
 */

//数据库操作类
public class DBHelper {
    //String
    String driver = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://127.0.0.1:3306/stock?useSSL=false";
    String user = "root";
    String password = "19950228MAGE";
    Connection conn = null;

    public DBHelper(){
        try{
            Class.forName(driver);
            this.conn = DriverManager.getConnection(this.url,this.user,this.password);
            if (!conn.isClosed()){
            }else{
                return;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createTable(String tableName){
        try{
            String sql = "create table if not exists "+ tableName +" (" +
                    "StockCode varchar(64) not null," +
                    "StockTime Date not null," +
                    "Open Double not null," +
                    "Close Double not null," +
                    "High Double not null," +
                    "Low Double not null," +
                    "Volume Double not null," +
                    "UpAndDown Double not null," +
                    "Type int not null," +
                    "primary key(StockTime)," +
                    "foreign key(StockCode) references stocktable(code) on delete cascade" +
                    ");";
            //System.out.println(sql);
            //System.out.println(sql);
            PreparedStatement pStmp = conn.prepareStatement(sql);
            pStmp.executeUpdate();
            return  true;
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public boolean insertIntoStock(StockData sd,int type){
        String sql = "";
        if(type==0){
            sql = "insert into " + sd.getCode() +"ss values(?,?,?,?,?,?,?,?,?);";
        }else{
            sql = "insert into " + sd.getCode() +"sz values(?,?,?,?,?,?,?,?,?);";
        }
        try{
            PreparedStatement pStmp = conn.prepareStatement(sql);
            pStmp.setString(1,sd.getCode());
            pStmp.setString(2,sd.getDate());
            pStmp.setDouble(3,sd.getOpen());
            pStmp.setDouble(4,sd.getClose());
            pStmp.setDouble(5,sd.getHigh());
            pStmp.setDouble(6,sd.getLow());
            pStmp.setDouble(7,sd.getVolume());
            pStmp.setDouble(8,sd.getUpAndDown());
            pStmp.setInt(9,sd.getType());
            pStmp.executeUpdate();
            return true;
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public List<Double> query(String sql){
        List<Double> list = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                Double low = rs.getDouble(1);
                list.add(low);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<List<String>> queryLowAndDate(String sql){
        List<List<String>>list = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                List<String> theList = new ArrayList<>();
                theList.add(rs.getString(1));
                theList.add(String.valueOf(rs.getDouble(2)));
                //Double low = rs.getDouble(1);
                list.add(theList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<String> queryCode(String sql){
        List<String> list = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                String code = rs.getString(1);
                list.add(code);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> queryCodeInfo(String sql){
        List<String> list = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                String code = rs.getString(1);
                list.add(code);
                String name = rs.getString(2);
                list.add(name);

                String c_name = rs.getString(3);
                list.add(c_name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double queryCurrentPrice(String sql){
        double low =0;
        try{
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                low = rs.getDouble(1);
            }
            return low;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void deleteTable(String tableName){
        try{
            String sql = "drop table "+tableName+";";
            PreparedStatement pStmp = conn.prepareStatement(sql);
            pStmp.executeUpdate();
        } catch (SQLException e) {
            return;
        }

    }

    public List<List<String>> selectStockTable(String sql){
       // String sql = "select code,name,c_name from stocktable;";
        List<List<String>> list = new ArrayList<>();
        List<String> code = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> c_name = new ArrayList<>();
        try{
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                code.add(rs.getString(1));
                name.add(rs.getString(2));
                c_name.add(rs.getString(3));
            }
            list.add(code);
            list.add(name);
            list.add(c_name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertIntoHasChooseAndHasStored(String sql,String code){
        //String sql = "insert into haschoosestock values(?);";
        try{
            PreparedStatement pStmp = conn.prepareStatement(sql);
            pStmp.setString(1,code);
            pStmp.executeUpdate();
            return true;
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public boolean deleteHasChoose(String sql){
        try{
            PreparedStatement pStmp = conn.prepareStatement(sql);
            pStmp.executeUpdate();
            return true;
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }
}
