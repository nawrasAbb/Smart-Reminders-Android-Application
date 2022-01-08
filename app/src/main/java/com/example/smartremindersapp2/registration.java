package com.example.smartremindersapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class registration extends AppCompatActivity {
    /*
    variables
     */
    private EditText NameText;
    private EditText UserNameText;
    private EditText EmailText;
    private EditText PassWardText;
    private EditText ConfirmPassWardText;
    private  DatabaseReference ref;


    public  registration(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        NameText = findViewById(R.id.Name_r);
        UserNameText = findViewById(R.id.UserName_r);
        EmailText = findViewById(R.id.Email_r);
        PassWardText = findViewById(R.id.password_r);
        ConfirmPassWardText = findViewById(R.id.ConfirmPassword_r);
        List<String> users=new ArrayList<>();
        ref=FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user= ds.getValue(User.class);
                    users.add(user.getUserName());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //show error if the inserted username exist
        UserNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(users.contains(UserNameText.getText().toString()))
                    AuxiliaryFunctions.SetErrorOnTextView(UserNameText,"user name already exist");
                else
                    UserNameText.setError(null);
            }
        });
    }

    // registration button
    public void Registration(View view) {
        List<String> TheError=checkRegistrationData(NameText,UserNameText,EmailText,PassWardText,ConfirmPassWardText);
        boolean legal=Exeptions(TheError);
        if (legal==true && UserNameText.getError()==null){
            User user = new User(NameText.getText().toString(), UserNameText.getText().toString(), EmailText.getText().toString(),
                    PassWardText.getText().toString(),0.,0.);
            SaveInDatabase.
                    saveUser(user);
            new openDialog().show(getSupportFragmentManager(),"example");
        }
    }


    //  pass over all the textviews and show error if necessary
    public boolean Exeptions(List<String> TheError) {
        for (String message : TheError) {
            if (message.contentEquals("success")) {
                return true;
            } else if (message.contentEquals("empty first name")) {
                AuxiliaryFunctions.SetErrorOnTextView(NameText,"Enter First Name");
            }
            else if (message.contentEquals("first name must contain only letters")) {
                AuxiliaryFunctions.SetErrorOnTextView(NameText,"Must Contain Only Letters");
            }
            else if (message.contentEquals("empty user name")) {
                AuxiliaryFunctions.SetErrorOnTextView(UserNameText,"Enter User Name");
            }
            else if (message.contentEquals("user name already exist")) {
                AuxiliaryFunctions.SetErrorOnTextView(UserNameText,"UserName Already Exist");
            }
            else if (message.contentEquals("empty email")) {
                AuxiliaryFunctions.SetErrorOnTextView(EmailText,"Enter An Email");
            }
            else if (message.contentEquals("invalid email")) {
                AuxiliaryFunctions.SetErrorOnTextView(EmailText,"Invalid Email");
            }
            else if (message.contentEquals("empty password")) {
                AuxiliaryFunctions.SetErrorOnTextView(PassWardText,"Empty Password");
            }
            else if (message.contentEquals("empty confirm password")) {
                AuxiliaryFunctions.SetErrorOnTextView(ConfirmPassWardText,"didn't confirm password");
            }
            else if (message.contentEquals("inappropriate passward")) {
                AuxiliaryFunctions.SetErrorOnTextView(ConfirmPassWardText,"inappropriate passward");
            }
        }
        return false;
    }

    //check if the inserted data is legal
    public List<String> checkRegistrationData(EditText nameText, EditText userNameText, EditText emailText, EditText passWardText, EditText confirmPassWardText)  {
        List<String> errors = new ArrayList<>();

        //if user name empty
        if (userNameText.getText().toString().isEmpty()) {
            errors.add("empty user name");
        }

        //if name empty
        if (nameText.getText().toString().isEmpty()) {
            errors.add("empty first name");

        //if name is legal
        } else if (!Pattern.matches("[a-zA-Z]+", nameText.getText().toString())) {
            errors.add("first name must contain only letters");
        }
        //if e-mail empty
        if (emailText.getText().toString().isEmpty()) {
            errors.add("empty email");
        }
        // if email not empty so check if it legal
        else {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pat = Pattern.compile(emailRegex);
            if (pat.matcher(emailText.getText().toString()).matches() == false) {
                errors.add("invalid email");
            }
        }

        //check if password empty
        if (passWardText.getText().toString().isEmpty()) {
            errors.add("empty password");
        }

        //check if confirmPassWardText empty
        if (confirmPassWardText.getText().toString().isEmpty()) {
            errors.add("empty confirm password");
        } else if (!(confirmPassWardText.getText().toString().contentEquals(passWardText.getText().toString()))) {
            errors.add("inappropriate password");
        }

        if (errors.isEmpty()) {
            errors.add("success");
        }
        return errors;
    }
}
