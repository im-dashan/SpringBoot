package com.dashan.p2p.service;

import com.dashan.p2p.model.user.FinanceAccount;

public interface FinanceAccountService {

    /**
     * 根据用户id查询账户表
     * @param id
     * @return
     */
    FinanceAccount queryFinanceAccountByUid(Integer userId);
}
