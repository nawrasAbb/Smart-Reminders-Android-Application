package com.example.smartremindersapp2;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class login extends AppCompatActivity implements View.OnClickListener{
    private EditText UserNameText;
    private EditText PasswordText;
    private DatabaseReference ref;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean savelogin;


    public login(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserNameText = findViewById(R.id.UserName_r);
        PasswordText = findViewById(R.id.Password);
        editSharedPreferences(UserNameText.getText().toString());
    }

    //retrieve password button clicked
    public void retrieve_password(View view) {
        AuxiliaryFunctions.getInstance()
                .openNewPage(getApplicationContext(),retrieve_password.class);
    }

    //registration button clicked
    public void registration(View view) {
        AuxiliaryFunctions.getInstance().
                openNewPage(getApplicationContext(),registration.class);
    }

    // if at the last time the user was login so we keep the application connected,
    //that's mean that we directly open the home page
    // else we open the login page
    private void editSharedPreferences(String username){
        sharedPreferences=getSharedPreferences("U",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        if (getIntent().getBooleanExtra("logout",false)==true){
            editor.putBoolean("saveLogIn",false);
            editor.commit();
        }
        savelogin=sharedPreferences.getBoolean("saveLogIn",false);
        if (savelogin==true){
            UserNameText.setText(sharedPreferences.getString("username",null));
            PasswordText.setText(sharedPreferences.getString("password",null));
            AuxiliaryFunctions.getInstance()
                    .openNewPage(getApplicationContext(),HomePage.class);
        }
        else{
            UserNameText.setText("");
            PasswordText.setText("");
        }
    }

    // check if user name or password are empty and show error accordingly
    public boolean checkLogIn(EditText UserName, EditText Password) {
        if (Password.getText().toString().isEmpty() || UserName.getText().toString().isEmpty()) {
            if (Password.getText().toString().isEmpty()) {
                AuxiliaryFunctions.SetErrorOnTextView(Password, "enter password");
                if (UserName.getText().toString().isEmpty()) {
                    AuxiliaryFunctions.SetErrorOnTextView(UserName, "enter user name");
                }
            }
            return false;
        }
        return true;
    }


    // check if the user name exist in the database, if yes check if the password is correct
    // and show error accordingly id needed
    public void log_in_button(View view){
        boolean legal= checkLogIn(UserNameText,PasswordText);
        if (legal==true){
            ref=FirebaseDatabase.getInstance().getReference().child("Users").child(UserNameText.getText().toString());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println(dataSnapshot.exists());
                    if (!dataSnapshot.exists()) {
                        UserNameText.setError("user name is not exist");
                        UserNameText.requestFocus();
                    } else {
                        if (((String) dataSnapshot.child("password").getValue()).contentEquals(PasswordText.getText().toString())) {

                            // if the entered data is correct we update the status to be true
                            // and save the username and the password in the sharedPreferences
                            // of this context and move to the home page
                            editor.putBoolean("saveLogIn",true);
                            editor.putString("username",UserNameText.getText().toString());
                            editor.putString("password",PasswordText.getText().toString());
                            editor.commit();
                            SaveInDatabase.UpdateUserData(UserNameText.getText().toString(),"status","1");
                            AuxiliaryFunctions.getInstance().openNewPage(getApplicationContext(),login.class);
                            AuxiliaryFunctions.getInstance()
                                    .openNewPage(getApplicationContext(),
                                            HomePage.class);
                        }
                        else{
                            PasswordText.setError("Incorrect Password");
                            PasswordText.requestFocus();
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    @Override
    public void onClick(View v) {}
}
