package com.dashan.p2p.mapper.loan;

import com.dashan.p2p.model.loan.BidInfo;

import java.util.List;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    /**
     * 查询平台累计成交金额
     * @return
     */
    Double selectAllBidMoney();


    /**
     * 根据产品id查询产品最近十条记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectRecentlyBidInfoByLoanId(Integer loanId);

    /**
     * 根据产品id查询出 投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectBidInfoByLoanId(Integer loanId);
}