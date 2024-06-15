package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.mapper.loan.RechargeRecordMapper;
import com.dashan.p2p.mapper.user.FinanceAccountMapper;
import com.dashan.p2p.model.loan.RechargeRecord;
import com.dashan.p2p.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

@Component
@Service(interfaceClass = RechargeRecordService.class, version = "1.0.0", timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public int saveRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    /**
     * 使用redis生成一个唯一的数字
     * @return
     */
    @Override
    public Long getOnlyNumber() {
        return redisTemplate.opsForValue().increment(Constans.ONLY_NUMBER, 1);
    }

    /**
     * 工具订单号修改订单状态
     * @param out_trade_no
     */
    @Override
    public void modifyRechargeStatusByRechargeNo(String out_trade_no) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("out_trade_no", out_trade_no);
        paramMap.put("rechargeStatus", 2);
        rechargeRecordMapper.updateStatusByRechargeNo(paramMap);
    }

    /**
     * 处理支付宝充值成功后的结果
     * 1.修改充值记录表的订单状态
     * 2.修改账户余额
     * 开启事务
     * @param paramMap
     */
    @Transactional
    @Override
    public void recharge(Map<String, Object> paramMap) {
        // 1.修改充值记录表的订单状态
        rechargeRecordMapper.updateStatusByRechargeNo(paramMap);
        //2.修改账户余额
        financeAccountMapper.updateFinanceAccountByRecharge(paramMap);


    }


}
