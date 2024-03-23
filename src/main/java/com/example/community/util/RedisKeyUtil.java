package com.example.community.util;

public class RedisKeyUtil {

    private static final String SPLITE = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

//    某个实体的赞
//    like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLITE + entityType + SPLITE + entityId;
    }

//    某个用户的赞
//    like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLITE + userId;
    }

//    某个用户关注的实体
//    followee:userId:entityType -> zset(entityId,now) (zset需要传入的分数使用点击关注当时的时间)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLITE + userId + SPLITE + entityType;
    }

//    某个实体拥有的粉丝
//    follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER + SPLITE + entityType + SPLITE + entityId;
    }

//    登录验证码
//    在用户访问登录页面时，给用户发一个凭证（一个随机字符串），用于临时标识用户
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA + SPLITE + owner;
    }

//    登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLITE + ticket;
    }

//    用户
    public static String getUserKey(int userId){
        return PREFIX_USER + SPLITE + userId;
    }
}
