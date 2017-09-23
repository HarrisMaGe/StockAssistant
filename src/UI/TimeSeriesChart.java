package UI;

//import com.orsoncharts.*;

import Tools.DBHelper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.*;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;

/**
 * Created by 马大侠 on 2017/7/6.
 */
class Count{
    static int count =0;
    public static void reset(){
        count=0;
    }
}
public class TimeSeriesChart {

    public void createChart(List<List<String>> selectCode,String fromdate,String toDate,DBHelper db) {

        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
        ChartFactory.setChartTheme(mChartTheme);
        TimeSeriesCollection mDataset = GetDataset(selectCode,fromdate,toDate,db);
        //System.out.println(Count.count);
        JFreeChart mChart = null;// 是否生成超链接
        try {
            mChart = ChartFactory.createTimeSeriesChart(
                    new String("结果".getBytes(),"utf-8"),//图名字
                    new String("年份".getBytes(),"utf-8"),//横坐标
                    new String("价格".getBytes(),"utf-8"),//纵坐标
                    mDataset,//数据集
                    //PlotOrientation.VERTICAL,
                    true, // 显示图例
                    true, // 采用标准生成器
                    false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        XYPlot plot = (XYPlot) mChart.getXYPlot();
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.BLUE);//背景底部横虚线
        plot.setOutlinePaint(Color.RED);//


        XYLineAndShapeRenderer lasp = (XYLineAndShapeRenderer) plot.getRenderer();
        for(int i=0;i<10;i++){
            try{
                lasp.setSeriesStroke(i, new BasicStroke(1.5F));
            }catch (Exception e){
                break;
            }

        }


        //DateAxis dateAxis = (DateAxis)mPlot.getDomainAxis();


        DateAxis dateaxis = (DateAxis)plot.getDomainAxis();
        if(Count.count>200){
            dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR,3));
        }else if(Count.count>100 && Count.count<200){
            dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR,2));
        }else {
            dateaxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR,1));
        }

        dateaxis.setDateFormatOverride(new java.text.SimpleDateFormat("Y"));
        //dateaxis.set(0.5f);
        ChartFrame mChartFrame = null;
        try {
            mChartFrame = new ChartFrame(new String("折线图\0".getBytes(),"utf-8").substring(0,3), mChart);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mChartFrame.pack();
        mChartFrame.setVisible(true);
        mChartFrame.setLocationRelativeTo(null);
        Count.reset();
    }
    public TimeSeriesCollection GetDataset(List<List<String>> selectCode,String fromdate,String toDate,DBHelper db)
    {
        TimeTableXYDataset mDataset = new TimeTableXYDataset();
//        StackedXYBarRenderer renderer = new StackedXYBarRenderer();
//        XYPlot plot = new XYPlot(dataset_bar, domainAxis, rangeAxis, renderer);
        //JFreeChart chart = new JFreeChart("", plot);
        //DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
        int size = selectCode.size();
        String sql = "";
        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        for(int i =0;i<size;i++) {

            if (selectCode.get(i).get(0).substring(0, 1).equals("9") || selectCode.get(i).get(0).substring(0, 1).equals("6")) {
                sql = "select date_format(stockTime,'%Y-%m'),avg(low) from " + selectCode.get(i).get(0) + "ss where stockTime >= '" + fromdate + "' and stockTime <= '" + toDate + "' group by year(stockTime),date_format(stockTime,'%Y-%m');";
            } else {
                sql = "select date_format(stockTime,'%Y-%m'),avg(low) from " + selectCode.get(i).get(0) + "sz where stockTime >= '" + fromdate + "' and stockTime <= '" + toDate + "' group by year(stockTime),date_format(stockTime,'%Y-%m');";
            }

           // System.out.println(sql);
            List<List<String>>list = new ArrayList<>();
            list = db.queryLowAndDate(sql);
            int length = list.size();
            if(length > Count.count){
                Count.count=length;
            }
            TimeSeries timeSeries=new TimeSeries(selectCode.get(i).get(0), Month.class);
            for(int j=0;j<list.size();j++){

                String temp[] = list.get(j).get(0).split("-");
                timeSeries.add(new Month(Integer.valueOf(temp[1]),Integer.valueOf(temp[0])),Double.valueOf(list.get(j).get(1)));
                //timeSeries.add(Double.valueOf(list.get(j).get(1)),selectCode.get(i).get(0),list.get(j).get(0));
            }
            timeSeriesCollection.addSeries(timeSeries);
        }

        return timeSeriesCollection;
    }
}
