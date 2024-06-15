package com.dashan.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.dashan.p2p.config.AlipayConfig;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.model.loan.RechargeRecord;
import com.dashan.p2p.model.user.User;
import com.dashan.p2p.service.RechargeRecordService;
import com.dashan.p2p.util.DateUtils;
import com.dashan.p2p.util.HttpClientUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class RechargeRecordController {

    @Reference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false, timeout = 15000)
    private RechargeRecordService rechargeRecordService;


    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge() {
        return "toRecharge";
    }


    /**
     * 支付宝充值
     * @param request
     * @param model
     * @param rechargeMoney
     * @return
     */
    @RequestMapping("/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request,
                                   Model model,
                                   Double rechargeMoney) {

        String rechargeNo = "";
        Date rechargeTime = new Date();

        try {
            User user = (User) request.getSession().getAttribute(Constans.USER);
            // 1.生成订单号
            rechargeNo = DateUtils.getTimestamp() + rechargeRecordService.getOnlyNumber();

            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUid(user.getId());
            rechargeRecord.setRechargeNo(rechargeNo);
            rechargeRecord.setRechargeStatus("0");
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeTime(rechargeTime);
            rechargeRecord.setRechargeDesc("支付宝充值");
            // 保存充值记录
            int rows = rechargeRecordService.saveRechargeRecord(rechargeRecord);
            if (rows == 0) {
                throw new Exception("添加充值记录");
            }

        } catch (Exception e) {
            // 充值失败

            model.addAttribute("trade_msg", "支付宝充值失败");
            return "toRechargeBack";
        }

        model.addAttribute("rechargeNo", rechargeNo);
        model.addAttribute("rechargeMoney", rechargeMoney);
        model.addAttribute("rechargeDesc", "支付宝充值");

        return "p2pToAlipay";
    }

    @RequestMapping("/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,
                             @RequestParam(value = "out_trade_no", required = true) String out_trade_no,
                             @RequestParam(value = "trade_no", required = true) String trade_no,
                             @RequestParam(value = "total_amount", required = true) String total_amount) throws Exception {

        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            // 查询下单接口 alipay.trade.query
            Map<String, Object> paramMap =new HashMap<>();
            paramMap.put("out_trade_no", out_trade_no);
            String resultJson = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

            JSONObject jsonObject = JSONObject.parseObject(resultJson);
            JSONObject responseJson = jsonObject.getJSONObject("alipay_trade_query_response");
            String code = responseJson.getString("code");

            if(!StringUtils.equals(code, "10000")){
                return  "redirect://p2p/loan/toRechargeBack";
            }

            String trade_status = responseJson.getString("trade_status");
            // WAIT_BUYER_PAY（交易创建，等待买家付款）
            // TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
            // TRADE_SUCCESS（交易支付成功）
            // TRADE_FINISHED（交易结束，不可退款）
            if (StringUtils.equals(trade_status, "TRADE_CLOSED")){
                // 交易失败，将充值状态改为2
                // 参数（recharge_no  =  out_trade_no）
                rechargeRecordService.modifyRechargeStatusByRechargeNo(out_trade_no);
                return  "redirect://p2p/loan/toRechargeBack";
            }
            if (StringUtils.equals(trade_status, "TRADE_SUCCESS")){
                // 交易成功，扣除用户的账户余额，
                // 充值状态由0改为1，
                // 进入个人中心
                //参数（out_trade_no，total_amount，uid）

                User user = (User) request.getSession().getAttribute(Constans.USER);
                Map<String, Object> param = new HashMap<>();
                param.put("out_trade_no", out_trade_no);
                param.put("total_amount", total_amount);
                param.put("uid", user.getId());
                param.put("rechargeStatus", 1);

                rechargeRecordService.recharge(param);
                return "redirect:/loan/myCenter";
            }
        }else {
            System.out.println("验签失败");
        }
        return "redirect:/loan/myCenter";
    }




    /**
     * 微信支付
     *
     * @param rechargeMoney
     * @return
     */
    @RequestMapping("/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request, Model model, Double rechargeMoney) {

        String rechargeNo = "";
        Date rechargeTime = new Date();


        try {
            User user = (User) request.getSession().getAttribute(Constans.USER);
            // 1.生成订单号
            rechargeNo = DateUtils.getTimestamp() + rechargeRecordService.getOnlyNumber();

            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUid(user.getId());
            rechargeRecord.setRechargeNo(rechargeNo);
            rechargeRecord.setRechargeStatus("0");
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeTime(rechargeTime);
            rechargeRecord.setRechargeDesc("微信充值");
            // 保存充值记录
            int rows = rechargeRecordService.saveRechargeRecord(rechargeRecord);
            if (rows == 0) {
                throw new Exception("添加充值记录");
            }

        } catch (Exception e) {
            // 充值失败

            model.addAttribute("trade_msg", "微信充值失败");
            return "toRechargeBack";
        }
        model.addAttribute("rechargeNo", rechargeNo);
        model.addAttribute("rechargeMoney", rechargeMoney);
        model.addAttribute("rechargeTime", rechargeTime);

        return "showQRcode";
    }


    @RequestMapping(value = "/loan/generateQRcode")
    public void generateQRcode(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "rechargeNo", required = true) String rechargeNo, @RequestParam(value = "rechargeMoney", required = true) Double rechargeMoney) throws Exception {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("body", "微信扫码支付");
        paramMap.put("out_trade_no", rechargeNo);
        paramMap.put("total_fee", rechargeMoney);

        // 调用pay工程统一下单API接口
        String jsonString = HttpClientUtils.doPost("http://localhost:9090/api/wxpay", paramMap);

        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        String returnCode = jsonObject.getString("return_code");

        // 判断通信标识
        if (!StringUtils.equals("SUCCESS", returnCode)) {
            response.sendRedirect(request.getContextPath() + "/loan/toRechargeBack");
        }

        String resultCode = jsonObject.getString("result_code");
        if (!StringUtils.equals("SUCCESS", resultCode)) {
            response.sendRedirect(request.getContextPath() + "/loan/toRechargeBack");
        }

        // 获取code url
        String code_url = jsonObject.getString("code_url");

        // 将code_url 生成一个二维码图片
        Map<EncodeHintType, Object> encodeHintTypeObjectMap = new HashMap<>();
        encodeHintTypeObjectMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE, 200, 200, encodeHintTypeObjectMap);

        OutputStream outputStream = response.getOutputStream();
        // 将矩阵转换成流对象
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);

        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping("/loan/wxpayNotify")
    public String wxpayNotify(){
        // 处理微信支付结果
        return "0";
    }
}
