package org.jeecg.modules.survey.survey.controller;

import org.jeecg.modules.survey.survey.entity.SurUser;
import org.jeecg.modules.survey.survey.resp.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
      @GetMapping("/test")
    public R test() {
          List<Object> list = new ArrayList<>();
          SurUser user = new SurUser();
          user.setName("yisehng");
            user.setAge(18);
            list.add(user);
        return R.ok().put("data", list);
        }
}
