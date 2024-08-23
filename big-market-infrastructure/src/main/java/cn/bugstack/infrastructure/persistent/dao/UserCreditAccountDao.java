package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserCreditAccount;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 用户积分账户dao
 * CreateTime: 2024-08-17
 */
@Mapper
public interface UserCreditAccountDao {

    int updateAddAmount(UserCreditAccount userCreditAccount);

    void insert(UserCreditAccount userCreditAccount);

    @DBRouter(key = "userId")
    UserCreditAccount queryUserCreditAccount(UserCreditAccount userCreditAccountReq);
}
