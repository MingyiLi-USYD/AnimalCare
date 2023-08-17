package usyd.mingyi.animalcare.query;
import usyd.mingyi.animalcare.pojo.Post;
import com.github.yulichang.wrapper.MPJLambdaWrapper;

import java.util.List;

public abstract class AbstractQueryBuilder<T> implements QueryBuilder<T> {

    protected MPJLambdaWrapper<T> query;

    @Override
    public QueryBuilder<T> filterPostByUserId(Long userId) {
        query.eq(Post::getUserId, userId);
        return this;
    }

    @Override
    public QueryBuilder<T> order(Integer order) {
        return this;
    }

    @Override
    public QueryBuilder<T> like(String keywords) {
        return this;
    }

    public AbstractQueryBuilder() {
        this.query = new MPJLambdaWrapper<>();
    }

    @Override
    public QueryBuilder<T> selectAll() {
        query.selectAll(getEntityClass());
        return this;
    }

    @Override
    public QueryBuilder<T> associationUser() {
        // Default empty implementation
        return this;
    }

    @Override
    public QueryBuilder<T> associationPost() {
        return this;
    }

    @Override
    public QueryBuilder<T> collectionImage() {
        // Default empty implementation
        return this;
    }

    @Override
    public MPJLambdaWrapper<T> build() {
        return query;
    }

    public   QueryBuilder<T> eqUserId(Long userId){
        return this;
    }

    @Override
    public QueryBuilder<T> neUserId(Long userId) {
        return this;
    }

    @Override
    public QueryBuilder<T> eqPostId(Long postId) {

        return this;
    }

    @Override
    public QueryBuilder<T> isRead(Boolean isRead) {
        return this;
    }

    @Override
    public QueryBuilder<T> in(List<Long> ids) {
        return this;
    }

    protected abstract Class<T> getEntityClass();

    @Override
    public QueryBuilder<T> isCanceled(Boolean isCanceled) {
        return this;
    }
}
