package org.jeecg.modules.survey.client.service;

import org.jeecg.modules.survey.client.req.ExportReq;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: excel相关业务接口
 * @Author: 一笙
 * @Date:   2022-11-26
 * @Version: V1.0
 */
public interface ExcelService {
    //导出excel
    void exportExcel(HttpServletResponse response, List<List<String>> head, List<List<Object>> data) throws Exception;
    //测评问卷导出
    void exportEvaluation(HttpServletResponse response, ExportReq req) throws Exception;
    //调查问卷导出
    void exportInvestigation(HttpServletResponse response, ExportReq req) throws Exception;
    //360问卷导出
    void export360(HttpServletResponse response, ExportReq req) throws Exception;
}
