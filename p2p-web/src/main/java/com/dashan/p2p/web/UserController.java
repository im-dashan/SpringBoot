package com.dashan.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.model.user.FinanceAccount;
import com.dashan.p2p.model.user.User;
import com.dashan.p2p.service.FinanceAccountService;
import com.dashan.p2p.service.RedisService;
import com.dashan.p2p.service.UserService;
import com.dashan.p2p.util.HttpClientUtils;
import com.dashan.p2p.util.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;

    @Reference(interfaceClass = RedisService.class, version = "1.0.0", check = false, timeout = 15000)
    private RedisService redisService;

    @Reference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceAccountService financeAccountService;

    /**
     * 调转到注册页面
     * @return
     */
    @RequestMapping("/loan/page/register")
    public String toRegister() {
        return "register";
    }

    @RequestMapping("/loan/checkPhone")
    @ResponseBody
    public Map<Object, Object> checkPhone(@RequestParam("phone") String phone){
        User user = userService.queryUserByPhone(phone);
        // 根据手机号查询数据库得结果过User
        if (!ObjectUtils.allNotNull(user)){
            return Result.success();

        }else {
            return Result.error("手机号:" + phone + "已经被注册！");
        }
    }


    /**
     * 实现注册
     * @param request
     * @param phone
     * @param loginPassword
     * @param messageCode
     * @return
     */
    @RequestMapping("/loan/register")
    @ResponseBody
    public Result register(HttpServletRequest request,
                           @RequestParam(value = "phone", required = true) String phone,
                           @RequestParam(value = "loginPassword", required = true) String loginPassword,
                           @RequestParam(value = "messageCode", required = true) String messageCode) {

            try {
                // 先判断输入的验证码是否和生成的验证码是否一致
                String redisCode = redisService.get(phone);
                if (!StringUtils.equals(redisCode, messageCode)){
                    return Result.error("验证码错误！");
                }
                User user = userService.regiser(phone, loginPassword);
                request.getSession().setAttribute(Constans.USER, user);
            }catch (Exception e){
                return Result.error("注册失败！");
        }
            return Result.success();
    }

    /**
     * 发送短信验证码
     * @param phone
     * @return
     * @throws Exception
     */
    @RequestMapping("/loan/sendMessageCode")
    @ResponseBody
    public Result sendMessageCode(@RequestParam(value = "phone", required = true) String phone) throws Exception {
        // 调用生成随机数的方法
        String messageCode = this.randomNum(6);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appkey","97eb7ca808cb197bea918dd09e779dc8");
        paramMap.put("mobile",phone);
        paramMap.put("content","【创信】你的验证码是：" + messageCode + "，3分钟内有效！");

//        String resultJson = HttpClientUtils.doGet("https://way.jd.com/chuangxin/dxjk", paramMap);

        // 假数据
        String resultJson = "{\"code\":\"10000\"," +
                "\"charge\":false," +
                "\"remain\":0," +
                "\"msg\":\"查询成功\"," +
                "\"result\":{" +
                "\"ReturnStatus\":\"Success\"," +
                "\"Message\":\"ok\"," +
                "\"RemainPoint\":72175," +
                "\"TaskID\":78005649," +
                "\"SuccessCounts\":1}," +
                "\"requestId\":\"6d4647f01b9947dd863306e2080ff643\"" +
                "}";

        JSONObject json = JSONObject.parseObject(resultJson);
        String  code = json.getString("code");

        if (!StringUtils.equals(code, "10000")){
            return Result.error("验证码发送失败！稍后再试！");
        }

        // 适用于json数据
        String returnStatus = json.getJSONObject("result").getString("ReturnStatus");

//        // 适用于xml
//        String result = json.getString("result");
//        Document document = DocumentHelper.parseText(result);
//        Node node = document.selectSingleNode("/returnsms/returnstatus[1]");
//        String returnStatus = node.getText();

        if (!StringUtils.equals(returnStatus, "Success")){
            return Result.error("returnstatus的值不是Success，错了！");
        }

        // 将验证码保存到redis中并设置超时时间
        redisService.put(phone, messageCode, 1);
        System.out.println("您的验证码为" + messageCode);

        // 发送短信成功
        return Result.success();

    }


    /**
     * 跳转到实名认证页面
     */
    @RequestMapping("/loan/realName")
    public String toRealName(){
        return "realName";
    }

    /**
     * 实名认证和更新user表信息
     * @param request
     * @param phone
     * @param realName
     * @param idCard
     * @param messageCode
     * @return
     * @throws Exception
     */
    @RequestMapping("/loan/checkRealName")
    @ResponseBody
    public Result checkRealName(HttpServletRequest request,
                                @RequestParam(value = "phone", required = true) String phone,
                                @RequestParam(value = "realName", required = true) String realName,
                                @RequestParam(value = "idCard", required = true) String idCard,
                                @RequestParam(value = "messageCode", required = true) String messageCode) throws Exception {
        // 从rides获取验证码
        String redisCode = redisService.get(phone);
        if (!StringUtils.equals(messageCode, redisCode)){
            return Result.error("验证码错误！");
        }
        // 进行实名认证
        Map<String, String> params = new HashMap<String, String>();
        params.put("appkey", "c2b572493c972ca7e5bdcc970cb0707c");
        params.put("cardNo", idCard);
        params.put("realName", realName);

//        String resultJson = HttpClientUtils.doGet("https://way.jd.com/hl/idcheck", params);

        // 假数据
        String resultJson = "{\"code\":\"10000\"," +
                "\"charge\":false," +
                "\"remain\":0," +
                "\"msg\":\"查询成功\"," +
                "\"result\":{" +
                "\"error_code\":0," +
                "\"reason\":\"成功\"," +
                "\"result\":{" +
                "\"realname\":\"大*\"," +
                "\"idcard\":\"613431************\"," +
                "\"isok\":true," +
                "\"IdCardInfor\":{" +
                "\"province\":\"广东省\"," +
                "\"city\":\"广东省\"," +
                "\"district\":\"成都市\"," +
                "\"area\":\"地址是假的，这是数据\"," +
                "\"sex\":\"男\"," +
                "\"birthday\":\"1999-4-20\"}}}," +
                "\"requestId\":\"b921f88305d9415aab907e3d39377a66\"" +
                "}";

        JSONObject jsonObject = JSONObject.parseObject(resultJson);
        String code = jsonObject.getString("code");
        if (!StringUtils.equals(code, "10000")){
            return Result.error("验证失败！");
        }

        String d1 = jsonObject.getString("result");
        JSONObject j1 = JSONObject.parseObject(d1);
        String d2 = j1.getString("result");
        JSONObject j2 = JSONObject.parseObject(d2);
        Boolean isok = j2.getBoolean("isok");
        if (!isok){
            return Result.error("认证失败！");
        }

        //实名认证成功更新数据库（u_user）
        User sessionUser = (User) request.getSession().getAttribute(Constans.USER);
        User user = new User();
        user.setId(sessionUser.getId());
        user.setName(realName);
        user.setIdCard(idCard);
        int rows = userService.modifyUser(user);
        if (rows == 0){
            throw new Exception("实名认证都更新user表失败！");
        }

        // 更新session的user对象，为了方便以后投资
        User userDetail = userService.queryUserByPhone(phone);
        request.getSession().setAttribute(Constans.USER, userDetail);

        return Result.success();
    }


    /**
     * 查询该用户的账户余额
     * @param request
     * @return
     */
    @RequestMapping("/loan/availableMoney")
    @ResponseBody
    public FinanceAccount availableMoney(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute(Constans.USER);
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());

        return financeAccount;
    }


    /**
     * 退出登录
     * @param request
     * @return
     */
    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request){
        // 删除session中的user
        request.getSession().removeAttribute(Constans.USER);

        return "redirect:/index";
    }


    /**
     * 调转到登录页面
     * @return
     */
    @RequestMapping("loan/page/login")
    public String toLogin(){
        return "login";
    }


    /**
     * 用户登录
     * @param request
     * @param phone
     * @param loginPassword
     * @param messageCode
     * @return
     */
    @RequestMapping("loan/login")
    @ResponseBody
    public Result login(HttpServletRequest request,
                        @RequestParam(value = "phone", required = true) String phone,
                        @RequestParam(value = "loginPassword", required = true) String loginPassword,
                        @RequestParam(value = "messageCode", required = true) String messageCode) {

        try {
            // 先对验证码进行验证,key=phone
            String redisCode = redisService.get(phone);
            if (!StringUtils.equals(messageCode, redisCode)) {
                return Result.error("验证码错误！");
            }
            // 查询数据库对账号和密码进行验证
            User user = userService.queryUserByPhoneAndPassword(phone, loginPassword);

            if (!ObjectUtils.anyNotNull(user)) {
                return Result.error("用户名或者密码错误！");
            }
            // 登录成功,将user对象保存到Session对象中
            request.getSession().setAttribute(Constans.USER, user);
        } catch (Exception e) {
            return Result.error("登录失败！");
        }
        return Result.success();
    }


    /**
     * 跳转到个人中心
     * @return
     */
    @RequestMapping("loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model) {
        // 获取当前用户信息
        User user = (User) request.getSession().getAttribute(Constans.USER);
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
        model.addAttribute("availableMoney", financeAccount.getAvailableMoney());
        model.addAttribute(Constans.USER, user);

        return "myCenter";
    }


    /**
     * 生成随机验证码
     */
    private String randomNum(int n){
        StringBuffer buffer = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int num = random.nextInt(10);
            buffer.append(num);
        }
        return buffer.toString();
    }
}
