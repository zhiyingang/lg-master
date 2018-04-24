import com.alibaba.fastjson.JSONObject;
import com.zyg.guns.core.http.FileUtil;
import com.zyg.guns.core.http.HttpClientTemplate;
import com.zyg.guns.core.utils.Base64Util;
import junit.framework.TestCase;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yg.zhi on 2018/4/24.
 */
public class ZgTest extends TestCase {

    String appKey = "GxDKkbdTZ4lQtP2AiQ7DBy7k";
    String secretKey = "B8zHC3s83BhvZGDHKWGXXIyG20l99NyG ";

    private HttpClientTemplate httpClientTemplate;

    public void setUp(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        httpClientTemplate = new HttpClientTemplate(builder.build());
    }

    @Test
    public void testApp()
    {
        String token = getToken(appKey, secretKey);
        System.out.println(token);
//        String token ="24.7c7e16b27dfbbb1802e66e7bf76b7aec.2592000.1527130219.282335-11146540";
        String filePath = "D:\\code\\222.jpg";
        String jsonObject = getCard(token,filePath);
        System.out.println(jsonObject);
    }


    public String getCard(String accessToken,String filePath){
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token"+accessToken;
        try {
            byte[] imgData = com.zyg.guns.core.util.FileUtil.toByteArray(filePath);
            String imgStr = Base64Util.encode(imgData);
            // 识别身份证正面id_card_side=front;识别身份证背面id_card_side=back;
            String params = "id_card_side=front&" + URLEncoder.encode("image", "UTF-8") + "="
                    + URLEncoder.encode(imgStr, "UTF-8");
            String result = com.zyg.guns.core.http.HttpUtils.post(url, accessToken, params);

            return result;
        }catch (Exception e){

        }
        return null;
    }



    public String getToken(String appKey,String secretKey){
        String access_token = null;
//        String url = "https://aip.baidubce.com/oauth/2.0/token";
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + appKey
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + secretKey;

        try {
            JSONObject jsonObject = httpClientTemplate.postForm(getAccessTokenUrl);
            if(jsonObject != null){
                access_token = jsonObject.getString("access_token");
            }
        }catch (IOException e){
        }
        return access_token;
    }

    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }


}
