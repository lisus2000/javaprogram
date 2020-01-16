package com.itheima.ssm.service;

import cn.itcast.domain.Orders;

import java.util.List;

public interface IOrdersService {
    /**
     * 查询所有的订单
     * @return
     */
    public List<Orders> findAll(int page,int size) throws Exception;

    Orders findById(String ordersId) throws Exception;
}
