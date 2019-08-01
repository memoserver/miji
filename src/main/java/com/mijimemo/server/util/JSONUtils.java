package com.mijimemo.server.util;

import com.alibaba.fastjson.JSONObject;

public class JSONUtils {

    public static JSONObject getJson(Integer status, Object data) {
        JSONObject json = new JSONObject();
        json.put("status",status);
        json.put("data",data);
        return json;
    }
}
