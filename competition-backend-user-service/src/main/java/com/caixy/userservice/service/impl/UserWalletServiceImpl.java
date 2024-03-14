package com.caixy.userservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caixy.model.entity.UserWallet;
import com.caixy.userservice.service.UserWalletService;
import com.caixy.userservice.mapper.UserWalletMapper;
import org.springframework.stereotype.Service;

/**
* @author CAIXYPROMISE
* @description 针对表【user_wallet】的数据库操作Service实现
* @createDate 2024-03-14 18:38:16
*/
@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet>
    implements UserWalletService{

}




