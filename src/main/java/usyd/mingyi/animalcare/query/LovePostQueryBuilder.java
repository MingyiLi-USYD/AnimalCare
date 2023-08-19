package usyd.mingyi.animalcare.query;

import usyd.mingyi.animalcare.dto.LovePostDto;
import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.Post;
import usyd.mingyi.animalcare.pojo.User;


public class LovePostQueryBuilder extends AbstractQueryBuilder<LovePost>{

    @Override
    protected Class<LovePost> getEntityClass() {
        return LovePost.class;
    }

    @Override
    public QueryBuilder<LovePost> associationUser() {
        query .selectAssociation(User.class, LovePostDto::getUserInfo)
                .leftJoin(User.class, User::getUserId, LovePost::getUserId);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> associationPost() {
        query .selectAssociation(Post.class, LovePostDto::getRelevantPost)
                .leftJoin(Post.class, Post::getPostId, LovePost::getPostId);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> eqUserId(Long userId) {
        query.eq(LovePost::getUserId, userId);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> neUserId(Long userId) {
        query.ne(LovePost::getUserId, userId);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> eqPostId(Long postId) {
        query.eq(LovePost::getPostId, postId);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> isCanceled(Boolean isCanceled) {
        query.eq(LovePost::getIsCanceled, isCanceled);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> isRead(Boolean isRead) {
        query.eq(LovePost::getIsRead, isRead);
        return this;
    }

    @Override
    public QueryBuilder<LovePost> orderByTimeDes() {
        query.orderByDesc(LovePost::getCreateTime);
        return this;
    }
}
