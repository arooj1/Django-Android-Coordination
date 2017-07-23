package com.example.ahmed.syncserver;

import org.json.JSONObject;

/**
 * Created by Ahmed on 7/22/2017.
 */

public class HistoryParser {
    public static Historydb parse (JSONObject jsonHistory){
        Historydb historydb=new Historydb();
        historydb.setUUid(jsonHistory.optString("uuid"));
        historydb.setHName(jsonHistory.optString("name"));
        historydb.getShortname(jsonHistory.optString("shortname"));
        historydb.getLocation(jsonHistory.optString("location"));
        historydb.getRoom(jsonHistory.optString("room"));
        historydb.getOutid(jsonHistory.optString("utility"));
        historydb.getUtility(jsonHistory.optString("utility"));
        return historydb;
    }
}
