package Analysis;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 马大侠 on 2017/7/6.
 */

//数据排序算法
public class SortMapByValue {
    public int getMaxValue(int[] array){
        Arrays.sort(array);
        int length = array.length;
        if(length==0){
            return -1;
        }else{
            int max = array[length-1];
            return max;
        }

    }

    public static double getMaxDoubleValue(List<Double> list){

        int length = list.size();
        double max = list.get(0);
        for(int i=0;i<length;i++){
            if(list.get(i)>max){
                max = list.get(i);
            }
        }
        return max;
    }
}
