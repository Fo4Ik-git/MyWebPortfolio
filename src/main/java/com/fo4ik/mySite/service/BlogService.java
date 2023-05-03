package com.fo4ik.mySite.service;

import com.fo4ik.mySite.model.Blog;
import com.fo4ik.mySite.model.User;
import com.fo4ik.mySite.repo.BlogRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    public final BlogRepo blogRepo;

    public BlogService(BlogRepo blogRepo) {
        this.blogRepo = blogRepo;
    }

    public void createPost(User user, String title, String description, String text, String date, String imgUrl) {
        Blog post = new Blog(user, title, description, text, date, imgUrl);
        blogRepo.save(post);
    }

    public void updatePost(long id, String title, String description, String text, String imgUrl) {
        Blog post = getPost(id);
        post.setTitle(title);
        post.setDescription(description);
        post.setText(text);
        post.setImgUrl(imgUrl);
        blogRepo.save(post);
    }

    public void updatePost(long id, String title, String description, String text) {
        Blog post = getPost(id);
        post.setTitle(title);
        post.setDescription(description);
        post.setText(text);
        blogRepo.save(post);
    }

    public void deletePost(long id) {
        blogRepo.deleteById(id);
    }
    public void deletePost(Blog post) {
        blogRepo.delete(post);
    }

    public void deleteAllPosts() {
        blogRepo.deleteAll();
    }

    public List<Blog> search(String query) {
        return blogRepo.findByTitleContainingIgnoreCaseOrTextContainingIgnoreCase(query, query);
    }

    public Blog getPost(long id) {
        return blogRepo.findById(id);
    }

    public List<Blog> getAllPosts() {
        return blogRepo.findAll();
    }

}
