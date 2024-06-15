package com.dashan.p2p.mapper.user;

import com.dashan.p2p.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**
     * 根据用户 id查询账户信息
     * @param userId
     * @return
     */
    FinanceAccount selectFaByUid(Integer userId);


    /**
     * 更新账户余额
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByUidAndBidMoney(Map<String, Object> paramMap);

    /**
     * 返还收益，更新账户余额
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByIncomeBack(Map<String, Object> paramMap);

    /**
     * 充值金额成功，更新账户余额
     * @param paramMap
     */
    void updateFinanceAccountByRecharge(Map<String, Object> paramMap);
}