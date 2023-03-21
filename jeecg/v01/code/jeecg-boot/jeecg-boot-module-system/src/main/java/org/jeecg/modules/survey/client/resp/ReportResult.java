package org.jeecg.modules.survey.client.resp;

import lombok.Data;

import java.util.List;

/**
 * @author:一笙
 * @create: 2023-01-12 15:11 @Description: 柱状图返回类
 */
@Data
public class ReportResult {
  private List<Object> data;
}
