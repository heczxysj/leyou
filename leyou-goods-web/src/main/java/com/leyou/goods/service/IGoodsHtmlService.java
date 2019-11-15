package com.leyou.goods.service;

public interface IGoodsHtmlService {

    void createHtml(Long spuId);

    void asyncExcute(Long spuId);

    void deleteHtml(Long id);
}
