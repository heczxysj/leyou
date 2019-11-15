package com.leyou.goods.service.impl;

import com.leyou.goods.service.IGoodsHtmlService;
import com.leyou.goods.service.IGoodsService;
import com.leyou.goods.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService implements IGoodsHtmlService {

    @Autowired
    private TemplateEngine engine;
    @Autowired
    private IGoodsService goodsService;

    @Override
    public void createHtml(Long spuId) {
        //初始化运行上下文
        Context context = new Context(); //org.thymeleaf.context.Context
        //设置数据模型
        context.setVariables(goodsService.loadData(spuId));

        PrintWriter printWriter = null;
        try {
            File file = new File("F:\\nginx\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);

            engine.process("item",context,printWriter);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null){
                printWriter.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    @Override
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    @Override
    public void deleteHtml(Long id) {
        File file = new File("F:\\nginx\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
