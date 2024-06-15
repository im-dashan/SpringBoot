package com.dashan.p2p.service;

import com.dashan.p2p.model.user.User;

public interface UserService {

    /**
     * 查询平台用户注册数量
     * @return
     */
    Integer queryAllUserCount();

    /**
     * 根据phone查询user对象
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 注册用户
     * @param phone
     * @param loginPassword
     * @return
     */
    User regiser(String phone, String loginPassword) throws Exception;


    /**
     * user表中更新数据
     * @param user
     * @return
     */
    int modifyUser(User user);


    /**
     * 用户登录，查询账号和密码
     * @param phone
     * @param loginPassword
     * @return
     */
    User queryUserByPhoneAndPassword(String phone, String loginPassword);
}
