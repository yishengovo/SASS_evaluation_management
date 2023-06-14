package org.jeecg.modules.survey.client.resp;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import java.io.Serializable;

@Data
@ExcelTarget("user")
public class IncompleteUser implements Serializable {
    @Excel(name = "姓名",width = 15)
    private String name;
}
