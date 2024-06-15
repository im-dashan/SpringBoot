package com.dashan.p2p.mapper.loan;

import com.dashan.p2p.model.loan.RechargeRecord;

import java.util.Map;

public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);


    /**
     * 工具订单号修改订单状态
     * @param paramMap
     */
    void updateStatusByRechargeNo(Map<String, Object> paramMap);
}