package com.ahead.distribution.admin.service.impl;

import com.ahead.distribution.admin.dto.PageResult;
import com.ahead.distribution.admin.mapper.DistributorMapper;
import com.ahead.distribution.admin.service.DistributorService;
import com.ahead.distribution.common.entity.Distributor;
import com.ahead.distribution.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributorServiceImpl implements DistributorService {

    @Autowired
    private DistributorMapper distributorMapper;

    @Override
    public Distributor findById(Long id) {
        Distributor distributor = distributorMapper.findById(id);
        if (distributor == null) {
            throw new BusinessException(404, "Distributor not found");
        }
        return distributor;
    }

    @Override
    public PageResult<Distributor> list(int page, int size, String keyword, Integer status) {
        int offset = (page - 1) * size;
        List<Distributor> distributors = distributorMapper.list(offset, size, keyword, status);
        int total = distributorMapper.count(keyword, status);
        return new PageResult<>(distributors, total, page, size);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Distributor existing = distributorMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(404, "Distributor not found");
        }
        distributorMapper.updateStatus(id, status);
    }
}
