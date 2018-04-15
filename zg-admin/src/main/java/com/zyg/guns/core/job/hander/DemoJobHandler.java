package com.zyg.guns.core.job.hander;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.zyg.guns.core.shiro.ShiroKit;
import com.zyg.guns.core.shiro.ShiroUser;
import com.zyg.guns.modular.system.model.User;
import com.zyg.guns.modular.system.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 任务Handler示例（Bean模式）
 *
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
//@JobHandler(value="demoJobHandler")
//@Component
public class DemoJobHandler extends IJobHandler {

	@Autowired
	private IUserService userService;

	private static Logger logger = LoggerFactory.getLogger(DemoJobHandler.class);
	@Override
	public ReturnT<String> execute(String param) throws Exception {
		logger.debug("XXL-JOB");
		ShiroUser shiroUser = ShiroKit.getUser();
		if(shiroUser != null){
			User user = userService.selectById(shiroUser.getId());
			logger.debug(user.getName());
		}

		return SUCCESS;
	}

}
