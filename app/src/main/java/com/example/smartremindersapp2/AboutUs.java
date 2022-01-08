package com.example.smartremindersapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

//display the text in the About Us page
public class AboutUs extends AppCompatActivity {
    private TextView about_us;
    private DrawerLayout drawerLayout;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        about_us=findViewById(R.id.AboutAs);
        drawerLayout=findViewById(R.id.drawer_layout);
        userName = getSharedPreferences("U", MODE_PRIVATE).getString("username", null);
        String about_us_string= "We are two young women with a flaming passion torward technology and programming," +
                " and we are at the end of our studies in the wonderful Haifa University .\n" +
                "We love productivity and organization, it is part of our daily life" +
                " , we were inspired to create the perfect app that will help you remember" +
                " , plan and execute , each task of your day without fail !\n" +
                "Have you ever felt like your mind is drowning in a never ending to-do" +
                " list? Many Studies show that when we write down our tasks it reliefs us " +
                "of the burden of constently thinking about them , and what better place to" +
                " save them than in our phones! \n" +
                "With this app never again feel overwhelmed or miss a task !";
        about_us.setText(about_us_string);
    }

    public void ClickMenu(View view){
        AuxiliaryFunctions.openDrawer(drawerLayout);
    }
    public void ClickHome(View view){ AuxiliaryFunctions.redirectActivity(this,HomePage.class,userName);  }
    public void ClickDashboard(View view){ AuxiliaryFunctions.redirectActivity(this,all_alarms.class,userName); }
    public void ClickAboutUs(View view){ recreate(); }
    public void ClickLogout(View view){ AuxiliaryFunctions.logout(this,userName); }

    @Override
    protected void onPause(){
        super.onPause();
        AuxiliaryFunctions.closeDrawer(drawerLayout);
    }
}