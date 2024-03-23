package com.example.community.controller;

import com.example.community.annotation.LoginRequired;
import com.example.community.entity.LoginTicket;
import com.example.community.entity.User;
import com.example.community.service.FollowService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.CommunityUtil;
import com.example.community.util.CookieUtil;
import com.example.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

//    进入setting
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }


//    上传头像
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(Model model, MultipartFile headerImage){
//        判断文件是否为空
        if (headerImage == null){
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

//        判断文件后缀是否合理
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件的格式不正确");
            return "/site/setting";
        }

//        生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
//        确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
//            存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！", e);
        }

//        更新当前用户头像路径(web访问路径)
//        http://localhost:8080/community/user/hearder/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }


    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
//        服务器存放路径
        fileName = uploadPath + "/" + fileName;
//        文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
//        响应图片
        response.setContentType("image/" + suffix);
//        Java7的语法，try()中创建的变量会在编译时自动添加finally，若变量有close()，则在finally中自动关闭。
        try (
//                获取response字节流，Spring MVC会自动关闭response输出流
                OutputStream os = response.getOutputStream();
//                创建文件输入流，输入流是自己创建的，需要自己在finally中手动关闭
                FileInputStream fileInputStream = new FileInputStream(fileName);
             ){
//            建立一个1024字节的缓冲区，一批一批输出
            byte[] buffer = new byte[1024];
            int b = 0;
//            通过while循环输出，每次最多read1024字节的数据赋值给b，判断b是否为-1，即是否没读到数据，等于-1时就结束
            while((b = fileInputStream.read(buffer)) != -1){
//                每次输出最多1024字节的数据
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取图片失败：" + e.getMessage());
        }
    }

    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(Model model, String oldPassword, String newPassword){
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword);
        if(map == null || map.isEmpty()){
            return "redirect:/logout";
        }else{
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(Model model, @PathVariable("userId") int userId){
        User user = userService.findUserById(userId);

        if (user == null){
            throw new RuntimeException("该用户不存在！");
        }

//        用户
        model.addAttribute("user", user);
//        点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

//        关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
//        粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
//        是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
}
