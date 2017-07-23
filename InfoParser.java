package com.example.ahmed.syncserver;

import org.json.JSONObject;

/**
 * Created by Ahmed on 7/10/2017.
 */

public class InfoParser {
    public static User parse(JSONObject jsonObject){

        User user =new User();
        user.setId(jsonObject.optInt("id"));
        user.setName(jsonObject.optString("name"));
        user.setEmail(jsonObject.optString("email"));
        user.setPassword(jsonObject.optString("password"));
        user.setLink(jsonObject.optString("link"));
        return user;
    }

   }
