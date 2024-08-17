package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.UserAwardRecord;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: chs
 * Description: 用户中将记录Dao
 * CreateTime: 2024-08-06
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface UserAwardRecordDao {

    void insert(UserAwardRecord userAwardRecord);

    int setStatusCompleted(UserAwardRecord userAwardRecord);

}
