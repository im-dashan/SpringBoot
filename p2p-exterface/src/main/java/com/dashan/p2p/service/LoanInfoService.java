package com.dashan.p2p.service;

import com.dashan.p2p.model.loan.LoanInfo;
import com.dashan.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {

    /**
     * 查询平台历史平均年化收益率
     * @return
     */
    Double queryHistryAvgRate();

    /**
     * 查询新手宝产品
     * @param paramMap
     * @return
     */
    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 分月查询
     * @param paramMap
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap);

    /**
     * 根据id查询产品详情
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoById(Integer id);

}
