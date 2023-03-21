package org.jeecg.modules.survey.survey.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yisheng
 * @version 1.0
 * @description string转list工具类
 * @date 2022/8/7 10:50
 */
public class ListUtils {
    public static List<String> toList(String str){
        //如果为空字符串
        if (str.length() == 0){
            return new ArrayList<>();
        }
        return Arrays.asList(str.substring(1, str.length() - 1).replaceAll("\\s", "").split(","));

    }
}
