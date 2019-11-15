package com.leyou.cart.service.impl;

import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.ICartService;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.rmi.runtime.Log;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "user:cart:";

    @Override
    public void addCart(Cart cart) {

        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //查询购物车记录
        BoundHashOperations<String, Object, Object> ops = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getUsername());


        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();

        //判断当前的商品是否在购物车中
        if(ops.hasKey(key)){
            //在 更新数量
            String cartJson = ops.get(key).toString();
            cart = JsonUtils.parse(cartJson,Cart.class);
            cart.setNum(cart.getNum() + num);
        }else{
            //不在 新增购物车
            Sku sku = this.goodsClient.querySkuBySkuId(cart.getSkuId());
            cart.setUserId(userInfo.getId());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());
        }
        ops.put(key,JsonUtils.serialize(cart));


    }

    @Override
    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();

        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getUsername())){
            return null;
        }
        //获取用户的购物车信息
        BoundHashOperations<String, Object, Object> ops = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getUsername());

        List<Object> cartsJson = ops.values();

        if (CollectionUtils.isEmpty(cartsJson)){
            return null;
        }

        return cartsJson.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(),Cart.class)).collect(Collectors.toList());

    }

    @Override
    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断用户是否有购物车记录
        if(!this.redisTemplate.hasKey(KEY_PREFIX+userInfo.getUsername())){
            return;
        }
        Integer num = cart.getNum();

        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> ops = this.redisTemplate.boundHashOps(KEY_PREFIX + userInfo.getUsername());

        String cartJson = ops.get(cart.getSkuId().toString()).toString();

        cart = JsonUtils.parse(cartJson,Cart.class);

        cart.setNum(num);

        ops.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    @Override
    public void deleteCart(String skuId) {
        UserInfo user = LoginInterceptor.getUserInfo();

        BoundHashOperations<String, Object, Object> ops = this.redisTemplate.boundHashOps(KEY_PREFIX + user.getUsername());

        ops.delete(skuId);
    }
}
