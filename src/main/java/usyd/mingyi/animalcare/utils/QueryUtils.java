package usyd.mingyi.animalcare.utils;

import com.github.yulichang.wrapper.MPJLambdaWrapper;
import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.PostImage;
import usyd.mingyi.animalcare.pojo.User;

import java.awt.*;


public class QueryUtils {

    public static <T> MPJLambdaWrapper<T> postWithUser(MPJLambdaWrapper<T> query) {
        return query .selectAssociation(User.class, PostDto::getPostUser)
                .leftJoin(User.class,User::getUserId, Post::getUserId);
    }



    public static <T> MPJLambdaWrapper<T> postWithPostImages(MPJLambdaWrapper<T> query) {
        return query.selectCollection(PostImage.class,PostDto::getImages)
                .leftJoin(PostImage.class,PostImage::getPostId,Post::getPostId);
    }

    public static <T> MPJLambdaWrapper<T> postPageWithPostImages(MPJLambdaWrapper<T> query) {
        return query
                .leftJoin(PostImage.class,PostImage::getPostId,Post::getPostId);
    }


}
