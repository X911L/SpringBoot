package com.xl.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xl.pojo.UserToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xl
 * @date 2021年10月26日 15:12
 */
@Mapper
public interface UserTokenDao extends BaseMapper<UserToken> {
}
