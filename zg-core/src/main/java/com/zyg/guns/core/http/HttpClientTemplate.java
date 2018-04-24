package com.zyg.guns.core.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyg.guns.core.utils.StringUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;


/**
 * Http Client Template
 *
 * @author jung.fan
 */
public class HttpClientTemplate {
    private final Logger logger = LoggerFactory.getLogger(HttpClientTemplate.class);
    private OkHttpClient okHttpClient;

    private final MediaType JSON_TYPE = MediaType.parse("application/json;charset=utf-8");

    public HttpClientTemplate(OkHttpClient okHttpClient){
        this.okHttpClient = okHttpClient;
    }

    /**
     * 发送post 请求
     * @param url
     * @param params
     * @return  JSONObject
     */
    public JSONObject postJSON(String url ,Map<String,Object> params) throws IOException{
        if(params == null){
            return null;
        }
        JSONObject jsonParams = (JSONObject) JSONObject.toJSON(params);
        RequestBody body = RequestBody.create(JSON_TYPE,jsonParams.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody);
        }
        return null;
    }

    /**
     * get请求
     * @param url
     * @return
     */
    public JSONObject get(String url) throws IOException{
        if(StringUtil.isEmpty(url)){
            return null;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody);
        }
        return null;
    }

    /**
     * 发送  x-www-form-urlencoded 请求
     * @param url
     * @param params
     * @return
     */
    public JSONObject postForm(String url,Map<String,Object> params) throws IOException{
        if (params == null) return null;

        FormBody.Builder formBuilder = new FormBody.Builder();
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,Object> entry = (Map.Entry) iterator.next();
            formBuilder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        FormBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody);
        }
        return null;
    }


    public JSONObject postForm(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody);
        }
        return null;
    }

    /**
     * 发送  x-www-form-urlencoded 请求
     * @param url
     * @param params
     * @return
     */
    public String postFormData(String url,Map<String,Object> params) throws IOException{
        if (params == null) return null;

        FormBody.Builder formBuilder = new FormBody.Builder();
        Iterator iterator = params.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,Object> entry = (Map.Entry) iterator.next();
            formBuilder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        FormBody formBody = formBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().string();
        }
        return null;
    }


    /**
     * 发送  x-www-form-urlencoded  formBody请求
     * @param url
     * @param formBody
     * @return
     */
    public JSONObject postForm(String url,FormBody formBody) throws IOException{
        if (formBody == null) return null;


        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        if(response.isSuccessful()){
            String responseBody = response.body().string();
            return JSON.parseObject(responseBody);
        }
        return null;
    }



    /**
     * get请求(原生url请求)
     * @param urlstr
     * @return
     */
    public String getDataByConnection(String urlstr) throws IOException{
        InputStream is = null;
        StringBuffer responseData = new StringBuffer();
        try{
            URL url = new URL(urlstr);
            //2.通过URL对象提出的openConnection方法创建URLConnection对象
            URLConnection connection = url.openConnection();
            //4.调用URLConnection对象提供的connect方法连接远程服务
            connection.connect();
            //6.获取输入流，从中读取资源数据
            is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            for (String line; (line = br.readLine()) != null; ) {
                responseData.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(is != null){
                is.close();
            }
        }
        System.out.println(URLDecoder.decode(responseData.toString()));
        return responseData.toString();
    }

    /**
     * get请求(直接返回ResponseBody)
     * @param urlstr
     * @return
     */
    public String getData(String urlstr) throws IOException{
        if(StringUtil.isEmpty(urlstr)){
            return null;
        }
        Request request = new Request.Builder()
                .url(urlstr).get().build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().string();
        }
        return null;
    }
}
