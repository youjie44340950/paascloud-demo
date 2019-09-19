package com.paascloud.provider.service.hystrix;

import com.paascloud.common.util.wrapper.WrapMapper;
import com.paascloud.common.util.wrapper.Wrapper;
import com.paascloud.provider.model.ProductDto;
import com.paascloud.provider.service.ProductFeignApi;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignHystrix implements ProductFeignApi {

    @Override
    public Wrapper saveProduct(ProductDto productDto) {
        return WrapMapper.timeOutWrap();
    }
}
