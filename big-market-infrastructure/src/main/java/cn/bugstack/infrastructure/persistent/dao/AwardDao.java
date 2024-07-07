package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: chs
 * @CreateTime: 2024-07-05
 * @Description: 奖品Dao
 * @Version: 1.0
 */
@Mapper
public interface AwardDao {
    List<Award> queryAwardList();
}
