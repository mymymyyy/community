package com.example.community.config;

import com.example.community.quartz.AlphaJob;
import com.example.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

//    FactoryBean可简化bean的实例化过程：
//    1、通过FactoryBean封装bean的实例化过程
//    2、将FactoryBean装配到spring容器中
//    3、将FactoryBean注入给其他的bean
//    4、该bean得到的是FactoryBean所管理的对象实例。

//    配置JobDetail()
//    @Bean
    public JobDetailFactoryBean alphaJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
//        声明任务是否持久保存
        factoryBean.setDurability(true);
//        声明任务是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

//    配置Trigger(SimpleTriggerFactoryBean（简单的trigger，例如每10分钟执行一次）, CronTriggerFactoryBean（复杂的trigger，例如每月月底半夜两点执行）)
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
//        声明执行间隔（每隔3000毫秒执行一次）
        factoryBean.setRepeatInterval(3000);
//        存储job的状态（这里初始化了一个默认对象）
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

//    刷新帖子任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
//        声明任务是否持久保存
        factoryBean.setDurability(true);
//        声明任务是否可恢复
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
//        声明执行间隔（每隔5分钟执行一次）
        factoryBean.setRepeatInterval(1000 * 60 * 2);
//        存储job的状态（这里初始化了一个默认对象）
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
