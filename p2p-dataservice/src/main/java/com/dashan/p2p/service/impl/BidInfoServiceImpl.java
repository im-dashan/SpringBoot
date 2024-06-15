package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.mapper.loan.BidInfoMapper;
import com.dashan.p2p.mapper.loan.LoanInfoMapper;
import com.dashan.p2p.mapper.user.FinanceAccountMapper;
import com.dashan.p2p.model.loan.BidInfo;
import com.dashan.p2p.model.loan.LoanInfo;
import com.dashan.p2p.model.user.User;
import com.dashan.p2p.model.vo.BidUserVO;
import com.dashan.p2p.service.BidInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = BidInfoService.class, version = "1.0.0", timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    /**
     * 同步代码块+双重验证
     *
     * @return
     */
    @Override
    public Double queryAllBidMoney() {

        // 修改RedisTemplate key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 从redis获取数据
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constans.ALL_BID_MONEY);
        // 如果redis数据为空
        if (!ObjectUtils.allNotNull(allBidMoney)) {
            /**
             * 同步代码块
             * 一个时间内只能有一个线程得到执行
             * 另一个线程必须等待当前线程执行完这个代码块以后才能执行该代码块
             */
            synchronized (this) {
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constans.ALL_BID_MONEY);
                // 再次判断
                if (!ObjectUtils.allNotNull(allBidMoney)) {
                    // 从数据库拿数据
                    allBidMoney = bidInfoMapper.selectAllBidMoney();
                    // 数据存入redis、设置数据过期时间，7天
                    redisTemplate.opsForValue().set(Constans.ALL_BID_MONEY, allBidMoney, 3, TimeUnit.DAYS);
                }
            }
        }
        return allBidMoney;
    }


    @Override
    public List<BidInfo> queryRecentlyBidInfoByLoanId(Integer loanId) {
        return bidInfoMapper.selectRecentlyBidInfoByLoanId(loanId);
    }

    /**
     * 投资业务功能的实现
     *
     * @param paramMap
     * @throws Exception
     */
    @Transactional  // 开启事务
    @Override
    public void invest(Map<String, Object> paramMap) throws Exception {
        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId = (Integer) paramMap.get("loanId");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");

        // 根据loanId，获取version字段
        LoanInfo info = loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version", info.getVersion());

        // 投资 (修改产品信息表(loanId、bidMoney)
        int loanInfoRows = loanInfoMapper.updateLoanInfoById(paramMap);
        if (loanInfoRows == 0) {
            throw new Exception("投资后修改产品信息表失败");
        }

        // 修改账户表（uid，bidMoney）
        int faRows = financeAccountMapper.updateFinanceAccountByUidAndBidMoney(paramMap);
        if (faRows == 0) {
            throw new Exception("投资后修改账户表失败！");
        }

        //新增用户投资信息表(loanId、uid、bidMoney))
        BidInfo bidInfo = new BidInfo();
        bidInfo.setUid(uid);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidStatus(1);
        bidInfo.setBidTime(new Date());
        bidInfo.setLoanId(loanId);
        int bidRows = bidInfoMapper.insertSelective(bidInfo);
        if (bidRows == 0) {
            throw new Exception("投资后修改账户表失败");
        }

        // 判断当前的产品剩余可投金额是否为0，如果是则将产品的状态由0改为1
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        if (loanInfo.getLeftProductMoney() == 0) {
            LoanInfo loanInfoDetail = new LoanInfo();
            loanInfoDetail.setId(loanId);
            loanInfo.setProductStatus(1);
            loanInfo.setProductFullTime(new Date());
            int rows = loanInfoMapper.updateByPrimaryKeySelective(loanInfoDetail);
            if (rows == 0) {
                throw new Exception("投资后产品状态失败");
            }
        }

        // 投资工程，记录投资排行榜
        // incrementScore累加,遇到相同的getPhone，bidMoney累加
        redisTemplate.opsForZSet().incrementScore(Constans.INVEST_TOP, phone, bidMoney);
    }

    /**
     * 获取投资排行榜
     * @return
     */
    @Override
    public List<BidUserVO> investTop() {
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(Constans.INVEST_TOP, 0, 5);
        // iterator迭代器
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = set.iterator();
        List<BidUserVO> list = new ArrayList<>();

        while (iterator.hasNext()) {
            BidUserVO bidUserVO = new BidUserVO();

            ZSetOperations.TypedTuple<Object> obj = iterator.next();
            Double score = obj.getScore();
            String value = (String) obj.getValue();

            bidUserVO.setPhone(value);
            bidUserVO.setBidMoney(score);
            list.add(bidUserVO);
        }


        return list;
    }
}
