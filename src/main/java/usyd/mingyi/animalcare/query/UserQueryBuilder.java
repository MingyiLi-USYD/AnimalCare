package usyd.mingyi.animalcare.query;

import usyd.mingyi.animalcare.pojo.User;

public class UserQueryBuilder extends AbstractQueryBuilder<User>{

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

}
