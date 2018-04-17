package com.zyg.guns.modular.pay.unionpay.service;


import com.zyg.guns.modular.pay.common.model.Product;

import java.util.Map;


public interface IUnionPayService {
	/**
	 * 银联支付
	 * @param product
	 * @return  String
	 * @Date	2017年8月2日
	 * 更新日志
	 *
	 */
	String unionPay(Product product);
	/**
	 * 前台回调验证
	 * @param valideData
	 * @param encoding
	 * @return  String
	 * @Date	2017年8月2日
	 * 更新日志
	 * 2017年8月2日  科帮网 首次创建
	 *
	 */
	String validate(Map<String, String> valideData, String encoding);
	/**
	 * 对账单下载
	 * @Date	2017年8月2日
	 * 更新日志
	 *
	 */
	void fileTransfer();
}
