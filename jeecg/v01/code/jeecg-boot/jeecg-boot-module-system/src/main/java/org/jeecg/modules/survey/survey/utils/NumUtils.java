package org.jeecg.modules.survey.survey.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * @author yuhuofei2021
 * @version 1.0
 * @description 数字处理工具类
 * @date 2022/7/14 10:50
 */
public class NumUtils {
    /**
     * @param data  小数
     * @param digit 百分数保留小数点位数
     * @return 返回百分数
     * @description java实现小数转百分数
     */
    public static String getPercent(double data, int digit) {
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(digit);
        return numberFormat.format(data);
    }

    //计算百分比
    public static String getPercent(int x, int y) {
        double d1 = x * 1.0;
        double d2 = y * 1.0;
        NumberFormat percentInstance = NumberFormat.getPercentInstance();
        // 设置保留几位小数，这里设置的是保留两位小数
        percentInstance.setMinimumFractionDigits(2);
        return percentInstance.format(d1 / d2);
    }

    //除法保留两位小数
    public static String device(int x, int y) {
        //格式化小数
        DecimalFormat df = new DecimalFormat("0.00");
        //返回的是String类型
        return df.format((float) x / y);
    }
    //加权平均数算法
    public static double getWeightedAverage(List<Double> list, List<Double> weight) {
        double sum = 0;
        double sumWeight = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i) * weight.get(i);
            sumWeight += weight.get(i);
        }
        return sum / sumWeight;
    }
    //将一个数字字符串去掉百分号 并转换为double类型
    public static double getDouble(String str) {
        String s = str.replace("%", "");
        return Double.parseDouble(s) / 100;
    }


}

