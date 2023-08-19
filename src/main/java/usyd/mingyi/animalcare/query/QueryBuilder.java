package usyd.mingyi.animalcare.query;

import com.github.yulichang.wrapper.MPJLambdaWrapper;

import java.util.List;


public interface QueryBuilder<T> {
    QueryBuilder<T> selectAll();
    QueryBuilder<T> associationUser();
    QueryBuilder<T> associationPost();
    QueryBuilder<T> collectionImage();
    QueryBuilder<T> eqUserId(Long userId);
    QueryBuilder<T> neUserId(Long userId);
    QueryBuilder<T> eqPostId(Long postId);
    QueryBuilder<T> order(Integer order);
    QueryBuilder<T> orderByTimeDes();
    QueryBuilder<T> like(String keywords);
    QueryBuilder<T> in(List<Long> ids);
    QueryBuilder<T> isRead(Boolean isRead);
    QueryBuilder<T> isCanceled(Boolean isCanceled);
    QueryBuilder<T> filterPostByUserId(Long userId);
    MPJLambdaWrapper<T> build();

}
