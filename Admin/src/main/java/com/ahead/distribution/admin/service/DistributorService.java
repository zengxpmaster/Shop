package com.ahead.distribution.admin.service;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.common.entity.Distributor;

public interface DistributorService {
    Distributor findById(Long id);
    PageResult<Distributor> list(int page, int size, String keyword, Integer status);
    void updateStatus(Long id, Integer status);
}
