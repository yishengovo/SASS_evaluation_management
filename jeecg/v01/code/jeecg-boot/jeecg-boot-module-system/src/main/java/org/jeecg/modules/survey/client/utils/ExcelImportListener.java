package org.jeecg.modules.survey.client.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Data
@Slf4j
public class ExcelImportListener extends AnalysisEventListener<Map<Integer, String>> {
    //定义每多少条数据进行数据库保存
    private static final int BATCH_COUNT = 128;
    //用list集合保存解析到的结果
    private List<Map<Integer, String>> list;

    public ExcelImportListener() {
        list = new ArrayList<>();
    }

    /**
     * 重写invokeHeadMap方法，获去表头，如果有需要获取第一行表头就重写这个方法，不需要则不需要重写
     *
     * @param headMap Excel每行解析的数据为Map<Integer, String>类型，Integer是Excel的列索引,String为Excel的单元格值
     * @param context context能获取一些东西，比如context.readRowHolder().getRowIndex()为Excel的行索引，表头的行索引为0，0之后的都解析成数据
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据：{}, currentRowHolder: {}", headMap.toString(), 			context.readRowHolder().getRowIndex());
        list.add(headMap);
    }

    /**
     * 重写invoke方法获得除Excel第一行表头之后的数据，
     * 如果Excel第二行也是表头，那么也会解析到这里，如果不需要就通过判断context.readRowHolder().getRowIndex()跳过
     *
     * @param integerStringMap    除了第一行表头外，数据都会解析到这个方法
     * @param analysisContext 和上面解释一样
     */
    @Override
    public void invoke(Map<Integer, String> integerStringMap, AnalysisContext analysisContext) {
        log.info("解析到一条数据：{}, currentRowIndex: {}----", integerStringMap.toString(), analysisContext.readRowHolder().getRowIndex());
        list.add(integerStringMap);
    }

    /**
     * 解析到最后会进入这个方法，需要重写这个doAfterAllAnalysed方法，然后里面调用自己定义好保存方法
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成！");
    }
}
