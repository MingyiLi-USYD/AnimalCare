package usyd.mingyi.animalcare.service;

import usyd.mingyi.animalcare.pojo.Comment;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.List;

public interface PostService {
    int addPost(Post post);
    int addImage(int imagePostId,String imageUrl);
    List<Post> getAllPosts(int currPage, int pageSize);
    List<Post> getAllPostsOrderByLove(int currPage, int pageSize);
    Post queryPostById(int postId);
    boolean checkLoved(int userId,int postId);
    int love(int userId, int postId);
    int cancelLove(int userId, int postId);
    int deletePost(int postId, int userId);
    int addComment(Comment comment);
    List<Post> getPostByUserId(int userId);
    List<Comment> getCommentsByPostId(int postId);
    List<Post> getPostsByKeywords(String keywords);


}
