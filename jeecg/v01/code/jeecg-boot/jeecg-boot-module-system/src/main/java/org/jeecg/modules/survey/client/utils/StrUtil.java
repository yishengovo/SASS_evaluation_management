package org.jeecg.modules.survey.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:一笙
 * @create: 2023-01-10 14:18 @Description: 字符串工具类
 */
public class StrUtil {
  /**
   * 过滤非数字
   *
   * @param str
   * @return
   */
  public static String getNumeric(String str) {
    String regEx = "[^0-9]";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(str);
    return m.replaceAll("").trim();
  }
}
