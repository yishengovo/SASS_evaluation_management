package org.jeecg.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** 单数据源配置（jeecg.datasource.open = false时生效） @Author zhoujf */
@Configuration
@MapperScan(value = {"org.jeecg.modules.**.mapper*"})
public class MybatisPlusSaasConfig {
  @Resource private RedisUtil redisUtil;

  /** tenant_id 字段名 */
  private static final String TENANT_FIELD_NAME = "tenant_id";
  /** 哪些表需要做多租户 表需要添加一个字段 tenant_id */
  private static final List<String> TENANT_TABLE = new ArrayList<String>();

  // 哪些接口跳过租户模式
  private static final List<String> REQUEST_URI = new ArrayList<String>();

  static {
    TENANT_TABLE.add("demo");
    TENANT_TABLE.add("sur_array_title");
    TENANT_TABLE.add("sur_evaluation_relationship");
    TENANT_TABLE.add("sur_evaluation_user");
    TENANT_TABLE.add("sur_evaluation_weight");
    TENANT_TABLE.add("sur_filter");
    TENANT_TABLE.add("sur_personal_survey");
    TENANT_TABLE.add("sur_project");
    // TENANT_TABLE.add("sur_project_survey");
    TENANT_TABLE.add("sur_project_user");
    // TENANT_TABLE.add("sur_question");
    // TENANT_TABLE.add("sur_question_choice");
    TENANT_TABLE.add("sur_question_choice_project");
    TENANT_TABLE.add("sur_question_dimension");
    TENANT_TABLE.add("sur_question_project");
    TENANT_TABLE.add("sur_report_filter");
    TENANT_TABLE.add("sur_response_list");
    TENANT_TABLE.add("sur_result");
    TENANT_TABLE.add("sur_survey_lib");
    TENANT_TABLE.add("sur_survey_project");
    TENANT_TABLE.add("sur_user");
    TENANT_TABLE.add("sur_user_result");
    TENANT_TABLE.add("sur_contact");
    TENANT_TABLE.add("sur_group");
    TENANT_TABLE.add("sur_group_contact");
    TENANT_TABLE.add("sur_contact_field");
    TENANT_TABLE.add("sur_contact_field_information");
    TENANT_TABLE.add("sur_payment");
    // TENANT_TABLE.add("survey");

    REQUEST_URI.add("/jeecg-boot/client/userProject/queryById");
    REQUEST_URI.add("/jeecg-boot/client/userProject/saveAnswer");
    REQUEST_URI.add("/jeecg-boot/client/userProject/judge");
    REQUEST_URI.add("/jeecg-boot/client/userProject/getPhoneCode");
    REQUEST_URI.add("/jeecg-boot/client/userProject/queryUserSurvey");
    REQUEST_URI.add("/jeecg-boot/client/surPayment/callback");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/project");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/survey");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/count");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/tag");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/tagDistribution");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/projectDistribution");
    REQUEST_URI.add("/jeecg-boot/survey/surProject/dashBord/projectRelease");
    // REQUEST_URI.add("/jeecg-boot/client/userProject/getSurveyTemplate");
    // REQUEST_URI.add("");
    //        //角色、菜单、部门
    //        tenantTable.add("sys_role");
    //        tenantTable.add("sys_permission");
    //        tenantTable.add("sys_depart");
  }

  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    // 先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
    interceptor.addInnerInterceptor(
        new TenantLineInnerInterceptor(
            new TenantLineHandler() {
              @Override
              public Expression getTenantId() {

                // String tenantIdStr = ;
                HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                // HttpSession session = request.getSession();
                //                session.getAttribute("tenantId");
                // String tenantId =
                // oConvertUtils.getString((String)redisUtil.get("tenantId"+request.getHeader(CommonConstant.X_ACCESS_TOKEN)),"0");
                // String tenantId = oConvertUtils.getString(TenantContext.getTenant(),"0");
                String tenantId =
                    oConvertUtils.getString(request.getHeader(CommonConstant.TENANT_ID), "0");
                // String tenantId = oConvertUtils.getString(session.getAttribute("tenantId"),"0");
                System.out.println("当前SAAS租户的id是:" + tenantId);
                return new LongValue(tenantId);
              }

              @Override
              public String getTenantIdColumn() {
                return TENANT_FIELD_NAME;
              }

              // 返回 true 表示不走租户逻辑
              @Override
              public boolean ignoreTable(String tableName) {
                // 如果为管理员租户id1则放开租住拦截
                HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
                String tenantId =
                    oConvertUtils.getString(request.getHeader(CommonConstant.TENANT_ID), "0");
                if (tenantId.equals("1")) {
                  return true;
                }

                // 放开的接口不走租户
                String uri = request.getRequestURI();
                System.out.println("uri:" + uri);
                for (String requestUri : REQUEST_URI) {
                  if (requestUri.equals(uri)) {
                    System.out.println("requestUri" + requestUri);
                    System.out.println("uri+++++" + uri);
                    return true;
                  }
                }

                for (String temp : TENANT_TABLE) {
                  if (temp.equalsIgnoreCase(tableName)) {
                    return false;
                  }
                }
                return true;
              }
            }));
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
    return interceptor;
  }

  //    /**
  //     * 下个版本会删除，现在为了避免缓存出现问题不得不配置
  //     * @return
  //     */
  //    @Bean
  //    public ConfigurationCustomizer configurationCustomizer() {
  //        return configuration -> configuration.setUseDeprecatedExecutor(false);
  //    }
  //    /**
  //     * mybatis-plus SQL执行效率插件【生产环境可以关闭】
  //     */
  //    @Bean
  //    public PerformanceInterceptor performanceInterceptor() {
  //        return new PerformanceInterceptor();
  //    }

}
