package com.example.ahmed.syncserver;

/**
 * Created by Ahmed on 7/10/2017.
 */

public class User {
    private int Id;
    private String Name;
    private String Email;
    private String Password;
    private String Link;

    User()
    {
        //this.setName(Name);
        //this.setSync_status(Sync_status);
        //this.setEmail(Email);
        //this.setPassword(Password);

    }
    // SET FUNCTIONS FOR EACH COLUMN OF A USER TABLE (DJANGO - USER MODEL).
    public void setId(int id){this.Id = id;}
    public void setName(String name)
    {
        this.Name = name;
    }
    public void setEmail(String email){this.Email = email;}
    public void setPassword(String password){this.Password = password;}
    public void setLink(String link)
    {
        this.Link = link;
    }

    // GET FUNCTIONS FOR EACH COLUMN OF A USER TABLE (DJANGO - USER MODEL).
    public int getId() {return Id;}
    public String getName ()
    {
        return Name;
    }
    public String getEmail() { return Email;}
    public String getPassword() {return Password;}
    public String getLink()
    {
        return Link;
    }
}

