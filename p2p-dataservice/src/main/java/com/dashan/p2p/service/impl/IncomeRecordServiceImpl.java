package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.mapper.loan.BidInfoMapper;
import com.dashan.p2p.mapper.loan.IncomeRecordMapper;
import com.dashan.p2p.mapper.loan.LoanInfoMapper;
import com.dashan.p2p.mapper.user.FinanceAccountMapper;
import com.dashan.p2p.model.loan.BidInfo;
import com.dashan.p2p.model.loan.IncomeRecord;
import com.dashan.p2p.model.loan.LoanInfo;
import com.dashan.p2p.service.IncomeRecordService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Service(interfaceClass = IncomeRecordService.class, version = "1.0.0", timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    /**
     * 生成收益计划
     * @throws Exception
     */
    @Override
    @Transactional
    public void generateIncomePlan() throws Exception {

        // 生成收益计划

        // 1.遍历投标产品信息（条件：产品状态为1）List<LoanInfo>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByLoanId(1);

        for (LoanInfo loanInfo : loanInfoList) {
            // 2.得到每个已满标的产品，根据当前产品获取它的投资记录（List<BidInfo>）
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoByLoanId(loanInfo.getId());
            // 3.遍历投资记录，得到每一条投资记录
            for (BidInfo bidInfo : bidInfoList) {

                // 收益时间
                Date incomeDate = null;
                // 收益金额
                Double incomeMoney = null;

                // 4.根据投资记录和产品信息，生成一条新的收益记录（收益状态为0）
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);

                // 收益时间 = （满标时间 + 投资周期）
                if (loanInfo.getProductType() == Constans.PRODUCT_TYPE_X) {
                    // 新手宝 收益时间（Date）=（满标时间（Date）=周期（天 int））
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    // 收益金额 = 投资金额 * （利率（rate）/365/100）*周期（天）
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 365 / 100) * loanInfo.getCycle();
                } else {
                    // 优选产品和三标 收益时间（Date）=（满标时间（Date）=周期（月 int））
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    // 收益金额 = 投资金额 * （利率（rate）/365/100）*周期（天）
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 365 / 100) * loanInfo.getCycle() * 30;
                }
                incomeMoney = Math.round(incomeMoney * Math.pow(10, 2)) / Math.pow(10, 2);
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);
                int irRows = incomeRecordMapper.insertSelective(incomeRecord);
                if (irRows == 0) {
                    throw new Exception("生成收益记录失败");

                }
            }
            // 将当前产品的状态由1改为2
            loanInfo.setProductStatus(2);
            int rows = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if (rows == 0) {
                throw new Exception("产品状态由1改为2失败！");
            }
        }
        // 修改当前产品状态为2
    }

    /**
     * 返还收益
     */
    @Transactional
    @Override
    public void generateIncomeBack() throws Exception {
        // 查询出待返回收益的收益记录
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordListByIncomeStatusAndCurDate(0);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        // 遍历待返回收益的List列表
        for (IncomeRecord incomeRecord : incomeRecordList) {
            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());
            // 更新账户
            int faRows = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);
            if (faRows == 0) {
                throw new Exception("返还收益，更新账户余额失败");
            }

            // 返还收益后，将收益状态由 0 改成1
            IncomeRecord record = new IncomeRecord();
            record.setId(incomeRecord.getId());
            record.setIncomeStatus(1);
            int rows = incomeRecordMapper.updateByPrimaryKeySelective(record);
            if (rows == 0) {
                throw new Exception("返还收益后，返还收益状态由0改为1失败");
            }

        }



    }
}
