package com.dashan.p2p.mapper.loan;

import com.dashan.p2p.model.loan.LoanInfo;

import java.util.List;
import java.util.Map;

public interface LoanInfoMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    /**
     * 查询平台历史平均年化收益率
     *
     * @return
     */
    Double selectHistryAvgRate();

    /**
     * 根据产品类型查询产品列表(查询新手宝产品)
     *
     * @param paramMap
     * @return
     */
    List<LoanInfo> selectLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 查询总记录数（总条数）
     *
     * @return
     */
    Integer selectLoanInfoTotalSize(Map<String, Object> paramMap);

    /**
     * 更新产品剩余可投金额
     *
     * @param paramMap
     * @return
     */
    int updateLoanInfoById(Map<String, Object> paramMap);

    /**
     * 根据产品状态查询出已满标的状态
     *
     * @param productStatus
     * @return
     */
    List<LoanInfo> selectLoanInfoListByLoanId(int productStatus);

}
