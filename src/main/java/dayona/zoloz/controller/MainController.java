package dayona.zoloz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zoloz.api.sdk.client.OpenApiClient;

@RestController
@RequestMapping("zoloz/")
public class MainController {
    @GetMapping("welcome")
    public String welcome(){
        return "Welcome";
    }

    private OpenApiClient openApiClient(){
        OpenApiClient oc=new OpenApiClient();
        oc.setHostUrl(ZOLOZ_URL);
        oc.setClientId(clientId);
        oc.setMerchantPrivateKey(MERCHANT_PRIVATE);
        oc.setOpenApiPublicKey(ZOLOZ_PUB);
        oc.setSigned(true);
        return oc;
    };
    
    
    
    @PostMapping("facecapture/initialize")
    public JSONObject realIdInit(@RequestBody String metaInfo) {

        // Step 1: instantiate the request object and provide necessary parameters
        JSONObject apiReq = new JSONObject();   
        apiReq.put("flowType", "REALIDLITE_KYC");
        apiReq.put("bizId", String.valueOf(System.currentTimeMillis()));
        apiReq.put("userId", String.valueOf(System.currentTimeMillis()));
        apiReq.put("metaInfo", JSON.parseObject(metaInfo));
        apiReq.put("docType", "00000001003");
        
        // Step 2: call the ZOLOZ API through openApiClient
        openApiClient().setSigned(true);
        String apiRespStr = openApiClient().callOpenApi(
                "v1.zoloz.facecapture.initialize",
                JSON.toJSONString(apiReq)
        );

        // Step 3: process the ZOLOZ API response and construct the return object
        JSONObject apiResp = JSON.parseObject(apiRespStr);
        JSONObject response = new JSONObject(apiResp);
        // response.put("rsaPubKey", openApiClient().getOpenApiPublicKey());
        return response;
    }

    @PostMapping("facecapture/checkresult")
    public JSONObject faceCaptureCheck(@RequestBody String req) {
        JSONObject request=JSON.parseObject(req);
        String businessId = "" + System.currentTimeMillis();
        String transactionId = request.getString("transactionId");
        String isReturnImage = request.getString("isReturnImage");

        JSONObject apiReq = new JSONObject();
        apiReq.put("bizId", businessId);
        apiReq.put("transactionId", transactionId);
        apiReq.put("isReturnImage", isReturnImage);

        String apiRespStr = openApiClient().callOpenApi(
                "v1.zoloz.facecapture.checkresult",
                JSON.toJSONString(apiReq)
        );

        JSONObject apiResp = JSON.parseObject(apiRespStr);

        JSONObject response = new JSONObject(apiResp);
        System.out.println("CHECK RESULT");
        return response;
    }
    
    String ZOLOZ_URL="https://sg-sandbox-api.zoloz.com";
    String clientId = "2188485307665580";
    String ZOLOZ_PUB = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvg78EIG7CKTV+FLotCu/4hJgbhPukVU1Ui8uk3tlDArt8zD8q4SH+lBovaOchxeyamSQ0HK3NgvgTIcfDhFcONETTzNr1F9aI1jiikvJC6Tx5W4va7N9UDB8+r5O362kRrttAB73pyebgAiD932Vn1hE9e31BT8Jq0+x1AEeKAl0lSKwf9AmnqnClSI/87kHjEJ2fVSLCGR93ss09lvjwaby+1bJKRZHToy5Rdto/fMVg4vn/vl4CxvqrIjELjAN1pqNM/0WXoJzopogobKSUxxGRoEN1DQgf4by30KvGxHjS71qAJvZ02N5F3ybGRug3v77MrmsQybhUBB9q4OY9QIDAQAB";
    String MERCHANT_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCK9Dm2HOgKBxErnLH+vyLicbqbtVfPYIO5ieeWYtn7hd56Z0ggi1PZGgZUeQeVxSqavQOaekO0ivt1YV+eP7snfxR4029Kx35+GpNhChJv/hwSI3gEdkW+VthZS3PqnWQXnhwgt+HcHwqkKEDXvkgzloMhwqtrO7Wa1qezzNspqOYov2bu23Rrc28osuUmp/htRwdM4WQ1C/28fhTBqIj+zP4LVW9yMtBs2t3fOKcmauYp1mbl2tzEgsbug7NlkX2se3LpMOZHqPFCDSIuQBJkKN7hq3DtVwsqXDtjcjSP01ILk4ymypMHKTQqcpMITyPx1Ew9rt9uWVkaRphR6manAgMBAAECggEAdFounQS1c/gY85jhHrwz3MN4i3uczKQ3iLwl9LJPbyLYeUxUhBPGoSr+k+tIY6vWkf2lr8ujN+GMz+w6Ycthzp/Fp6r8LPTBQZzFPeNuLuLr5HBYxfu3SLt1q8fXh2RFdNVLHkZ7VJOVmIGwUjceRDxLD5Dvcvprhf0qLg9ikQ2Cq00HKfKOPiJjQ7IlZ7C4U8aoI+yMv6iOO/Yd5FmiuNgxT9wsL1MFaLs2HTtkUGI/34XtJHWbjW5SzDnhxJJ9v6t0CUiO/XVzkJMCFDQDElolCrnE0KTIzhyfwiOpi+y2r0ETdbWEJzbvWSuZjqqkGF9PcwNWlV+Cb3kgCXEamQKBgQDZTfeCb0+I6gjD/YeAxxzigDkp/n2JnXeYRxrDV+cpt6bZ3rY/lKyFyfAmmcHZ6YfWuEZPTjW5gV7ib0RIpclyRuAC3gTLDh51iyz2DX4d4x2pS7RrIDZxSXgBcXgplGd1E2f6iWxEToUYJwxs8ztVfqIYDjYwQJr5vYzyXFPu0wKBgQCjspMl7ehB9CTqf/7cF2AhZRWIujolVVquhN6QEPWvVgaoGYHUfqgYYIgFCgQRleH9qDf7SvVq09f/OY3wHALxarlFhgpV1eGG59Aze9Abe6Gjl1xTJjwmfaDLcTxBIDLlciHrldiiGYN5q+q7vi/Clj4AngS1zLT9/EwneaJMXQKBgQCzUOMtfyzijj0tfciTO7taLPT+8kIljQtjXUl4aNEYW7QTlPRPAyBaRoklUTvl1g+B4A4k4yTyLw9dSvPN/wB8jH/N3eeWp8/6rx4q7HdlskSHrzDyNWK3mSiuCDRgG1ZEBf+Hfl8Y/rJVe+OFBDGyGw0nTJLqvmUkMIZeeFHKgQKBgDfgxjAFVOnmkA9oH7Q7Wnoryj0A7MY/q5O1EU/2Khdu8xjAlDvX/ENN+EBYGely01DF8k4aWUndBHc/yz6yISMNfi7IQZDpUHG/gaeBVRl8i0GPclmDzLaK3a90eM/2UMlXu1JytR8oPGP1Fkx98EmpBYMqbYstVH76d3hmIcuVAoGBAK2iRKWfr7/3kbXoiKs4VFZpVMXBbiMQCCrGY7hy2woyuiyJaBtIVL1ygsfNGGVThd3aHNBGo6daUqAK7Fa+xtHxzSMRQtBQuSIZNBoRys2ZLIoMG1LlcDoy4YhVOAO/nDYDBII7/Bb2mK99+xe+gY7/UJ+LP1vb9hfaohDQcHDX";

}
