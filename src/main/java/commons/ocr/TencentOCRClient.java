package commons.ocr;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class TencentOCRClient {

    private static String CONFIG_FILE = "OCR_Tencent";
    private static String host="recognition.image.myqcloud.com";
    private static int appid=1256045784;
    private static String secretId="AKIDaMG6pQODUcmojSkOmz1tYecOzObeoT8d";
    private static String secretKey="zsb8UebNyQjdNuFOLOK4hCYjiW9apung";
    private static String targeturl="http://recognition.image.myqcloud.com/ocr/handwriting";
    private static String encoding = "UTF-8";



    /**
     * 识别图片
     * @param
     * @param paramsMap 参数map
     * @return
     * @throws Exception
     */
    public static Map<String, Object> recognizeImage(Map<String,Object> paramsMap) throws Exception{
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(targeturl);// 创建httpPost
        httpPost.setHeader("host", host);
        //设置签名
        httpPost.setHeader("Authorization", SignUtil.appSign(appid, secretId, secretKey, "", 2592000));//设置请求头, 签名
        //设置参数
        JSONObject requestParam = new JSONObject();
        requestParam.put("appid", String.valueOf(appid));
        for(String key :paramsMap.keySet()){//循环加入请求参数
            requestParam.put(key, paramsMap.get(key));
        }
        //请求报文
        StringEntity entity = new StringEntity(requestParam.toString(), encoding);
        entity.setContentEncoding(encoding);
        entity.setContentType("application/json");//发送json数据需要设置contentType
        httpPost.setEntity(entity);
        httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 120000);
        httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 120000);
        int state = 0;
        String result = "";
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                result = EntityUtils.toString(responseEntity);
            }else{
                //new BaseBean().writeLog("读取OCR驾驶证或者行驶证接口失败，状态码:"+state);
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("state", state);
        resultMap.put("result", result);
        return resultMap;
    }


    //测试
    public static void main(String[] args) {
        String imgurl = "http://m.qpic.cn/psb?/V114AzJ02UpCax/Y2VdAOzz6ZFtCmLL99RBhWFe*N5BKWXpoSf*3FuVp9E!/b/dAgBAAAAAAAA&bo=oAU4BAAAAAARB6k!&rf=viewer_4";
        try {
            Map<String, Object> requestParam = new HashMap<String, Object>();
            requestParam.put("url", imgurl);
            Map<String,Object> res =recognizeImage(requestParam);

            JSONObject resultJson = JSONObject.fromObject(res.get("result"));
            System.out.println(resultJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
