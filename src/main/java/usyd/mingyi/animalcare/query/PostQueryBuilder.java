package usyd.mingyi.animalcare.query;

import usyd.mingyi.animalcare.dto.PostDto;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.PostImage;
import usyd.mingyi.animalcare.pojo.User;

import java.util.List;


public class PostQueryBuilder extends AbstractQueryBuilder<Post> {

    @Override
    public QueryBuilder<Post> associationUser() {
        query.selectAssociation(User.class, PostDto::getPostUser)
                .leftJoin(User.class, User::getUserId, Post::getUserId);
        return this;
    }

    @Override
    public QueryBuilder<Post> collectionImage() {
        query.selectCollection(PostImage.class, PostDto::getImages)
                .leftJoin(PostImage.class, PostImage::getPostId, Post::getPostId);
        return this;
    }

    @Override
    public QueryBuilder<Post> eqUserId(Long userId) {
        query.eq(Post::getUserId, userId);
        return this;
    }


    @Override
    public QueryBuilder<Post> eqPostId(Long postId) {
        query.eq(Post::getPostId, postId);
        return this;
    }

    @Override
    protected Class<Post> getEntityClass() {
        return Post.class;
    }

    @Override
    public QueryBuilder<Post> order(Integer order) {
        query.eq(Post::getVisible, true)
                .orderByDesc(order == 1, Post::getPostTime)
                .orderByDesc(order == 2, Post::getLove);
        return this;
    }

    @Override
    public QueryBuilder<Post> like(String keywords) {
        query .like(keywords != null && !keywords.isEmpty(), Post::getPostContent, keywords);
        return this;
    }

    @Override
    public QueryBuilder<Post> in(List<Long> ids) {
        query.in(Post::getPostId, ids);
        return this;
    }
}