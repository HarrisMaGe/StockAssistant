package UI;

import Analysis.AnalysisMethods;
import Analysis.SortMapByValue;
import StockUtil.GetData;
import Tools.DBHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;


/**
 * Created by 马大侠 on 2017/7/6.
 */

//程序主界面
public class Interface {
     //存储用户选择的股票信息
     private List<List<String>> theSelectCode = new ArrayList<>();
     GetData getDateF = new GetData();
     AnalysisMethods ay = new AnalysisMethods();
     DBHelper db = new DBHelper();
     //存储时间指数
     Map<String,Double> timeIndex = new LinkedHashMap<>();
     String fromdate = "";
     String toDate = "";
     TimeSeriesChart pie = new TimeSeriesChart();
     Frame f = null;
     public Interface() throws UnsupportedEncodingException {
         f=new Frame("Stock");

         f.setSize(715,540);
         f.setLocationRelativeTo(null);
         //f.setLocation(300,200);
         //f.setLayout(null);

         JPanel panel1= new JPanel();
         panel1.setLayout(null);

         //输入股票代码框
         TextField code = new TextField(10);
         code.setBounds(300,10,200,25);
         panel1.add(code);

         //提示
         String str1 = "请输入六位股票代码\0";
         String str = new String(str1.getBytes(),"utf-8");
         Label label = new Label(str.substring(0,9));
         label.setBounds(50,10,150,25);
         panel1.add(label);

         //展示股票板块的复选框的初始化
         List<String> list = new ArrayList<String>();
         list = db.queryCode("select distinct c_name from stocktable");
         int size = list.size();
         String c_name[] = new String[size];
         c_name[0] = new String("选择股票板块".getBytes(),"utf-8");
         for(int i =1;i<size;i++){
             // System.out.println();
              c_name[i] = list.get(i-1);
         }
         //复选框，显示股票板块
         JComboBox c_name_box;
         c_name_box = new JComboBox(c_name);
         c_name_box.setBounds(50,55,200,25);
         panel1.add(c_name_box);

         //空复选框

         JComboBox empty_query_box = new JComboBox();
         empty_query_box.setBounds(300,55,200,25);
         panel1.add(empty_query_box);

         //用户选择股票板块之后，空复选框填上选择的板块的所有股票代码
         c_name_box.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                   int temp = c_name_box.getSelectedIndex();
                   String c_name_select = (String) c_name_box.getItemAt(temp);
                   List<String> code_select_list = db.queryCode("select code from stocktable where c_name = '"+c_name_select+"';");
                   String code_select[] = code_select_list.toArray(new String[code_select_list.size()]);
                   empty_query_box.removeAllItems();
                   for(int i =0;i<code_select.length;i++){
                        empty_query_box.addItem(code_select[i]);
                   }

              }
         });


         //添加股票按钮
         String addStr = new String("添加股票".getBytes(),"utf-8");
         Button addBtn = new Button(addStr);
         addBtn.setBounds(550,10,100,70);
         panel1.add(addBtn);

         //标签
         String selectText = new String("选择要比较的股票".getBytes(),"utf-8");
         Label labels = new Label(selectText);
         labels.setBounds(50,110,125,25);
         panel1.add(labels);

         //用户选择的股票的表格的表头
         String headers[] = {(new String("股票代码".getBytes(),"utf-8")),(new String("股票名称".getBytes(),"utf-8")),(new String("发行时间".getBytes(),"utf-8"))};
         //String dates[][] = {};

         //从数据库中读取用户上次选择的股票
         List<String> hasChooseCode = db.queryCode("select code from haschoosestock;");
         List<List<String>> initHasChooseCode = new ArrayList<>();
         for(int i =0;i<hasChooseCode.size();i++){
              String sql;
              //根据上次用户选择的股票代码取出股票基本信息
              if(hasChooseCode.get(i).substring(0,1).equals("9") || hasChooseCode.get(i).substring(0,1).equals("6")){
                    sql =  sql = "select code,name,min(s.stocktime) " +
                            "from stocktable,"+hasChooseCode.get(i)+"ss s " +
                            "where code = s.stockcode;";
              }else{
                    sql =  sql = "select code,name,min(s.stocktime) " +
                            "from stocktable,"+hasChooseCode.get(i)+"sz s " +
                            "where code = s.stockcode;";
              }

              List<String> tempCodeList  = db.queryCodeInfo(sql);
              theSelectCode.add(tempCodeList);
              initHasChooseCode.add(tempCodeList);
         }

         //展示数据的数组
         String dates[][] = new String [3][];
         String newnumber_1[][] = {};
         int length = initHasChooseCode.size();

         for(int j =0;j<3;j++){
              dates[j] = new String[length];
              for(int i =0;i<length;i++){
                   //存入String数组，用来初始化表格
                   dates[j][i] = initHasChooseCode.get(i).get(j);
              }
         }

         newnumber_1 = new String [dates[0].length][dates.length];
         for(int i =0;i<dates.length;i++){
              //行列颠倒
              for(int j =0;j<dates[0].length;j++){
                   newnumber_1[j][i] = dates[i][j];
              }
         }

         //初始化表格
         DefaultTableModel selectmodel = new DefaultTableModel(newnumber_1,headers){
              public boolean isCellEditable(int row,int column){
                   return false;
              }
         };
         JTable selecttable;
         selecttable = new JTable(selectmodel);
         JScrollPane scrollPane1 = new JScrollPane(selecttable);
         scrollPane1.setBounds(50,150,200,116);
         panel1.add(scrollPane1);

         //添加按钮的事件处理
         addBtn.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                   String sql;
                   String stockCode;
                   if(code.getText().equals("")){//用户输入股票代码优先，若为空，则从复选框中读取
                        int temp = empty_query_box.getSelectedIndex();
                        stockCode = (String) empty_query_box.getItemAt(temp);
                   }else {//
                        stockCode = code.getText().toString();
                   }

                   //获取数据
                   getDateF.getDate(stockCode);

                   if(stockCode.substring(0,1).equals("9") || stockCode.substring(0,1).equals("6")){
                        sql =  sql = "select code,name,min(s.stocktime) " +
                                "from stocktable,"+stockCode+"ss s " +
                                "where code = s.stockcode;";
                   }else{
                        sql =  sql = "select code,name,min(s.stocktime) " +
                                "from stocktable,"+stockCode+"sz s " +
                                "where code = s.stockcode;";
                   }

                   //向表格追加信息
                   List<String> tempCodeList  = db.queryCodeInfo(sql);
                   if(tempCodeList.size()==0){
                        try {
                             JOptionPane.showMessageDialog(null, new String("请检查输入的股票代码是否正确".getBytes(),"utf-8"), new String("错误".getBytes(),"utf-8"),JOptionPane.ERROR_MESSAGE);
                        } catch (UnsupportedEncodingException e1) {
                             e1.printStackTrace();
                        }
                        code.setText("");
                        return;
                   }
                   int tempsize = theSelectCode.size();
                   for(int i =0;i<tempsize;i++){
                        if(theSelectCode.get(i).get(0).equals(tempCodeList.get(0))){
                             code.setText("");
                             return;
                        }
                   }

                   theSelectCode.add(tempCodeList);
                   db.insertIntoHasChooseAndHasStored( "insert into haschoosestock values(?);",tempCodeList.get(0));
                   selectmodel.addRow(tempCodeList.toArray(new String[3]));
                   code.setText("");
              }
         });


         //清除按钮
         String clearStr = new String("清除".getBytes(),"utf-8");
         Button clearBtn = new Button(clearStr);
         clearBtn.setBounds(190,110,60,25);
         panel1.add(clearBtn);

         clearBtn.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                   int size = selectmodel.getRowCount();
                   for(int i =0;i<size;i++){
                        selectmodel.removeRow(0);//删除表格内容
                   }

                   theSelectCode.clear();//用户已选择数组清空
                   db.deleteHasChoose("delete from haschoosestock;");
              }
         });

         String YearStr[] = new String[29];
         YearStr[0] = new String("年份".getBytes(),"utf-8");
         int year = 1990;
         for(int i =1;i<29;i++){
              YearStr[i] = String.valueOf(year);
              year++;
         }
         //起始年份
         JComboBox fromYear = new JComboBox(YearStr);
         fromYear.setBounds(280,110,100,25);
         panel1.add(fromYear);

         String MonthStr[] = new String[13];
         MonthStr[0] =new String("月份".getBytes(),"utf-8");
         int month = 01;
         for(int i =1;i<13;i++){
              MonthStr[i] = String.valueOf(month);
              month++;
         }

         //起始月份
         JComboBox fromMonth = new JComboBox(MonthStr);
         fromMonth.setBounds(280,150,100,25);
         panel1.add(fromMonth);

         Label labelto = new Label("to");
         labelto.setBounds(320,175,25,25);
         panel1.add(labelto);

         //结束年份
         JComboBox toYear = new JComboBox(YearStr);
         toYear.setBounds(280,200,100,25);
         panel1.add(toYear);

         //结束月份
         JComboBox toMonth = new JComboBox(MonthStr);
         toMonth.setBounds(280,240,100,25);
         panel1.add(toMonth);

         Button fenduanBtn = new Button(new String("分段统计".getBytes(),"utf-8"));
         fenduanBtn.setBounds(390,110,80,65);
         panel1.add(fenduanBtn);

         //分段查询事件处理
         fenduanBtn.addActionListener(new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                   //判断用户是否选择有效起始日期和截止日期
                   if(fromYear.getSelectedIndex()==0 || fromMonth.getSelectedIndex()==0
                           ||toYear.getSelectedIndex()==0||toMonth.getSelectedIndex()==0){
                        try {
                             String errorStr =new String("请\0选择正确的起始时间\0".getBytes(),"utf-8");
                             JOptionPane.showMessageDialog(null, errorStr.substring(0,1)+errorStr.substring(3,12), new String("错误".getBytes(),"utf-8"),JOptionPane.ERROR_MESSAGE);
                        } catch (UnsupportedEncodingException e1) {
                             e1.printStackTrace();
                        }
                        return;
                   }

                   //拼接起始日期和截止日期
                   fromdate = fromYear.getItemAt(fromYear.getSelectedIndex())+"-"+fromMonth.getItemAt(fromMonth.getSelectedIndex());
                   toDate = toYear.getItemAt(toYear.getSelectedIndex())+"-"+toMonth.getItemAt(toMonth.getSelectedIndex());
                   //System.out.println(fromdate+"\t"+toDate);
                   String chooseStr = null;
                   try {
                       chooseStr = new String("已确定分段时间\0".getBytes(),"utf-8").substring(0,7);
                       JOptionPane.showMessageDialog(null, chooseStr, new String("错误".getBytes(),"utf-8"),JOptionPane.CLOSED_OPTION);
                   } catch (UnsupportedEncodingException e1) {
                       e1.printStackTrace();
                   }
              }
         });

         Label labelmohu = new Label(new String("模糊指数".getBytes(),"utf-8"));
         labelmohu.setBounds(480,110,50,25);
         panel1.add(labelmohu);


         TextField mohuText = new TextField();
         mohuText.setBounds(540,110,110,25);
         panel1.add(mohuText);



         Label labeltimes = new Label(new String("增长次数".getBytes(),"utf-8"));
         labeltimes.setBounds(480,150,50,25);
         panel1.add(labeltimes);

         TextField timesText = new TextField();
         timesText.setBounds(540,150,110,25);
         panel1.add(timesText);



         Button historyBtn = new Button(new String("历史统计".getBytes(),"utf-8"));
         historyBtn.setBounds(390,200,80,65);
         panel1.add(historyBtn);
         historyBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {

                 fromdate = "1990-01";
                 toDate = "2017-07";
                 try {
                     String chooseStr = new String("已确定统计时间\0".getBytes(),"utf-8").substring(0,7);
                     JOptionPane.showMessageDialog(null, chooseStr, new String("错误".getBytes(),"utf-8"),JOptionPane.CLOSED_OPTION);
                 } catch (UnsupportedEncodingException e1) {
                     e1.printStackTrace();
                 }
             }
         });

         Button timeBtn = new Button(new String("时间指数".getBytes(),"utf-8"));
         timeBtn.setBounds(480,200,80,65);
         panel1.add(timeBtn);
         timeBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 if(fromdate.equals("")|| toDate.equals("")){
                     String errorStr = null;
                     try {
                         errorStr = new String("日期选择出错".getBytes(),"utf-8");
                         JOptionPane.showMessageDialog(null, errorStr, new String("错误".getBytes(),"utf-8"),JOptionPane.ERROR_MESSAGE);
                     } catch (UnsupportedEncodingException e1) {
                         e1.printStackTrace();
                     }
                     return;

                 }
                 int size = theSelectCode.size();

                 //计算时间指数
                 for(int i =0;i<size;i++){
                     String sql;
                     String currentPriceSql;
                     if(theSelectCode.get(i).get(0).substring(0,1).equals("9") || theSelectCode.get(i).get(0).substring(0,1).equals("6")){
                         sql = "select low from "+theSelectCode.get(i).get(0)+"ss where stockTime >= '"+fromdate+"' and stockTime <= '"+toDate+"';";
                         currentPriceSql = "select low from "+theSelectCode.get(i).get(0)+"ss order by stockTime desc limit 1;";
                     }else{
                         sql = "select low from "+theSelectCode.get(i).get(0)+"sz where stockTime >= '"+fromdate+"' and stockTime <= '"+toDate+"';";
                         currentPriceSql = "select low from "+theSelectCode.get(i).get(0)+"sz order by stockTime desc limit 1;";
                     }
                     //System.out.println(sql);
                     List<Double> list = new ArrayList<>();
                     list = db.query(sql);
                     double currentPrice = db.queryCurrentPrice(currentPriceSql);
                     double var = ay.timeAnalysis(list,currentPrice);
                     //已 股票代码----时间指数 的键值对形式存储时间指数信息
                     timeIndex.put(theSelectCode.get(i).get(0),var);
                 }
                 try {
                     //拼接用于向用户展示的信息，时间指数取六位小数
                     String output = "";
                     List<Double> theMaxList = new ArrayList<>();
                     for (Map.Entry<String, Double> entry : timeIndex.entrySet()) {
                         output+=entry.getKey();
                         output+=new String("时间指数".getBytes(),"utf-8");
                         output+=":   ";
                         // DecimalFormat df = new DecimalFormat("#.0000");
                         double value = entry.getValue();

                         BigDecimal b   =   new   BigDecimal(value);
                         double   f1   =   b.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
                         theMaxList.add(value);
                         output+=f1;
                         output+="\n";
                         //System.out.println(output);

                     }
                     double max = SortMapByValue.getMaxDoubleValue(theMaxList);

                     String theChoose  = "";
                     Iterator it = timeIndex.entrySet().iterator();
                     while (it.hasNext()) {
                         Map.Entry entry = (Map.Entry) it.next();
                         Object obj = entry.getValue();
                         if (obj != null && obj.equals(max)) {
                             theChoose=(String) entry.getKey();
                         }
                     }


                     output+=new String("推荐：\0".getBytes(),"utf-8").substring(0,3);
                     output+=theChoose;
                     output+="\n";
                     //使用弹出对话框来展示信息
                     JOptionPane.showConfirmDialog(null, output, new String("时间指数".getBytes(),"utf-8"), JOptionPane.CLOSED_OPTION);
                 } catch (UnsupportedEncodingException e1) {
                     e1.printStackTrace();
                 }
             }
         });

         Button timeQueueBtn = new Button(new String("时间序列".getBytes(),"utf-8"));
         timeQueueBtn.setBounds(570,200,80,65);
         panel1.add(timeQueueBtn);

         timeQueueBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 if(fromdate.equals("")|| toDate.equals("")){
                     String errorStr = null;
                     try {
                         errorStr = new String("日期选择出错".getBytes(),"utf-8");
                         JOptionPane.showMessageDialog(null, errorStr, new String("错误".getBytes(),"utf-8"),JOptionPane.ERROR_MESSAGE);
                     } catch (UnsupportedEncodingException e1) {
                         e1.printStackTrace();
                     }
                     return;

                 }

                 if(mohuText.getText().toString().equals("")|| timesText.getText().toString().equals("")){
                     String errorStr = null;
                     try {
                         errorStr = new String("请填写模糊指数和增长次数".getBytes(),"utf-8");
                         JOptionPane.showMessageDialog(null, errorStr, new String("错误".getBytes(),"utf-8"),JOptionPane.ERROR_MESSAGE);
                     } catch (UnsupportedEncodingException e1) {
                         e1.printStackTrace();
                     }
                     return;
                 }
                 double fuzzyVar = Double.valueOf(mohuText.getText().toString());
                 if(fuzzyVar>=1 || fuzzyVar <=0){
                     try {
                         JOptionPane.showConfirmDialog(null, new String("模糊指数输入有误".getBytes(),"utf-8"), new String("时间指数".getBytes(),"utf-8"), JOptionPane.CLOSED_OPTION);
                     } catch (UnsupportedEncodingException e1) {
                         e1.printStackTrace();
                     }
                 }

                 int times = Integer.valueOf(timesText.getText().toString());
                 int size = theSelectCode.size();

                 //计算时间序列
                 List<int[]> timeList = new ArrayList<>();
                 //String output = "";
                 List<Double> confidenceList = new ArrayList<>();
                 List<Double> supportList = new ArrayList<>();
                 for(int i =0;i<size;i++){
                     String sql;
                     if(theSelectCode.get(i).get(0).substring(0,1).equals("9") || theSelectCode.get(i).get(0).substring(0,1).equals("6")){
                         sql = "select avg(low) from "+theSelectCode.get(i).get(0)+"ss where stockTime >= '"+fromdate+"' and stockTime <= '"+toDate+"' group by year(stockTime),date_format(stockTime,'%Y-%m');";
                     }else{
                         sql = "select avg(low) from "+theSelectCode.get(i).get(0)+"sz where stockTime >= '"+fromdate+"' and stockTime <= '"+toDate+"' group by year(stockTime),date_format(stockTime,'%Y-%m');";
                     }
                     //System.out.println(sql);
                     List<Double> list = new ArrayList<>();
                     list = db.query(sql);
                     //已 股票代码----时间指数 的键值对形式存储时间指数信息
                     int[] result = ay.timeSeries(list,times,fuzzyVar);
                     int[] result2 = ay.timeSeries(list,times+1,fuzzyVar);
                     int max = ay.getMaxSeries(list,fuzzyVar);
                     int theResult[] = new int[3];
                     double Confidence = (double) (result[0] - result2[0])/result[0];
                     double Support = (double)(result[0] - result2[0])/list.size();
                     //double value = entry.getValue();
                     BigDecimal b   =   new   BigDecimal(Confidence);
                     double   f1   =   b.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
                     confidenceList.add(f1);
                     BigDecimal c   =   new   BigDecimal(Support);
                     double   f2   =   c.setScale(6,   BigDecimal.ROUND_HALF_UP).doubleValue();
                     supportList.add(f2);
//                     output+=theSelectCode.get(i).get(0);
//                     try {
//                         output+=new String("置信度为：\0".getBytes(),"utf-8").substring(0,5);
//                         output+=f1;
//                         output+="\n";
//                         output+=new String("\t支持度为:\0".getBytes(),"utf-8").substring(0,7);
//                         output+=f2;
//                         output+="\n";
//                         theResult[0] = result[0];
//                         theResult[1] = result2[0];
//                         theResult[2] = max;
//                         timeList.add(theResult);
//
//
//                     } catch (UnsupportedEncodingException e1) {
//                         e1.printStackTrace();
//                     }

                 }

                 otherFace(theSelectCode,confidenceList,supportList);
//                 try {
//                     //
//                     JOptionPane.showConfirmDialog(null, output, new String("时间序列".getBytes(),"utf-8"), JOptionPane.CLOSED_OPTION);
//                 } catch (UnsupportedEncodingException e1) {
//                     e1.printStackTrace();
//                 }
             }
         });

         //展示所有股票信息的表格的初始化
         String celldata[][] = new String [3][];
         List<List<String>> listinfo = AnalysisMethods.listAllStockInfo();
         List<String> a1 = listinfo.get(0);
         List<String> a2 = listinfo.get(1);
         List<String> a3 = listinfo.get(2);
         celldata[0] = a1.toArray(new String[a1.size()]);
         celldata[1] = a2.toArray(new String[a2.size()]);
         celldata[2] = a3.toArray(new String[a3.size()]);
         String newnumber[][] = new String [celldata[0].length][celldata.length];
         for(int i =0;i<celldata.length;i++){
              for(int j =0;j<celldata[0].length;j++){
                   newnumber[j][i] = celldata[i][j];
              }
         }

         String headers_1[] = {(new String("股票代码".getBytes(),"utf-8")),(new String("股票名称".getBytes(),"utf-8")),(new String("股票板块".getBytes(),"utf-8"))};
         DefaultTableModel model = new DefaultTableModel(newnumber,headers_1){
              public boolean isCellEditable(int row,int column){
                   return false;
              }
         };
         JTable table1;
         table1 = new JTable(model);
         JScrollPane scrollPane = new JScrollPane(table1);
         scrollPane.setBounds(50,280,420,205);
         panel1.add(scrollPane);

         Button deleteBtn = new Button(new String("删除股票".getBytes(),"utf-8"));
         deleteBtn.setBounds(480,280,170,65);
         panel1.add(deleteBtn);
         deleteBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 int index = selecttable.getSelectedRow();
                 if(index==-1){
                     try {
                         String str = new String("请在已\0选择列表中\0选择要删除的股票".getBytes(),"utf-8");
                         JOptionPane.showConfirmDialog(null,str.substring(0,2)+str.substring(5,9)+str.substring(12,20) , new String("提示".getBytes(),"utf-8"), JOptionPane.CLOSED_OPTION);
                     } catch (UnsupportedEncodingException e1) {
                         e1.printStackTrace();
                     }
                     return;
                 }
                 String selectCode = selecttable.getValueAt(index,0).toString();

                 selectmodel.removeRow(index);//删除表格内容
                 for(int i =0;i<theSelectCode.size();i++){
                     if(selectCode.equals(theSelectCode.get(i).get(0))){
                         theSelectCode.remove(i);
                         i--;
                     }
                 }
                 db.deleteHasChoose("delete from haschoosestock where code = "+selectCode+";");
                 //System.out.println(index);
             }
         });

         Button addAnotherBtn = new Button(new String("添加比较".getBytes(),"utf-8"));
         addAnotherBtn.setBounds(480,350,170,65);
         panel1.add(addAnotherBtn);
         addAnotherBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                     int index = table1.getSelectedRow();
                     if(index==-1){
                         try {
                             String str = new String("请\0选择列表中要比较的股票\0".getBytes(),"utf-8");
                             JOptionPane.showConfirmDialog(null,str.substring(0,1)+str.substring(3,14) , new String("提示".getBytes(),"utf-8"), JOptionPane.CLOSED_OPTION);
                         } catch (UnsupportedEncodingException e1) {
                             e1.printStackTrace();
                         }
                         return;
                     }
                     String selectCode = table1.getValueAt(index,0).toString();
                     //System.out.println(selectCode);

                     String sql;
                     getDateF.getDate(selectCode);

                     if(selectCode.substring(0,1).equals("9") || selectCode.substring(0,1).equals("6")){
                         sql =  sql = "select code,name,min(s.stocktime) " +
                                 "from stocktable,"+selectCode+"ss s " +
                                 "where code = s.stockcode;";
                     }else{
                         sql =  sql = "select code,name,min(s.stocktime) " +
                                 "from stocktable,"+selectCode+"sz s " +
                                 "where code = s.stockcode;";
                     }

                     //向表格追加信息
                     List<String> tempCodeList  = db.queryCodeInfo(sql);
                     if(tempCodeList.size()==0){
                         try {
                             JOptionPane.showMessageDialog(null, new String("请检查输入的股票代码是否正确".getBytes(),"utf-8"), new String("错误".getBytes(),"utf-8"),JOptionPane.ERROR_MESSAGE);
                         } catch (UnsupportedEncodingException e1) {
                             e1.printStackTrace();
                         }
                         code.setText("");
                         return;
                     }
                     int tempsize = theSelectCode.size();
                     for(int i =0;i<tempsize;i++){
                         if(theSelectCode.get(i).get(0).equals(tempCodeList.get(0))){
                             code.setText("");
                             return;
                         }
                     }

                     theSelectCode.add(tempCodeList);
                     db.insertIntoHasChooseAndHasStored( "insert into haschoosestock values(?);",tempCodeList.get(0));
                     selectmodel.addRow(tempCodeList.toArray(new String[3]));
                     code.setText("");
                 }
         });


         Button seeSeriesBtn = new Button(new String("查看折线图\0".getBytes(),"utf-8").substring(0,5));
         seeSeriesBtn.setBounds(480,420,170,65);
         panel1.add(seeSeriesBtn);
         seeSeriesBtn.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 pie.createChart(theSelectCode,fromdate,toDate,db);
             }
         });
         f.add(panel1);
         f.setVisible(true);
          f.setResizable(false);
         f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent we) {
                   // f.setVisible(false);
                   System.exit(0);
              }
         });
     }

    public void otherFace(List<List<String>> SelectCode,List<Double> confidenceList,List<Double> supportList) {
        String str = "";
         try {
            str =(new String("时间序列".getBytes(),"utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final JFrame oframe = new JFrame(str);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);

        int size = SelectCode.size();
        String celldata[][] = new String [3][size];
        for(int i =0;i<size;i++){
            celldata[0][i] = SelectCode.get(i).get(0);
            celldata[1][i] = confidenceList.get(i).toString();
            celldata[2][i] = supportList.get(i).toString();
        }

        String newnumber[][] = new String [celldata[0].length][celldata.length];
        for(int i =0;i<celldata.length;i++){
            for(int j =0;j<celldata[0].length;j++){
                newnumber[j][i] = celldata[i][j];
            }
        }

        String headers_1[] = new String[0];
        try {
            headers_1 = new String[]{(new String("股票代码".getBytes(),"utf-8")),(new String("置信度\0".getBytes(),"utf-8").substring(0,3)),(new String("支持度\0".getBytes(),"utf-8").substring(0,3))};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DefaultTableModel model = new DefaultTableModel(newnumber,headers_1){
            public boolean isCellEditable(int row,int column){
                return false;
            }
        };
        JTable table1;
        table1 = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table1);
        scrollPane.setBounds(40,30,400,210);
        jPanel.add(scrollPane);

        oframe.setSize(500,300);
        oframe.setLocationRelativeTo(null);
        oframe.setVisible(true);
        oframe.add(jPanel);
    }

     public static void main(String[] args) throws UnsupportedEncodingException {
          Interface first = new Interface();
     }
}
