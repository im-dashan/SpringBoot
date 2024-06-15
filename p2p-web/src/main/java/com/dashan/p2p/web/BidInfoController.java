package com.dashan.p2p.web;


import com.alibaba.dubbo.config.annotation.Reference;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.model.user.User;
import com.dashan.p2p.service.BidInfoService;
import com.dashan.p2p.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BidInfoController {

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;


    @RequestMapping("/loan/invest")
    @ResponseBody
    public Result invest(HttpServletRequest request,
                         @RequestParam(value = "loanId", required = true) Integer loanId,
                         @RequestParam(value = "bidMoney", required = true) Double bidMoney) {

        try {
            // 投资（需要修改的产品信息表（loanId，bidMoney） 新增用户投资信息表（loanId，uid，bidMoney））
            // 修改账户表（uid，bidMoney）

            // session中获取uid
            User user = (User) request.getSession().getAttribute(Constans.USER);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("uid", user.getId());
            paramMap.put("loanId", loanId);
            paramMap.put("bidMoney", bidMoney);
            paramMap.put("phone", user.getPhone());
            bidInfoService.invest(paramMap);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("投资失败，请稍后再试！");
        }
        return Result.success();
    }
}
