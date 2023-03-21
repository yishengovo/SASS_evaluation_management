package org.jeecg.modules.survey.survey.dto;

import lombok.Data;

//报表选项dto
@Data
public class ReportDto {
    //id
    private String value;
    //值
    private String text;
    private String title;
}
