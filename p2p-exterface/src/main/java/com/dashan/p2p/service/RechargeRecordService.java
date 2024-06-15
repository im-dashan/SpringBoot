package com.dashan.p2p.service;

import com.dashan.p2p.model.loan.RechargeRecord;

import java.util.Map;

public interface RechargeRecordService {
    /**
     * 创建充值记录
     * @param rechargeRecord
     * @return
     */
    int saveRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 使用redis生成一个唯一的数字
     * @return
     */
    Long getOnlyNumber();

    /**
     * 工具订单号修改订单状态
     * @param out_trade_no
     */
    void modifyRechargeStatusByRechargeNo(String out_trade_no);

    /**
     * 处理支付宝充值成功后的结果
     * @param paramMap
     */
    void recharge(Map<String, Object> paramMap);
}
