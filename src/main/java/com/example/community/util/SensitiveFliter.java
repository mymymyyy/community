package com.example.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFliter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFliter.class);

//    替换符
    private static final String REPLACEMENT = "***";

//    根节点
    private TreeNode rootNode = new TreeNode();

//    该注解表示在容器实例化SensitiveFliter这个bean之后，即在调用其构造器之后，就自动调用该注解的方法
//    SensitiveFliter在服务启动时就被初始化了，所以在服务启动时init()方法就被调用，前缀树就被构造好了，避免多次构造
    @PostConstruct
    public void init(){
        try(
                //        获取类加载器，从类路径下加载资源，即target/classes路径下获取，代码编译后所有的文件都在classes路径下
                //        这样获取到的是一个字节流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
//                将字符流转为缓冲流
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ){
            String keyword;
            while((keyword = reader.readLine()) != null){
//                添加到前缀树
                this.addKeyword(keyword);
            }

        }catch (IOException e){
            logger.error("加载敏感词失败：" + e.getMessage());
        }

    }

    private void addKeyword(String keyword){
        TreeNode tempNode = rootNode;
        for (int i=0;i<keyword.length();i++){
            char c = keyword.charAt(i);
            TreeNode subNode = tempNode.getSubNode(c);

            if (subNode == null){
//                初始化子节点
                subNode = new TreeNode();
                tempNode.addSubNodes(c, subNode);
            }

//            指向子节点，进入下一轮循环
            tempNode = subNode;

//            设置结束标识
            if (i == keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

//        指针1
        TreeNode tempNode = rootNode;
//        指针2
        int begin = 0;
//        指针3
        int position = 0;
//        结果
        StringBuilder sb = new StringBuilder();

        while(position < text.length()){
            char c = text.charAt(position);

//            跳过符号
            if (isSymbol(c)){
//                若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
//                无论符号在开头或中间，指针3都向下走一步
                position++;
                continue;
            }

//            检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null){
//                以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
//                进入下一个位置
                position = ++begin;
//                重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isKeywordEnd()){
//                发现敏感词，将begin-position字符串替换掉
                sb.append(REPLACEMENT);
//                进入下一个位置
                begin = ++position;
//                重新指向根节点
                tempNode = rootNode;
            }else{
//                检查下一个字符
                position++;
            }
        }

//        将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

//    判断是否为符号
    private boolean isSymbol(Character c){
//        harUtils.isAsciiAlphanumeric()判断是否为普通字符（abc123...）
//        0x2E80 - 0x9FFF表示东亚的文字范围，在此范围之外认为时特殊符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

//    前缀树
    private class TreeNode{
//        关键词结束标识
        private boolean isKeywordEnd = false;

//        子节点（key是下级字符，value是下级节点）
        private Map<Character, TreeNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNodes(Character c, TreeNode node) {
            subNodes.put(c, node);
        }

        public TreeNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
