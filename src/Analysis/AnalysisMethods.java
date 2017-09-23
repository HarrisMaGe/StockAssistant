package Analysis;

import Tools.DBHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by 马大侠 on 2017/7/6.
 */

//股票历史统计 以及 股票预测
public class AnalysisMethods {
    DBHelper db = new DBHelper();
    //时间指数
    public double timeAnalysis(List<Double> list,double currentLow){
        double var = 0.0;
        double counter = 0;
        int size = list.size();
        //double currentPrice = list.get(0);
        for(int i =0;i<size;i++){
            if(currentLow<list.get(i)){
                counter++;
            }
        }
        var = counter/size;
        return var;
    }

    //计算一个序列中连续增长周期为times的次数
    public int[] timeSeries(List<Double> list,int times,double fuzzyVar){
        Collections.reverse(list);//倒置数组
        int size = list.size();
        int counter = 0;
        int[] result = {0,0};
        //对每个数组元素
        try{
            for(int i =0;i<size;i++){
                //递增次数计数器，数组索引
                int temp = 0;
                int k =i;
                //对于times次递增
                for(int j =0;j<times;j++){
                    //判断是否递增
                    if(list.get(k) <= list.get(k+1)){
                        temp++;
                        k++;
                    }
                }
                //如果 递增的次数>= 要求的次数*模糊指数，且在times长度下最后一个数字大于第一个数字，
                // 则判断存在一个递增次数为times的子序列
                if((temp >= times * fuzzyVar) && (list.get(i+times) > list.get(i))){
                    counter++;
                }
            }
            //result数组：第一位为递增长度为times的子序列的个数
            //第二位为序列总长度


        }catch (Exception e){

        }
        result[0] = counter;
        result[1] = size;
        return result;
    }

    //获取最大递增序列长度
    public int getMaxSeries(List<Double> list,double fuzzyVar){
        Collections.reverse(list);
        SortMapByValue sv = new SortMapByValue();
        int size = list.size();
        int result[] = new int[size];
        try{
            for(int i =1;i<size;i++){
                int counter = 1;
                int k;
                for(k = i-1;k<=size;k++){
                    if(list.get(k+1) > list.get(k)){
                        counter++;
                    }else{
                        break;
                    }
                }
                    //if(counter/i >= fuzzyVar){
                result[i] = counter;
                    //}
            }
        }catch (Exception e){
           //  e.printStackTrace();
        }

        int max = sv.getMaxValue(result);
        return max;
    }

    public static List<List<String>> listAllStockInfo(){
        DBHelper db = new DBHelper();
        List<List<String>> list = new ArrayList<>();
        list = db.selectStockTable("select code,name,c_name from stocktable;");
        return list;
    }
}
