package com.dashan.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.model.loan.LoanInfo;
import com.dashan.p2p.model.vo.PaginationVO;
import com.dashan.p2p.service.BidInfoService;
import com.dashan.p2p.service.LoanInfoService;
import com.dashan.p2p.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class IndexController {

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;


    /**
     * 展示首页内容
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String toIndex(Model model) {

        /**
         * 平台历史平均年化收益率
         */
        Double histryAvgRate = loanInfoService.queryHistryAvgRate();
        model.addAttribute(Constans.HISTRY_AVG_RATE, histryAvgRate);

        /**
         * 平台用户注册数量
         */
        Integer allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constans.ALL_USER_COUNT, allUserCount);

        /**
         * 平台累计成交金额
         */
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constans.ALL_BID_MONEY, allBidMoney);

        /**
         * 查询新手宝产品
         * 查询产品 参数(product_type=0,1,2  limit 0,1 0,4 0,8)
         * 返回值==>>List<LoanInfo>
         */
        Map<String, Object> paramMap = new HashMap<String, Object>();
        //产品类型
        paramMap.put("productType", Constans.PRODUCT_TYPE_X);
        //limit查询(0,1)
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 1);
        List<LoanInfo> loanInfoXList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute(Constans.LOAN_INFO_X_LIST, loanInfoXList);

        /**
         * 查询优选产品
         */
        //产品类型
        paramMap.put("productType", Constans.PRODUCT_TYPE_Y);
        //limit查询(0,4)
        paramMap.put("pageSize", 4);
        List<LoanInfo> loanInfoYList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute(Constans.LOAN_INFO_Y_LIST, loanInfoYList);

        /**
         * 查询散标产品
         */
        //产品类型
        paramMap.put("productType", Constans.PRODUCT_TYPE_S);
        //limit查询(0,8)
        paramMap.put("pageSize", 8);
        List<LoanInfo> loanInfoSList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute(Constans.LOAN_INFO_S_LIST, loanInfoSList);

        return "index";
    }
}