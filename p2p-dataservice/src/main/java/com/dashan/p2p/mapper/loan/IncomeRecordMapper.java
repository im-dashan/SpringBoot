package com.dashan.p2p.mapper.loan;

import com.dashan.p2p.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);


    /**
     * 查询待返还的收益的收益记录
     * @param incomeStatus
     * @return
     */
    List<IncomeRecord> selectIncomeRecordListByIncomeStatusAndCurDate(int incomeStatus);
}