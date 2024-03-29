package com.example.community.controller;

import com.example.community.entity.DiscussPost;
import com.example.community.entity.Page;
import com.example.community.service.ElasticsearchService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import org.apache.ibatis.annotations.UpdateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(Model model, Page page, String keyword){
//        查找帖子
        List<Object> list = elasticsearchService.searchDiscussPost(keyword, page.getCurrent()-1, page.getLimit());

//        聚合数据
        List<DiscussPost> tempPosts = (List<DiscussPost>) list.get(1);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (tempPosts.size() != 0){
            for (DiscussPost discussPost : tempPosts){
                Map<String, Object> map = new HashMap<>();
                map.put("post", discussPost);
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId()));
                map.put("user", userService.findUserById(discussPost.getUserId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword");

//        设置分页
        page.setPath("/search?keyword=" + keyword);
        page.setRows(list.get(0) == null ? 0 : (int)list.get(0));

        return "/site/search";
    }
}
