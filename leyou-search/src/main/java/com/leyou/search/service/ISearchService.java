package com.leyou.search.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;

import java.io.IOException;

public interface ISearchService {
    SearchResult search(SearchRequest request);

    void save(Long id) throws IOException;

    void delete(Long id);
}
