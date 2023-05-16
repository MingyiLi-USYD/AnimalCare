package usyd.mingyi.animalcare.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import usyd.mingyi.animalcare.pojo.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisUtils {
    public static Post getPost(RedisTemplate redisTemplate,int id,boolean loved){
        String postId = "post"+id;
        if (redisTemplate.hasKey(postId)){
            redisTemplate.opsForHash().increment(postId,"visitCount",1);
            Post post = new Post();
            Integer userId = (Integer) redisTemplate.opsForHash().get(postId,"userId");
            Integer love = (Integer) redisTemplate.opsForHash().get(postId,"love");
            String postContent = (String) redisTemplate.opsForHash().get(postId,"postContent");
            String tag = (String) redisTemplate.opsForHash().get(postId,"tag");
            String topic = (String) redisTemplate.opsForHash().get(postId,"topic");
            Long posTime = (Long) redisTemplate.opsForHash().get(postId,"posTime");
            List<String> imageUrlList = (List<String>) redisTemplate.opsForHash().get(postId,"imageUrlList");
            String userAvatar = (String) redisTemplate.opsForHash().get(postId,"userAvatar");
            String nickName = (String) redisTemplate.opsForHash().get(postId,"nickName");
            String userName = (String) redisTemplate.opsForHash().get(postId,"userName");
            post.setPostId(id);
            post.setUserId(userId);
            post.setLove(love);
            post.setPostContent(postContent);
            post.setTag(tag);
            post.setTopic(topic);
            post.setPosTime(posTime);
            post.setImageUrlList(imageUrlList);
            post.setUserAvatar(userAvatar);
            post.setNickName(nickName);
            post.setUserName(userName);
            post.setLoved(loved);
            return post;
        }else {
            return null;
        }

    }
    public static void putPost(RedisTemplate redisTemplate,Post post){
        String key = "post" + post.getPostId();
        redisTemplate.opsForHash().put(key,"postId",post.getPostId());
        redisTemplate.opsForHash().put(key,"userId",post.getUserId());
        redisTemplate.opsForHash().put(key,"love",post.getLove());
        redisTemplate.opsForHash().put(key,"postContent",post.getPostContent());
        redisTemplate.opsForHash().put(key,"topic",post.getTopic());
        redisTemplate.opsForHash().put(key,"posTime",post.getPosTime());
        redisTemplate.opsForHash().put(key,"tag",post.getTag());
        redisTemplate.opsForHash().put(key,"imageUrlList",post.getImageUrlList());
        redisTemplate.opsForHash().put(key,"userAvatar",post.getUserAvatar());
        redisTemplate.opsForHash().put(key,"nickName",post.getNickName());
        redisTemplate.opsForHash().put(key,"userName",post.getUserName());
        redisTemplate.opsForHash().put(key,"visitCount",1);
        redisTemplate.expire(key,600, TimeUnit.SECONDS);

    }

    public static List<Post> getHots(RedisTemplate redisTemplate){
        if(redisTemplate.hasKey("hots")){
            Set<Integer> postIds = redisTemplate.opsForZSet().reverseRange("hots", 0, -1);

            int count = 0;
            List<Post> posts = new ArrayList<>();
            for (Integer postId : postIds) {

                Post post = RedisUtils.getPost(redisTemplate, postId, true);
                if (post != null) {
                    posts.add(post);
                    count++;
                }
                if (count == 5) break;
            }
            return posts;
        }
        Set<String> postsName = redisTemplate.keys("post*");
        for (String s : postsName) {
            int visitCount = (int) redisTemplate.opsForHash().get(s, "visitCount");
            int postId = (int) redisTemplate.opsForHash().get(s, "postId");
            redisTemplate.opsForZSet().add("hots", postId, visitCount);
        }
        redisTemplate.expire("hots", 10, TimeUnit.SECONDS);
        Set<Integer> postIds = redisTemplate.opsForZSet().reverseRange("hots", 0, -1);

        int count = 0;
        List<Post> posts = new ArrayList<>();
        for (Integer postId : postIds) {

            Post post = RedisUtils.getPost(redisTemplate, postId, true);
            if (post != null) {
                posts.add(post);
                count++;
            }
            if (count == 5) break;
        }

        return posts;
    }

}
