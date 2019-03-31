package com.oc.liza.go4lunch.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.oc.liza.go4lunch.MainActivity;
import com.oc.liza.go4lunch.R;
import com.oc.liza.go4lunch.api.UserHelper;

import java.util.Objects;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.button_delete)
    ImageButton delete;
    @BindView(R.id.update_button)
    ImageButton update_button;
    @BindView(R.id.update_text)
    EditText update_text;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSettings();
    }

    private void initSettings() {
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserFromFirestore();
            }
        });
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsername();
            }
        });
    }

    private void updateUsername() {
        final String username = update_text.getText().toString();
        UserHelper.updateUsername(username, uid).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Your username is updated: " + username, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error updating username: " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserFromFirestore() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_user_dialog)
                .setMessage(R.string.delete_user_in_settings)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        UserHelper.deleteUser(uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), R.string.successfully_deleted, Toast.LENGTH_SHORT).show();
                                deleteUserFromFirebase();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_deleting_user) + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUserFromFirebase() {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                            }
                        });

            }
        });
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_settings;
    }
}
