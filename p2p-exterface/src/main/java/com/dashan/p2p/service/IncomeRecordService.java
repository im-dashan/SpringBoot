package com.dashan.p2p.service;

public interface IncomeRecordService {

    /**
     * 生成收益计划
     */
    void generateIncomePlan() throws Exception;

    /**
     * 返还收益
     */
    void generateIncomeBack() throws Exception;
}
