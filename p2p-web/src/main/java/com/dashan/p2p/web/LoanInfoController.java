package com.dashan.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.model.loan.BidInfo;
import com.dashan.p2p.model.loan.LoanInfo;
import com.dashan.p2p.model.user.FinanceAccount;
import com.dashan.p2p.model.user.User;
import com.dashan.p2p.model.vo.BidUserVO;
import com.dashan.p2p.model.vo.PaginationVO;
import com.dashan.p2p.service.BidInfoService;
import com.dashan.p2p.service.FinanceAccountService;
import com.dashan.p2p.service.LoanInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class LoanInfoController {

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceAccountService financeAccountService;


    @RequestMapping("loan/loan")
    public String toLoan(Model model, @RequestParam(value = "ptype", required = false) Integer ptype, @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage) {


        // 分页查询产品列表，需要的参数：ptype（可选） 当前页currentPage，每页显示的条数pageSize
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int pageSize = 9;
        // 每页查询条数
        paramMap.put("pageSize", pageSize);
        paramMap.put("currentPage", (currentPage - 1) * pageSize);
        // 判断ptype为空不封装，不为空封装
        if (ObjectUtils.allNotNull(ptype)) {
            paramMap.put("productType", ptype);
        }

        // 分页查询
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoListByPage(paramMap);

        // 计算出总页数
        int totalPage = paginationVO.getTotalSize().intValue() / pageSize;
        int mod = paginationVO.getTotalSize().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        // List数据
        model.addAttribute("list", paginationVO.getDatas());

        // 总页数
        model.addAttribute("totalPage", totalPage);

        // 总条数
        model.addAttribute("totalSize", paginationVO.getTotalSize());

        // 当前页
        model.addAttribute("currentPage", currentPage);

        // 产品类型
        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("ptype", ptype);
        }


        // 产品投资排行榜
        List<BidUserVO> bidUserVOList = bidInfoService.investTop();
        model.addAttribute("bidUsers", bidUserVOList);
        return "loan";
    }


    @RequestMapping("loan/loanInfo")
    public String toLoanInfo(HttpServletRequest request,
                             Model model,
                             @RequestParam(value = "loanId", required = true) Integer loanId) {
        // 查询产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);
        model.addAttribute("loanInfo", loanInfo);

        // 查询该产品最近的十条投资记录（需要查询用户投资记录表和用户表）
        // 两表联查
        List<BidInfo> bidInfoList = bidInfoService.queryRecentlyBidInfoByLoanId(loanId);
        model.addAttribute("bidInfoList", bidInfoList);
        // 查询账户余额
        User user = (User) request.getSession().getAttribute(Constans.USER);
        if (ObjectUtils.allNotNull(user)){
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(user.getId());
            model.addAttribute("availableMoney", financeAccount.getAvailableMoney());
        }

        return "loanInfo";
    }
}
