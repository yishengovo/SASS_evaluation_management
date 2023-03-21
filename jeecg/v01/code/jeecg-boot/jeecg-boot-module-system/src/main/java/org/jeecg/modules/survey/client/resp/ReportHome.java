package org.jeecg.modules.survey.client.resp;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg.modules.demo.client.resp
 * @Author: GGB
 * @CreateTime: 2022-12-09  11:34
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ReportHome {
    /*总销售额*/
    Integer salesTotal;
    /*总销售额周同比增长*/
    Integer salesTotalWeekCompared;
    /*总销售额日同比增长*/
    Integer salesTotalDayCompared;
    /*订单量*/
    Integer OrderTotal;
    /*近10天的订单量*/
    List<DayValue> orderTenDayValueList;
    /*日订单量*/
    Integer OrderDay;
    /*支付笔数*/
    Integer paymentTotal;
    /*近10天的支付量*/
    List<DayValue> paymentTenDayValueList;
    /*日支付量*/
    Integer paymentDay;
    /*积分花费百分比*/
    Integer integralCost;
    /*积分排行榜*/
    List<Leaderboard> integralLeaderboards;
    /*总充值排行榜*/
    List<Leaderboard> costLeaderboards;
    /*积分柱状图*/
    List<MonthValue> integralHistogram;
    /*支付柱状图*/
    List<MonthValue> paymentHistogram;
}
