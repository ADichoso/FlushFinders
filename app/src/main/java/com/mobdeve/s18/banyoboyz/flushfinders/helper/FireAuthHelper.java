package com.mobdeve.s18.banyoboyz.flushfinders.helper;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireAuthHelper
{
    private static FireAuthHelper instance = null;

    public static FireAuthHelper getInstance()
    {
        if (instance == null)
        {
            instance = new FireAuthHelper();
        }

        return instance;
    }

    public FireAuthHelper() {}

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void createUser(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful())
                        Log.d("FireAuthHelper", "User created successfully!");
                    else
                        Log.d("FireAuthHelper", task.getException().getMessage());
                });
    }

    public void signInUser(String email, String password, OnCompleteListener<AuthResult> onCompleteListener)
    {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }

    public void signInUser(String email, String password)
    {
        signInUser(email, password, task -> {
            if (task.isSuccessful())
                Log.d("FireAuthHelper", "User Signed In successfully!");
            else
                Log.d("FireAuthHelper", task.getException().getMessage());
        });
    }

    public void verifyUser()
    {
        if (!isCurrentUserSignedIn())
            return;

        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
                Log.d("FireAuthHelper", "Verification Email Sent successfully!");
            else
                Log.d("FireAuthHelper", task.getException().getMessage());
        });
    }

    public void signOutUser()
    {
        auth.signOut();
        Log.d("FireAuthHelper", "User signed out.");
    }

    public boolean isCurrentUserSignedIn()
    {
        return (auth.getCurrentUser() != null);
    }

    public boolean isCurrentUserVerified()
    {
        return auth.getCurrentUser().isEmailVerified();
    }

    public void updateUserPassword(String new_password)
    {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null)
        {
            user.updatePassword(new_password).addOnCompleteListener(task ->
            {
                if (task.isSuccessful())
                    Log.d("FireAuthHelper", "User password updated successfully!");
                else
                    Log.d("FireAuthHelper", task.getException().getMessage());
            });
        }
        else
        {
            Log.d("FireAuthHelper", "No user is signed in.");
        }
    }

    public void sendPasswordResetEmail(String email)
    {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                Log.d("FireAuthHelper", "Password reset email sent successfully to: " + email);
            }
            else
            {
                Log.d("FireAuthHelper", "Failed to send password reset email: " + task.getException().getMessage());
            }
        });
    }
}
