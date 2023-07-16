package usyd.mingyi.animalcare.utils;

import java.util.Arrays;

public class CommonUtils {
    public static String combineId(String fromId,String toId){
        String[] ids = {fromId, toId};
        Arrays.sort(ids);
       return String.join("_", ids);
    }
}
