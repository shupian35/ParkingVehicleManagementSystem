package com.shupian.pvms.service;

import com.alipay.api.AlipayApiException;
import com.shupian.pvms.domain.entity.AlipayBean;

public interface PayService {

    String aliPay(AlipayBean aliPayBean) throws AlipayApiException;
}

