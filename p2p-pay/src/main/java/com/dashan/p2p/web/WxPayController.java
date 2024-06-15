package com.dashan.p2p.web;

import com.dashan.p2p.util.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Controller
public class WxPayController {

    /**
     * 封装相关参数，请求微信支付接口
     *
     * @param body
     * @param out_trade_no
     * @param total_fee
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/api/wxpay")
    public @ResponseBody Object wxpay(@RequestParam(value = "body", required = true) String body, @RequestParam(value = "out_trade_no", required = true) String out_trade_no, @RequestParam(value = "total_fee", required = true) Double total_fee) throws Exception {

        //拼接一个xml格式的请求参数,根节点为xml
        //创建一个map集合的请求参数
        Map<String, String> requestDataMap = new HashMap<String, String>();
        requestDataMap.put("appid", "wx8a3fcf509313fd74");
        requestDataMap.put("mch_id", "1361137902");
        requestDataMap.put("nonce_str", WXPayUtil.generateNonceStr());
        requestDataMap.put("body", body);
        requestDataMap.put("out_trade_no", out_trade_no);

        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        int i = multiply.intValue();
        requestDataMap.put("total_fee", String.valueOf(i));
        requestDataMap.put("spbill_create_ip", "127.0.0.1");
        requestDataMap.put("notify_url", "http://localhost:8080/loan/wxpayNotify");
        requestDataMap.put("trade_type", "NATIVE");
        requestDataMap.put("product_id", out_trade_no);

        //生成签名值
        String signature = WXPayUtil.generateSignature(requestDataMap, "367151c5fd0d50f1e34a68a802d6bbca");
        requestDataMap.put("sign", signature);

        //将map集合的请求参数转换为xml格式的请求参数
        String requestDataXml = WXPayUtil.mapToXml(requestDataMap);

        //将请求参数传递给接口地址
        String responseDataXml = HttpClientUtils.doPostByXml("https://api.mch.weixin.qq.com/pay/unifiedorder", requestDataXml);

        //将xml格式的响应参数转换为map集合
        Map<String, String> responseDataMap = WXPayUtil.xmlToMap(responseDataXml);


        return responseDataMap;
    }
}
