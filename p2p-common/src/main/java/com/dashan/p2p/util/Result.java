package com.dashan.p2p.util;

import java.util.HashMap;

public class Result extends HashMap<Object, Object> {

    /**
     * 成功
     * @return
     */
    public static Result success(){
        Result result = new Result();
        result.put("code", 1);
        result.put("message", "");
        result.put("success", true);
        return result;
    }

    /**
     * 失败
     * @param msg
     * @return
     */
    public static Result error(String msg){
        Result result = new Result();
        result.put("code", -1);
        result.put("message", msg);
        result.put("success", false);
        return result;
    }
}
