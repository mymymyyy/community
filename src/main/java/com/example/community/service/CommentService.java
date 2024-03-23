package com.example.community.service;

import com.example.community.dao.CommentMapper;
import com.example.community.entity.Comment;
import com.example.community.util.CommunityConstant;
import com.example.community.util.SensitiveFliter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.awt.print.PrinterAbortException;
import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFliter sensitiveFliter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentByEntity(int entity_type, int entity_id, int offset, int limit){
        return commentMapper.selectCommentByEntity(entity_type, entity_id, offset, limit);
    }

    public int findCommentCount(int entity_type, int entity_id){
        return commentMapper.selectCountByEntity(entity_type, entity_id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

//        添加评论
//        添加html标识符转义
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
//        过滤敏感词
        comment.setContent(sensitiveFliter.filter(comment.getContent()));

        int rows = commentMapper.insertComment(comment);

//        更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

//    根据id查询评论
    public Comment findCommentById(int id){
        return commentMapper.selectCommentById(id);
    }
}
