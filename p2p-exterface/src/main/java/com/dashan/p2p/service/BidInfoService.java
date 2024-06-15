package com.dashan.p2p.service;

import com.dashan.p2p.model.loan.BidInfo;
import com.dashan.p2p.model.vo.BidUserVO;

import java.util.List;
import java.util.Map;

public interface BidInfoService {

    /**
     * 平台累计成交金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品id查询产品最近的十条记录
     * @param loanId
     * @return
     */
    List<BidInfo> queryRecentlyBidInfoByLoanId(Integer loanId);

    /**
     * 投资
     * @param paramMap
     */
    void invest(Map<String, Object> paramMap) throws Exception;

    /**
     * 获取投资排行榜
     * @return
     */
    List<BidUserVO> investTop();
}
