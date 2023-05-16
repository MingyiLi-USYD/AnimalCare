package usyd.mingyi.animalcare.utils;

import lombok.Data;

@Data
public class ResultData<t> {
    /** 结果状态 ,具体状态码参见ResultData.java*/
    private int status;
    private String message;
    private t data;
    private long timestamp ;

    public ResultData (){
        this.timestamp = System.currentTimeMillis();
    }

    public static <t> ResultData<t> success(t data) {
        ResultData<t> resultData = new ResultData<>();
        resultData.setStatus(ReturnCode.RC200.getCode());
        resultData.setMessage(ReturnCode.RC200.getMessage());
        resultData.setData(data);
        return resultData;
    }

    public static <t> ResultData<t> fail(int code, String message) {
        ResultData<t> resultData = new ResultData<>();
        resultData.setStatus(code);
        resultData.setMessage(message);
        return resultData;
    }


}
