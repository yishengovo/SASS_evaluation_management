package org.jeecg;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Random;

/**
 * @BelongsProject: jeecg-boot
 * @BelongsPackage: org.jeecg
 * @Author: GGB
 * @CreateTime: 2022-10-09  14:55
 * @Description: TODO
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSMS {
    @Autowired
    RedisUtil redisUtil;

    @Test
    public void test01() throws IOException {
        String phone = "18189777090";
        //生成6位数字的验证码
        Random random = new Random();
        String code = "";
        for(int i=0;i<6;i++){
            int rand = random.nextInt(10);
            code += rand;
        }
        redisUtil.set(phone,code,120);

        //设置短信内容，并转码
        String  content = "【朗新天霁】验证码"+code+"(2分钟有效)。如非本人操作，请忽略本短信。";
        String unicode = new String(content.getBytes(),"UTF-8");
        String gbk = new String(unicode.getBytes("GB2312"));
        String url = "http://api.sms1086.com/Api/verifycode.aspx?username=18059257022&password=r12345&mobiles="+phone+"&content="+gbk+"&f=1&timestamp=2013-01-01%2012:00:00";

        HttpClient httpClient = HttpClients.createDefault();
//		HttpGet httpGet = new HttpGet(url);
        HttpGet httpGet = new HttpGet();
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000)
                .setConnectTimeout(60000).setConnectionRequestTimeout(60000).build();
        httpGet.setConfig(requestConfig);
        httpGet.setURI(URI.create(url));
        httpGet.setHeader("Accept", "application/x-www-form-urlencoded");
        httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        if(entity != null){
            result = EntityUtils.toString(entity,"UTF-8");
            System.out.println(result);
        }
        httpGet.abort();

    }
}
