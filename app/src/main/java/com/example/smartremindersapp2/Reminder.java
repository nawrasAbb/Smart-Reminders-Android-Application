package com.example.smartremindersapp2;

import java.util.Date;
import java.util.List;

public class Reminder {
    public int id;
    private String Title;
    private Date  RemindDate;
    private String Key;
    private String Description;
    private Double LAT;
    private Double LNG;
    private boolean state;
    private String LocationAsString;
    private String Location;
    private List<String> audios;
    private String Type;
    private User Person;
    private boolean DateState;


    public Reminder(String key,
      String title, String description , String type,Date date,
      String location,String address,List<String> audioArr
            ,User person) {
        Title =title ;
        RemindDate = date;
        setState(true);
        audios=audioArr;
        DateState=false;
        Person=person;
        Key = key;
        Type=type;
        Location=address;
        Description = description;
        LocationAsString=location;

    }
    public Reminder() {}


    /*
    getters
     */
    public String getLocation() {
        return Location;
    }
    public User getPerson() { return Person; }
    public String getLocationAsString(){return LocationAsString;}
    public Double getLAT() {
        return LAT;
    }
    public Double getLNG() {
        return LNG;
    }
    public String getMyType() {
        return Type;
    }
    public String getKey() {
        return Key;
    }
    public String getDescription() {
        return Description;
    }
    public List<String> getAudios() {
        return audios;
    }
    public boolean isState() {
        return state;
    }
    public String getTitle() {
        return Title;
    }
    public Date getRemindDate() {
        return RemindDate;
    }
    public int getId() {
        return id;
    }
    public boolean isDateState() {return DateState;}



    /*
    setters
     */
    public void setLocation(String location) {
        Location = location;
    }
    public void setPerson(User person) { Person = person; }
    public void setLocationAsString(String Location){this.LocationAsString=Location;}
    public void setLAT(Double LAT) {
        this.LAT = LAT;
    }
    public void setLNG(Double LNG) {
        this.LNG = LNG;
    }
    public void setMyType(String myType) {
        Type = myType;
    }
    public void setKey(String key) {
        Key = key;
    }
    public void setDescription(String description) {
        Description = description;
    }
    public void setAudios(List<String> audios) {
        this.audios = audios;
    }
    public void setState(boolean state) {
        this.state = state;
    }
    public void setTitle(String message) {
        this.Title = message;
    }
    public void setRemindDate(Date remindDate) {
        this.RemindDate = remindDate;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDateState(boolean dateState) {DateState = dateState;}
}