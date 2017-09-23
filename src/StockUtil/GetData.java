package StockUtil;

import Tools.DBHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.w3c.dom.ls.LSInput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by 马大侠 on 2017/7/6.
 */

//从新浪股票接口获取股票信息

/*雅虎实时获取数据
http://finance.yahoo.com/d/quotes.csv?A=股票名称&B=数据列选项*/

/*雅虎获取历史数据
        http://ichart.yahoo.com/table.csv?A=string&B=int&C=int&D=int&E=int&F=
        int&G=int&H=d&ignore=.csv
        其中 A 代表需要查询的股票的代码，比如说“000001.sz（上
        海浦发银行）”；B 代表需要查询的股票的起始时间的月份；C 代表起始时间的日期；
        D 代表起始时间的年份；E 代表要查询股票结束时间的月份；
        F 代表结束时间的日期；G 代表结束时间的年份，H 代表时间周期。

        参数 H 有 4 个可选值：d/(day)以日为单位；w/(week)以周为单位；m/(month)
        以月为单位;v->(dividends only)以股息为单位。开发者可以通过指定这个参数
        来获取不同格式的数据。

        http://finance.yahoo.com/q/hp?s=WU&a=01&b=19&c=2010&d=01&e=19&f=2010&g=d
        */


public class GetData {
    private final static String YAHOO_FINAL_URL = "http://ichart.yahoo.com/d/table.csv?";
    public static List<StockData> getStockCsvData(String stockName,String fromDate,String toDate){
        List<StockData> list = new ArrayList<StockData>();
        String[] dateFromInfo = fromDate.split("-");
        String[] toDateInfo = toDate.split("-");
        String code = stockName.substring(0,6);
        String a = (Integer.valueOf(dateFromInfo[1])-1) + "";//a:起始时间 月  这里要减一
        String b = dateFromInfo[2];//b:起始时间 日
        String c = dateFromInfo[0];//c:起始时间 年
        String d = (Integer.valueOf(toDateInfo[1])-1) + "";//a:结束时间 月  这里要减一
        String e = toDateInfo[2];//b:结束时间 日
        String f = toDateInfo[0];//c:结束时间 年
        String params = "&a=" + a + "&b=" + b + "&c=" + c + "&d=" + d + "&e="+e+"&f=" + f;
        String url = YAHOO_FINAL_URL + "s="+ stockName + params +"&g=d&ignore=.csv";
        System.out.println(url);
        URL MyURL = null;
        URLConnection con = null;
        InputStreamReader ins = null;
        BufferedReader br = null;
        try{
            MyURL = new URL(url);
            con = MyURL.openConnection();
            ins = new InputStreamReader(con.getInputStream(),"UTF-8");
            br = new BufferedReader(ins);
            String newline = br.readLine();
            while ((newline = br.readLine())!= null){
                System.out.println(newline);
                String stockInfo[] = newline.trim().split(",");
                String stockTime = stockInfo[0];
                float open = Float.valueOf(stockInfo[1]);
                float high = Float.valueOf(stockInfo[2]);
                float low = Float.valueOf(stockInfo[3]);
                float close = Float.valueOf(stockInfo[4]);
                float volume = Float.valueOf(stockInfo[5]);
                float adj = Float.valueOf(stockInfo[6]);
                StockData sd = new StockData();
                sd.setCode(code);
                sd.setDate(stockTime);
                sd.setOpen(open);
                sd.setHigh(high);
                sd.setLow(low);
                sd.setClose(close);
                sd.setVolume(volume);
                sd.setAdj(adj);
                list.add(sd);
            }
            return list;
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return null;
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static void getFromSohu(){
        URL MyURL = null;
        URLConnection con = null;
        InputStreamReader ins = null;
        BufferedReader br = null;
        try{
            String url = "http://q.stock.sohu.com/hisHq?code=cn_600000&start=20100101&end=20160414";
            MyURL = new URL(url);
            con = MyURL.openConnection();
            ins = new InputStreamReader(con.getInputStream(),"UTF-8");
            br = new BufferedReader(ins);
            System.out.println(br);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public JSONObject httpRequest(String code,String fromDate,String toDate ,int type) {
        JSONObject jsonObject = null;
        StringBuffer buffer = new StringBuffer();
        try {
           // String[] dateFromInfo = fromDate.split("-");
            //String[] toDateInfo = toDate.split("-");
            String startUrl = "http://stock.liangyee.com/bus-api/stock/freeStockMarketData/getDailyKBar?userKey=707F11238F5D4E9AAC79461F819DFC30";
            startUrl = startUrl +"&startDate=" + fromDate + "&symbol=" + code + "&endDate=" + toDate + "&type="+ String.valueOf(type);
//            String startDate = "";
//            for(int i =0;i<dateFromInfo.length;i++){
//                startDate+=dateFromInfo[i];
//            }
//            String endDate = "";
//            for(int i =0;i<toDateInfo.length;i++){
//                endDate+=toDateInfo[i];
//            }
            URL url = new URL(startUrl);
            // http协议传输
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();

            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod("GET");


            httpUrlConn.connect();
            // 将返回的输入流转换成字符串
            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

           // buffer.append("{");
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
           // buffer.append(":null}");

           // System.out.println(buffer.lastIndexOf("]"));
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public List<StockData> parserStock(JSONObject JS,String code,int type){
        JSONObject jsonObject =JS;
        JSONArray jsonArray = JS.getJSONArray("result");
        if(jsonArray == null){
            return null;
        }
        List<StockData> list = new ArrayList<>();
        int size = jsonArray.size();
        for(int i =0;i<size;i++){
            //System.out.println(jsonArray.get(i));
            String[] stockInfo = jsonArray.get(i).toString().split(",");
            StockData sd = new StockData();
            //String code = JS.getString("code");
            float open = Float.valueOf(stockInfo[1]);
            float close = Float.valueOf(stockInfo[2]);
            float high = Float.valueOf(stockInfo[3]);
            float low = Float.valueOf(stockInfo[4]);
            float volume = Float.valueOf(stockInfo[5]);
            float upAndDown = Float.valueOf(stockInfo[6]);
            sd.setCode(code);
            sd.setDate(stockInfo[0]);
            sd.setOpen(open);
            sd.setHigh(high);
            sd.setLow(low);
            sd.setClose(close);
            sd.setVolume(volume);
            sd.setUpAndDown(upAndDown);
            sd.setType(type);
            list.add(sd);
        }
        return list;
    }

    public  boolean getDate(String code){
        try{
            DBHelper db = new DBHelper();
            //先判断是否已经在数据库中存储过该股票数据
            List<String> hasStoredCode = db.queryCode("select code from hasstored;");
            for(int i =0;i<hasStoredCode.size();i++){
                if(hasStoredCode.get(i).equals(code)){
                    return false;
                }
            }
            int type = -1;
            String codeType = null;
            //上证
            if(code.substring(0,1).equals("9") || code.substring(0,1).equals("6")){
                type = 0;
                codeType = "ss";
            }else{
                //深证
                type = 1;
                codeType = "sz";
            }
            JSONObject JS = httpRequest(code,"1990-01-01","2017-07-11",type);
            //System.out.println(JS);
            List<StockData> list = new ArrayList<>();
            if(parserStock(JS,code,type)!=null){
                list = parserStock(JS,code,type);
            }

            db.createTable(code+codeType);
            int size = list.size();
            for(int  j=0;j<size;j++){
                if(db.insertIntoStock(list.get(j),type) == true){
                    continue;
                }else{
                    db.deleteTable(code+codeType);
                    return false;
                }
            }
            db.insertIntoHasChooseAndHasStored("insert into hasstored values(?);",code);
            sleep(5000);








            String querySQL ="select date_format(stockTime,'%Y-%m'),avg(low) from 600000ss where stockTime >= '2015-01-01' and stockTime <= '2016-01-01' group by year(stockTime),date_format(stockTime,'%Y-%m');";
            String queryMinDate = "select stockTime from 600000ss where stockTime=(select min(stockTime) from 600000ss);";
            String queryAve = "select avg(low) from 600000ss where stockTime >='1991-01-01' and stockTime <='2017-02-03' group by year(stockTime),date_format(stockTime,'%Y-%m');";
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }

    }
}
