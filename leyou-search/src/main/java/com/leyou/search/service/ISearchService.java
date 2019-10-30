package com.leyou.search.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;

public interface ISearchService {
    PageResult<Goods> search(SearchRequest request);
}
