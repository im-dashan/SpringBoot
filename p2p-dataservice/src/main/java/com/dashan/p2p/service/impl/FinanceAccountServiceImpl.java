package com.dashan.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dashan.p2p.mapper.user.FinanceAccountMapper;
import com.dashan.p2p.model.user.FinanceAccount;
import com.dashan.p2p.service.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = FinanceAccountService.class, version = "1.0.0", timeout = 15000)
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer userId) {
        return financeAccountMapper.selectFaByUid(userId);
    }
}
