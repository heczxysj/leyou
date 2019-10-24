package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.extension.SpuBo;
import com.leyou.item.service.ICategoryService;
import com.leyou.item.service.IGoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%" + key + "%");
        }
        if(saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //分页条件
        PageHelper.startPage(page,rows);
        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //spu集合转换成spuBo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            // copy共同属性的值到新的对象
            BeanUtils.copyProperties(spu, spuBo);

            //查询品牌名称
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            String bName = brand.getName();
            spuBo.setBname(bName);

            //查询分类名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));

            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(),spuBos);
    }
}
