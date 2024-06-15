package com.dashan.p2p.timer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dashan.p2p.service.IncomeRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TimeManager {

    @Reference(interfaceClass = IncomeRecordService.class, version = "1.0.0", check = false, timeout = 15000)
    private IncomeRecordService incomeRecordService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomePlan() throws Exception {
        System.out.println("======开始生成收益计划======");
        // 生成收益计划
        incomeRecordService.generateIncomePlan();

        System.out.println("======结束生成收益计划======");
    }


    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBack() throws Exception {
        System.out.println("======开始返还收益======");
        // 返还收益
        incomeRecordService.generateIncomeBack();

        System.out.println("======结束返还收益======");
    }
}
