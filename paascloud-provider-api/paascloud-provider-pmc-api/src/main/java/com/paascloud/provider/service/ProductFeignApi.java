package com.paascloud.provider.service;

import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.ProductDto;
import com.paascloud.provider.service.hystrix.ProductFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "paascloud-provider-pmc",fallback = ProductFeignHystrix.class)
public interface ProductFeignApi {

    /**
     * 新增商品.
     *
     * @param productDto
     * @return the wrapper
     */
    @PostMapping(value = "/pmc/product/save")
    Wrapper saveProduct(ProductDto productDto);

}
