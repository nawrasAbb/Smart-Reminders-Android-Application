package com.example.smartremindersapp2;

import androidx.appcompat.app.AppCompatDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class openDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Success Message").setMessage("Successfully Registered!").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(getContext(),login.class);
                startActivity(intent);
            }
        });
        return builder.create();
    }


}