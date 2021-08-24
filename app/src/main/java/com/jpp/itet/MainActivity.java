package com.jpp.itet;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AppCons.GOOGLE_LOGIN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK){
                //Navigate
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.i(AppCons.TAG, currentUser.getDisplayName()+" is logged in");
                NavHostFragment nhf = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                //Read user profile from DB
                //If the user is not yet registered - Go to Registration Fragment
                //If the user registration is not approved - Go to Registration pending Fragment
                //If the user registration approved - Go to Public, Member, Admin or SuperAdmin Dashboard Fragment
                nhf.getNavController().navigate(R.id.action_loginFragment_to_registrationFragment);
            }else{
                //Login was not successful
                // Display the error
                Log.i(AppCons.TAG, " Login failed");
                Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}