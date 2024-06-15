package com.dashan.p2p.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000117693490";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDrSmtJ3n58fyPN+5tt+X+Z22EUuZtbx4+Fky6aGxnznHZRwwULD7BHx2jf+TkXyAsFF1xvIdeIaK0mPzpIzcMdpH6pokPh5/0Mb9i79O8U/JZq7iMoGwtttptgRSg27RFWvkPe5d7IOrlCyiYrfiek9dxoyT1MdPU4rR2EfD4iaKq8jlFgaYFx6Duk2Tlofy5aMa4LhoKlAl2FNEyN9o7fl2xUZCNj7992Vl4yWyZpwnn8a0tLBP7f3s+/kgkaHVQ1+NFeuIdNdnWvDsvW9Pe7P8NasQjbKgaOvdDxYncZKdA+RB75x2dLAoUKfObfxiv2+ChomCfI3SvFPreEN2vbAgMBAAECggEBAK7twR5GQJummqQFr+TvXaKxq1eRNqPhlh6k8jy4cW7goEbhStqSa2iyJJJ/46Z+n1LFr38gFfHggPqyf06pS6q6JUFQ1k8Z/tREQ0paK3Xa5KnTKmgi0zhaTuIgCGntjrqC8krBdARBRIMXb5FeVXMbJybwlpZFThs4+X0KMde5tF7VqHJEn5DdyshIiDjg0IH+MtTQj+XJKhDnSGYnb76k88n7AUt6Q25xmk424SS8jJYLeeL+egblyIOrqda1QJH+SQNy68SyiMg16Jcggf2SbxKsoKwuS7sT66QgXJaA7oZ0aHxdO4fWEJm+pbfnWH6wXR4KpRPJ4iYYk/4bt+ECgYEA+K4uwOqVxT+4LHGWHdL+LQP1KabULQ3NH4dNoQCs6YiD9hpvxntFXHZRbzdExMYYSe6fvjeLCNNddWDTcezIqbPd0d5UXSgoa42vhyo/DIpyltcTBZsqLNmD1Jim45F9Vd2CrJJgs83hb8+cfSSMvb16IRFWyn7UZnd5kk9ErFUCgYEA8jdYMPvCMqhFehQT1t/KjhxNGD+bIOlQL5Wx6Yrkhrq7WTq9DT5voTJj1uBDrMu2idJx1SuPKUeKikz8GPDENBssZ6Dcomc4kvltTY8ihngrs7TWzWGBP5vh9Hx7QzYktQRKFGTOoKGrEutQDftMLM+g5VLimzuJ18b6XmYw528CgYAdms7iFItD8jQAxnzyi/3KaiAzlzLHMk9rI3ZwZl4vZxsen0MTQ/Wb7lH/hXrgWhzmIExQHtikclU79gz2ERRKb6lGpMsyU+eL9lAhLTxYR9sgbfZLwo8gy7xQAORNTzK2mCpIVRzCCw59ex/MHh/LLtVtPl0wuiybDjog2uL4CQKBgDaOAp4dxlK0ldl2NjW6eBvkAfVJNj6GbloZZFYOw4QZAOOLGFOoUWpEODqIjpWX2IhYTu1Nf1Qz2d9mkY1ohTibOjgpAijOCO8JoPXMHGGfJXC3dX4SzRKvoIf5laXAxynCSpEnV2bUovyC5agQhsq9BSdBsOp30Yc5OOEUMUWfAoGBAIJHi0pEIWHaqoHyTbPD3IeaTS5L0oZv2IN0gbxcd/ge9/UmcEDWiEgcn/5ui+i0QxdSeJmtbEc+xL52KHFKEBmI9m+0Ae8qIuWJYtxlFgiUusihX5qnNOVtFE20RLGLhXZVHDL3cYDeYHs9CPoRg9VPKEcc5eQBv8y2xOQzl/g+";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg7XGhQT/uK+aDqT7h05e/kUqZjCSuhksuxMYB05zC9k1Gz7HVVhdv2X1GpUpbXzGOonE84cYkW8/D/6XsNRImyxUdZyhKy4M/3FPLudtdRU2yxG7Hi3oETfC4uMGZEGeuWzlU4OgZ4U8H+xQcRW3DWKivnhXf6gSSoZ9lEqA20baknJVz+2d8EpcYoPHjwTbXF1ePcMzoTw+yEgWQkbA5vi3JMUZAa3vQwTnA663ujpSGSMlu+jLZTf8GuXDVmvuD5vE91ZUf3c8aTo5Z6+y7I/Qjcj8m/t0o7SVB2BCM9a1VgOi724ccAnjWpcu2s/aq5PscddD4GWgmkYgbOlaGQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/p2p/loan/alipayBack/";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

