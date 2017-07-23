package com.example.ahmed.syncserver;

/**
 * Created by Ahmed on 7/22/2017.
 */

public class Historydb {
    private String UUid;
    private String HName;
    private String Shortname;
    private String Room;
    private String Outid;
    private String Location;
    private String Utility;

    Historydb(){};
    // SET FUNCTIONS FOR EACH COLUMN OF A USER TABLE (DJANGO - USER MODEL).
    public void setUUid(String uuid){this.UUid=uuid;}
    public void setHName(String hName){this.HName=hName;}
    public void setShortname(String shortname){this.Shortname=shortname;}
    public void setRoom(String room){this.Room=room;}
    public void setOutid(String outid){this.Outid=outid;}
    public void setLocation(String location){this.Location=location;}
    public void setUtility(String utility){this.Utility=utility;}

    // GET FUNCTIONS FOR EACH COLUMN OF A USER TABLE (DJANGO - USER MODEL).
    public String getUUid ()
    {
        return UUid;
    }
    public String getHName() { return HName;}
    public String getShortname(String shortname) {return Shortname;}
    public String getRoom(String room)
    {
        return Room;
    }
    public String getOutid(String utility){return Outid;}
    public String getLocation(String location){return Location;}
    public String getUtility(String utility){return Utility;}
}
