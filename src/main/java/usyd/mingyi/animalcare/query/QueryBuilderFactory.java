package usyd.mingyi.animalcare.query;

import usyd.mingyi.animalcare.pojo.LovePost;
import usyd.mingyi.animalcare.pojo.User;
import usyd.mingyi.animalcare.pojo.Post;

public class QueryBuilderFactory {
    public static QueryBuilder<User> createUserQueryBuilder() {
       // return new UserQueryBuilder();
        return null;
    }

    public static QueryBuilder<Post> createPostQueryBuilder() {
        return new PostQueryBuilder();
    }

    public static QueryBuilder<LovePost> createLovePostQueryBuilder() {
        return new LovePostQueryBuilder();
    }
}
