package com.fo4ik.mySite.controllers.blog;


import com.fo4ik.mySite.config.Config;
import com.fo4ik.mySite.model.Blog;
import com.fo4ik.mySite.model.Logo;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.service.BlogService;
import com.fo4ik.mySite.service.LogoService;
import com.fo4ik.mySite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/blog")
public class BlogController {

    public static final Logger log = LoggerFactory.getLogger(BlogController.class);

    private final UserService userService;
    private final LogoService logoService;
    private final BlogService blogService;
    private static final String FOLDER = "pages/blog/";

    @Value("${user}")
    private String username;

    @Value("${host}")
    private String host;

    public BlogController(UserService userService, LogoService logoService, BlogService blogService) {
        this.userService = userService;
        this.logoService = logoService;
        this.blogService = blogService;
    }

    @GetMapping("")
    public String blog(@AuthenticationPrincipal User user, Model model) {
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("title", "Blog");
            model.addAttribute("posts", blogService.getAllPosts());
            model.addAttribute("user", user.getId());
        } catch (Exception e) {
            log.error("Error in blog: " + e.getMessage());
        }
        return FOLDER + "blog";
    }

    @GetMapping("/createPost")
    public String createPost(@AuthenticationPrincipal User user, Model model) {
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("title", "Create post");
        } catch (Exception e) {
            log.error("Error in blog: " + e.getMessage());
        }
        return FOLDER + "createPost";
    }

    @PostMapping("/createPost")
    public String createPost(@AuthenticationPrincipal User user, Model model, @RequestParam("title") String title,
                             @RequestParam("description") String description, @RequestParam("text") String text, @RequestParam("imgUrl") String imgUrl) {
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = formatter.format(date);

            blogService.createPost(user, title, description, text, strDate, imgUrl);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            log.error("Error in blog: " + e.getMessage());
        }
        return "redirect:/blog";
    }

    @PostMapping("/deletePost/{id}")
    public String deletePost(@PathVariable("id") long id, @AuthenticationPrincipal User user) {
        Blog post = blogService.getPost(id);
        if (post.getUser().getId() != user.getId()) {
            System.out.println("Not user");
            return "redirect:/blog";
        }
        blogService.deletePost(post);
        return "redirect:/blog";
    }

    @GetMapping("{id}")
    public String post(@PathVariable("id") long id, @AuthenticationPrincipal User user, Model model) {
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);

            Blog post = blogService.getPost(id);

            User contentUser = userService.getUser(username);
            Logo contentLogo = logoService.getLogo(contentUser);
            model.addAttribute("image", contentLogo.getPath());
            model.addAttribute("pageUrl", host + "/blog/" + id);
            model.addAttribute("title", post.getTitle());
            model.addAttribute("post", post);
        } catch (Exception e) {
            log.error("Error in blog: " + e.getMessage());
        }
        return FOLDER + "post";
    }

    @GetMapping("/editPost/{id}")
    public String editPost(@PathVariable("id") long id, @AuthenticationPrincipal User user, Model model) {
        if (blogService.getPost(id).getUser().getId() != user.getId()) {
            model.addAttribute("title", "Edit post");
            return "redirect:/blog";
        }
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);

            model.addAttribute("title", "Edit post");
            model.addAttribute("post", blogService.getPost(id));
        } catch (Exception e) {
            log.error("Error in blog: " + e.getMessage());
        }
        return FOLDER + "editPost";
    }

    @PostMapping("/editPost/{id}")
    public String editPost(@PathVariable("id") long id, @AuthenticationPrincipal User user, Model model, @RequestParam("title") String title,
                           @RequestParam("description") String description, @RequestParam("text") String text, @RequestParam("imgUrl") String imgUrl) {
        if (blogService.getPost(id).getUser().getId() != user.getId()) {
            model.addAttribute("title", "Edit post");
            return "redirect:/blog";
        }
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("title", "Edit post");
            blogService.updatePost(id, title, description, text, imgUrl);
            //TODO create in editor past code
        } catch (Exception e) {
            log.error("Error in blog: " + e.getMessage());
        }
        return "redirect:/blog";
    }

    @GetMapping("/search")
    public String search(@AuthenticationPrincipal User user, Model model, @RequestParam("search") String search) {
        try {
            Config config = new Config(userService, logoService);
            config.getUserLogo(user, model);
            model.addAttribute("title", "Search");
            model.addAttribute("posts", blogService.search(search));
        } catch (Exception e) {
            log.error("Error in blog: " + e.getMessage());
        }
        return FOLDER + "blog";
    }
}
