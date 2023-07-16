package usyd.mingyi.animalcare.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommonUtils {
    public static String combineId(String fromId,String toId){
        String[] ids = {fromId, toId};
        Arrays.sort(ids);
       return String.join("_", ids);
    }

    public static List<String> combineParticipates(String fromId, String toId){
        String[] ids = {fromId, toId};
       return Arrays.stream(ids).collect(Collectors.toList());
    }
}
