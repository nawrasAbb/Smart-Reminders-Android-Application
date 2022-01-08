package com.example.smartremindersapp2;

public class User {
    /*
     variables
    */
    private String Name;
    private String UserName;
    private String Email;
    private String Password;
    private String Status;
    private double lat;
    private double lang;

    /*
     Constructors
    */
    public User(String name, String userName, String email, String password,Double lat1,Double lang1) {
        Name = name;
        UserName = userName;
        Email = email;
        Password = password;
        Status="0";
        lat=lat1;
        lang=lang1;
    }
    public User() {}


    /*
     getters
    */
    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLang() {
        return lang;
    }
    public void setLang(double lang) {
        this.lang = lang;
    }
    public String getStatus() {
        return Status;
    }
    public String getName() {
        return Name;
    }
    public String getUserName() {
        return UserName;
    }
    public String getEmail() {
        return Email;
    }
    public String getPassword() {
        return Password;
    }

    /*
      setters
    */
    public void setName(String name) {
        Name = name;
    }
    public void setUserName(String userName) {
        UserName = userName;
    }
    public void setEmail(String email) {
        Email = email;
    }
    public void setPassword(String password) {
        Password = password;
    }
    public void setStatus(String status) {
        Status = status;
    }
}
