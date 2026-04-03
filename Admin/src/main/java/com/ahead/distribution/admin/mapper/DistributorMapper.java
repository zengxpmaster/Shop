package com.ahead.distribution.admin.mapper;

import com.ahead.distribution.common.entity.Distributor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DistributorMapper {
    Distributor findById(@Param("id") Long id);
    Distributor findByUserId(@Param("userId") Long userId);
    Distributor findByInviteCode(@Param("inviteCode") String inviteCode);
    List<Distributor> list(@Param("page") int page, @Param("size") int size,
                           @Param("keyword") String keyword, @Param("status") Integer status);
    int count(@Param("keyword") String keyword, @Param("status") Integer status);
    int insert(Distributor distributor);
    int update(Distributor distributor);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
