import com.alibaba.fastjson.JSON;
import com.baidu.aip.face.AipFace;
import com.baidu.aip.ocr.AipOcr;
import com.zyg.guns.ocr.dto.IdentityInfo;
import com.zyg.guns.ocr.util.DateUtil;
import com.zyg.guns.ocr.util.OCRUtil;
import com.zyg.guns.ocr.util.ThumbnailUtilService;
import junit.framework.TestCase;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yg.zhi on 2018/4/24.
 */
public class OCRTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int MAX_LENGTH = 1024 * 150;
    private ThumbnailUtilService thumbnailUtilService = new ThumbnailUtilService();

    String appId = "11146540";
    String appKey = "GxDKkbdTZ4lQtP2AiQ7DBy7k";
    String secretKey = "B8zHC3s83BhvZGDHKWGXXIyG20l99NyG";

    private AipOcr aipOcr;

    private  AipFace aipFace;

    public void setUp(){
        aipOcr = new AipOcr(appId,appKey,secretKey);
        aipOcr.setConnectionTimeoutInMillis(6000);

        aipFace = new AipFace(appId,appKey,secretKey);
        aipFace.setConnectionTimeoutInMillis(6000);
    }

    //身份证识别
    @Test
    public void testCard() throws Exception{
        logger.info("开始调用百度身份证识别接口... at {} ", DateUtil.formatNow());
        long start = System.currentTimeMillis();

        String filePath = "D:\\code\\222.jpg";
        byte[] bytes = com.zyg.guns.core.util.FileUtil.toByteArray(filePath);
        if (bytes.length > MAX_LENGTH) {
            bytes = thumbnailUtilService.createThumbnail(bytes);
            logger.info("压缩过后的文件大小为：{} KB", bytes.length / 1024);
        }

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("detect_direction", "true");
        JSONObject idCard = aipOcr.idcard(bytes, true, options);
        long end = System.currentTimeMillis();
        logger.info("调用百度身份证识别接口调用结束... at {} , cost {} ms", DateUtil.formatNow(), end - start);
        IdentityInfo identityInfo = OCRUtil.getIdentityInfo(idCard);
        System.out.println(JSON.toJSONString(identityInfo));
    }

    //人脸对比
    @Test
    public void testFace()  throws Exception{
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("ext_fields", "qualities");
        options.put("image_liveness", ",faceliveness");
        options.put("types", "7,13");

        //参数为本地图片路径列表
        String path1 = "D:\\code\\111.jpg";
        String path2 = "D:\\code\\333.jpg";
        ArrayList<String> images = new ArrayList<String>();
        images .add(path1);
        images.add(path2);
        System.out.println(".............");
        JSONObject res = aipFace.match(images, options);
        System.out.println(res);
        System.out.println(".......3......");
    }
}
