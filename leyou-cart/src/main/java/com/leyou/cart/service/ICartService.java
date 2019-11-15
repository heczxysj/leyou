package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;

import java.util.List;

public interface ICartService {
    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateNum(Cart cart);

    void deleteCart(String skuId);
}
