package com.example.community.dao.elasticsearch;

import com.example.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
//extends ElasticsearchRepository<DiscussPost, Integer>
//继承ElasticsearchRepository接口，需要添加泛型，即该接口需要处理的实体类是DiscussPost，实体类中的主键是Integer类型
//ElasticsearchRepository接口已经定义好了对es服务器访问的增删改查方法
//继承ElasticsearchRepository并添加@Repository注解后，spring会自动实现该接口，直接调用即可
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
