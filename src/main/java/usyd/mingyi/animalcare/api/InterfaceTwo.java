package usyd.mingyi.animalcare.api;

public interface InterfaceTwo {
    //下面方法都已经实现
    int love (int userId,int postId);
    int cancelLove (int userId,int postId);
    int lovePlus(int postId);
    int loveMinus(int postId);
}
