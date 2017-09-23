package StockUtil;

/**
 * Created by 马大侠 on 2017/7/6.
 */

//股票实体类
public class StockData {
    private String code;//股票代码
    private String name;//股票名称
    private String date;//时间
    private double open = 0.0;//今日开盘价
    private double close = 0.0;//今日收盘价
    private double high = 0.0;//今日最高值
    private double low = 0.0;//今日最低值
    private double volume = 0.0;//今日成交总量
    private double adj = 0.0;//今日股票复权价
    private double upAndDown = 0.0;
    private int type = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getUpAndDown() {
        return upAndDown;
    }

    public void setUpAndDown(double upAndDown) {
        this.upAndDown = upAndDown;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getAdj() {
        return adj;
    }

    public void setAdj(double adj) {
        this.adj = adj;
    }
}
