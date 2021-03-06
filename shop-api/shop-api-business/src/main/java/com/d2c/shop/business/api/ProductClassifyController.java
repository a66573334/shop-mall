package com.d2c.shop.business.api;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.business.api.base.BaseController;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.product.model.ProductClassifyDO;
import com.d2c.shop.service.modules.product.query.ProductClassifyQuery;
import com.d2c.shop.service.modules.product.service.ProductClassifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Cai
 */
@Api(description = "商品分类业务")
@RestController
@RequestMapping("/b_api/product_classify")
public class ProductClassifyController extends BaseController {

    @Autowired
    private ProductClassifyService productClassifyService;

    @ApiOperation(value = "分类查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public R<Collection<ProductClassifyDO>> list(PageModel page) {
        ProductClassifyQuery query = new ProductClassifyQuery();
        page.setPs(PageModel.MAX_SIZE);
        page.setDesc("sort", "create_date");
        Page<ProductClassifyDO> pager = (Page<ProductClassifyDO>) productClassifyService.page(page, QueryUtil.buildWrapper(query, false));
        Map<Long, ProductClassifyDO> first = new LinkedHashMap<>();
        List<ProductClassifyDO> second = new ArrayList<>();
        // 一级分类
        for (ProductClassifyDO pc : pager.getRecords()) {
            if (pc.getParentId() == null) {
                first.put(pc.getId(), pc);
            } else if (pc.getParentId() > 0) {
                second.add(pc);
            }
        }
        // 二级分类
        for (ProductClassifyDO pc : second) {
            if (first.get(pc.getParentId()) != null) {
                first.get(pc.getParentId()).getChildren().add(pc);
            }
        }
        return Response.restResult(first.values(), ResultCode.SUCCESS);
    }

}
