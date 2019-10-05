package com.paascloud.provider.pmc.controller;

import com.paascloud.common.util.UserContext;
import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.ProductDto;
import com.paascloud.provider.pmc.dao.ProductMapper;
import com.paascloud.provider.pmc.entity.Product;
import com.paascloud.provider.service.ProductFeignApi;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductFeignApi {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public Wrapper saveProduct(@RequestBody ProductDto productDto) {
        String s = UserContext.get();
        System.out.println(s);
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        int insert = productMapper.insert(product);
        return WrapMapper.success(insert);
    }
}
