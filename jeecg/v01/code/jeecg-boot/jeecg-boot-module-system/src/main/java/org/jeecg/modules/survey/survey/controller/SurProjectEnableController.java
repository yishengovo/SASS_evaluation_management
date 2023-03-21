package org.jeecg.modules.survey.survey.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.survey.survey.entity.SurProjectEnable;
import org.jeecg.modules.survey.survey.req.TestReport;
import org.jeecg.modules.survey.survey.service.ISurProjectEnableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.survey.controller
 * @Author: GGB
 * @CreateTime: 2022-11-09  16:50
 * @Description: TODO
 * @Version: 1.0
 */
@Api(tags = "项目控制")
@RestController
@RequestMapping("/survey/surProjectEnable")
@Slf4j
public class SurProjectEnableController extends JeecgController<SurProjectEnable, ISurProjectEnableService> {
    @Autowired
    ISurProjectEnableService surProjectEnableService;

    /**
     *   添加
     *
     * @param surProjectEnable
     * @return
     */
    @PostMapping()
    public Result<String> add(@RequestBody SurProjectEnable surProjectEnable) {
        if(surProjectEnable.getTimeEnable()==null){
            surProjectEnable.setTimeEnable(false);
        }
        //查看是否选择了时间
        if(surProjectEnable.getTimeEnable()&&(surProjectEnable.getStartTime()==null&&surProjectEnable.getEndTime()==null&&surProjectEnable.getDayStartTime()==null&&surProjectEnable.getDayStartTime()==null)){
            return Result.error("请选择时间!");
        }
        surProjectEnableService.saveProjectEnable(surProjectEnable);
        return Result.OK("添加成功！");
    }

    @GetMapping()
    public Result<SurProjectEnable> get(@RequestParam(name = "projectId") String projectId){
        if(oConvertUtils.isEmpty(projectId)){
            return Result.error("项目id为null");
        }
        return Result.ok(surProjectEnableService.getSurProjectEnable(projectId));
    }

    @DeleteMapping()
    public Result<String> delete(@RequestParam(name = "projectId") String projectId){
        if(oConvertUtils.isEmpty(projectId)){
            return Result.error("项目id为null！");
        }
        surProjectEnableService.deleteProjectEnable(projectId);
        return Result.OK("删除成功！");
    }

    @GetMapping("/timeControll")
    public Result<String> timeControll(@RequestParam(name = "projectId") String projectId){
        SurProjectEnable surProjectEnable = surProjectEnableService.getSurProjectEnable(projectId);
        //没有添加控制的项目直接放行
        if(surProjectEnable!=null){
            if(surProjectEnable.getTimeEnable()){
                Date date = DateUtil.date();
                Date startTime = surProjectEnable.getStartTime();
                Date endTime = surProjectEnable.getEndTime();
                Date dayStartTime = surProjectEnable.getDayStartTime();
                Date dayEndTime = surProjectEnable.getEndTime();
                if(startTime!=null&&startTime.getTime()>date.getTime()){
                    return Result.error("尚未到达开始时间!");
                }
                if(endTime!=null&&date.getTime()>endTime.getTime()){
                    return Result.error("答卷时间已结束!");
                }
                //当前时间的小时和分钟
                int dateHour = DateUtil.hour(date, true);
                int dateMinute = DateUtil.minute(date);
                int dateDay = dateHour*60 + dateMinute;
                if(dayStartTime!=null){
                    //设置的开始时间的小时和分钟
                    int dayStartTimeHour = DateUtil.hour(dayStartTime, true);
                    int dayStartTimeMinute = DateUtil.minute(dayStartTime);
                    int dayStartTimeDay = dayStartTimeHour*60 + dayStartTimeMinute;
                    //判断是否到开始时间
                    if(dayStartTimeDay>dateDay){
                        return Result.error("尚未到达开始时间!");
                    }
                }
                if(dayEndTime!=null){
                    //设置的结束时间的小时和分钟
                    int dayEndTimeHour = DateUtil.hour(dayEndTime, true);
                    int ddayEndTimeMinute = DateUtil.minute(dayEndTime);
                    int dayEndTimeDay = dayEndTimeHour*60 + ddayEndTimeMinute;
                    if(dayEndTimeDay<dateDay){
                        return Result.error("答卷时间已结束!");
                    }
                }
            }
        }
        return Result.ok("没有项目控制!");
    }

    /*测试报表api的接口*/
    @GetMapping("/testReport")
    public JSONObject testReport(@RequestParam String id) {
        Result result = new Result();
        JSONObject jsonObject = new JSONObject();

        if (oConvertUtils.isNotEmpty(id)&&id.equals("1595959344500445185")) {
            List<TestReport> testReportList = new ArrayList<>();
            TestReport testReport = new TestReport();
            testReport.setName("a1");
            testReport.setValue(9);
            testReportList.add(testReport);
            TestReport testReport2 = new TestReport();
            testReport2.setName("b2");
            testReport2.setValue(9);
            testReportList.add(testReport2);
            TestReport testReport3 = new TestReport();
            testReport3.setName("c3");
            testReport3.setValue(10);
            testReportList.add(testReport3);
            TestReport testReport4 = new TestReport();
            testReport4.setName("d4");
            testReport4.setValue(13);
            testReportList.add(testReport4);
            TestReport testReport5 = new TestReport();
            testReport5.setName("e5");
            testReport5.setValue(12);
            testReportList.add(testReport5);
            TestReport testReport6 = new TestReport();
            testReport6.setName("f6");
            testReport6.setValue(11);
            testReportList.add(testReport6);
            TestReport testReport7 = new TestReport();
            testReport7.setName("g7");
            testReport7.setValue(14);
            testReportList.add(testReport7);
            TestReport testReport8 = new TestReport();
            testReport8.setName("h8");
            testReport8.setValue(16);
            testReportList.add(testReport8);
            TestReport testReport9 = new TestReport();
            testReport9.setName("i9");
            testReport9.setValue(13);
            testReportList.add(testReport9);
            jsonObject.put("data", testReportList);
        } else {
            jsonObject.put("data", null);
        }
        return jsonObject;
    }
}
