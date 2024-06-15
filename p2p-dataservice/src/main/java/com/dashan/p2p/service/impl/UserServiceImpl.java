package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.constans.Constans;
import com.dashan.p2p.mapper.user.FinanceAccountMapper;
import com.dashan.p2p.mapper.user.UserMapper;
import com.dashan.p2p.model.user.FinanceAccount;
import com.dashan.p2p.model.user.User;
import com.dashan.p2p.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Service(interfaceClass = UserService.class, version = "1.0.0", timeout = 15000)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Integer queryAllUserCount() {

        // 修改RedisTemplate key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 从redis获取数据
        Integer allUserCount = (Integer) redisTemplate.opsForValue().get(Constans.ALL_USER_COUNT);
        // 如果redis数据为空
        if (!ObjectUtils.allNotNull(allUserCount)) {
            /**
             * 同步代码块
             * 一个时间内只能有一个线程得到执行
             * 另一个线程必须等待当前线程执行完这个代码块以后才能执行该代码块
             */
            synchronized (this) {
                allUserCount = (Integer) redisTemplate.opsForValue().get(Constans.ALL_USER_COUNT);
                // 再次判断
                if (!ObjectUtils.allNotNull(allUserCount)) {
                    // 从数据库拿数据
                    allUserCount = userMapper.seletAllUserCount();
                    // 数据存入redis、设置数据过期时间，7天
                    redisTemplate.opsForValue().set(Constans.ALL_USER_COUNT, allUserCount, 3, TimeUnit.DAYS);
                }
            }
        }
        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }


    /**
     * 注册用户
     * @param phone
     * @param loginPassword
     * @return
     */
    @Transactional  //开启事务
    @Override
    public User regiser(String phone, String loginPassword) throws Exception {
        // 给 u_user表中添加一条数据
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());
        int userRows = userMapper.insertSelective(user);
        if (userRows == 0){
            throw new Exception("注册用户失败！");
        }
//        // 通过手机号 查询user表数据
//        User userDetail = userMapper.selectUserByPhone(phone);

        // 给 u_finanaceaccount表中添加一条数据
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setUid(user.getId());
        financeAccount.setAvailableMoney(888.0);
        int acRows = financeAccountMapper.insertSelective(financeAccount);
        if (acRows == 0){
            throw new Exception("开新账户失败！");
        }
        return user;
    }

    /**
     * 修改用户资料姓名和身份证号
     * @param user
     * @return
     */
    @Override
    public int modifyUser(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }


    /**
     * 验证登录，查询账号和密码
     * @param phone
     * @param loginPassword
     * @return
     */
    @Override
    public User queryUserByPhoneAndPassword(String phone, String loginPassword) {
        User user = userMapper.selectUserByPhoneAndPassword(phone, loginPassword);
        // 更新登录时间
        User userDetail = new User();
        userDetail.setId(user.getId());
        userDetail.setLastLoginTime(new Date());
        userMapper.updateByPrimaryKeySelective(userDetail);
        return user;
    }
}