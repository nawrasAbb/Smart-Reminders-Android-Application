package com.example.smartremindersapp2;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
public class SaveInDatabase {
    private static DatabaseReference ref;
    private static SaveInDatabase MySaveInDatabase;


    private SaveInDatabase(){}

    public static SaveInDatabase getInstance(){
        if (MySaveInDatabase==null)
            MySaveInDatabase=new SaveInDatabase();
        return MySaveInDatabase;
    }

    // save user after registration
    public static void saveUser(User user){
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserName());
        ref.setValue(user);
    }

    // update fields in the user object
    public static void UpdateUserData(String username,String Field,String NewData){
        HashMap map = new HashMap();
        map.put(Field,NewData);
        ref= FirebaseDatabase.getInstance().getReference().child("Users").child(username);
        ref.updateChildren(map);
    }

    // update fields in the alarm object
    public static void UpdateAlarmData(String username,String key,String Field,boolean NewData){
        HashMap map = new HashMap();
        map.put(Field,NewData);
        ref=FirebaseDatabase.getInstance().getReference()
                .child("Users").child(username).child("Alarms").child(key);
        ref.updateChildren(map);
    }
}





