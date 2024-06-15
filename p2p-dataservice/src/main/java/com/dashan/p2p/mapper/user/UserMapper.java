package com.dashan.p2p.mapper.user;

import com.dashan.p2p.model.user.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 查询平台用户注册数量
     * @return
     */
    Integer seletAllUserCount();

    /**
     * 根据phone查询user
     * @param phone
     * @return
     */
    User selectUserByPhone(String phone);

    /**
     * 查询用户名和密码进行登录验证
     * @param phone
     * @param loginPassword
     * @return
     */
    User selectUserByPhoneAndPassword(String phone, String loginPassword);
}