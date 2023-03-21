package org.jeecg.modules.survey.client.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.survey.client.entity.SurTopUp;
import org.jeecg.modules.survey.client.service.ISurTopUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: jeecg-boot @BelongsPackage: org.jeecg.modules.demo.client.controller @Author:
 * GGB @CreateTime: 2022-11-30 15:54 @Description: TODO @Version: 1.0
 */
@Api(tags = "积分")
@RestController
@RequestMapping("/client/surTopUp")
@Slf4j
public class SurTopUpController {
  @Autowired ISurTopUpService surTopUpService;

  @PostMapping()
  public Result<String> add(@RequestBody JSONObject jsonObject) {
    Integer integral = jsonObject.getInteger("integral");
    if (integral <= 0 || integral == null) {
      return Result.error("积分必须大于0！");
    }
    surTopUpService.addTopUp(integral);
    return Result.ok("添加成功!");
  }

  @GetMapping()
  public Result<SurTopUp> get() {
    return Result.ok(surTopUpService.getSurTopUp());
  }
}
