package usyd.mingyi.animalcare.utils;

public class BaseContext {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Integer id){
        threadLocal.set(id);
    }

    public static Integer getCurrentId(){
        return threadLocal.get();
    }
}
