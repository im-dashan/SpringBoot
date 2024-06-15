package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.mapper.loan.LoanInfoMapper;
import com.dashan.p2p.model.loan.LoanInfo;
import com.dashan.p2p.model.vo.PaginationVO;
import com.dashan.p2p.service.LoanInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = LoanInfoService.class, version = "1.0.0", timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    /**
     * 同步代码块+双重验证
     *
     * @return
     */
    @Override
    public Double queryHistryAvgRate() {

        // 修改RedisTemplate key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 从redis获取数据
        Double histryAvgRate = (Double) redisTemplate.opsForValue().get(Constans.HISTRY_AVG_RATE);
        // 如果redis数据为空
        if (!ObjectUtils.allNotNull(histryAvgRate)) {
            /**
             * 同步代码块
             * 一个时间内只能有一个线程得到执行
             * 另一个线程必须等待当前线程执行完这个代码块以后才能执行该代码块
             */
            synchronized (this) {
                histryAvgRate = (Double) redisTemplate.opsForValue().get(Constans.HISTRY_AVG_RATE);
                // 再次判断
                if (!ObjectUtils.allNotNull(histryAvgRate)) {
                    // 从数据库拿数据
                    histryAvgRate = loanInfoMapper.selectHistryAvgRate();
                    // 数据存入redis、设置数据过期时间，7天
                    redisTemplate.opsForValue().set(Constans.HISTRY_AVG_RATE, histryAvgRate, 7, TimeUnit.DAYS);
                }
            }
        }
        return histryAvgRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoListByProductType(paramMap);
    }

    /**
     * 分页查询
     * @param paramMap
     * @return
     */
    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> vo = new PaginationVO<>();
        // 查询List<LoanInfo>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductType(paramMap);

        // 查询totalSize
        Integer totalSize = loanInfoMapper.selectLoanInfoTotalSize(paramMap);

        vo.setDatas(loanInfoList);
        vo.setTotalSize(totalSize);

        return vo;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }
}
