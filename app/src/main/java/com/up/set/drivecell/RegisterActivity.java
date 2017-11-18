package com.up.set.drivecell;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class RegisterActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";

    Button btnRegister;
    EditText txtEmail,txtPass,txtConfirmPass,txtUserName,txtPhoneNo;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        initViews();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth=FirebaseAuth.getInstance();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Register User

                signUp();


            }
        });

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    private void signUp() {
        final String email=txtEmail.getText().toString().trim();
        String password=txtPass.getText().toString().trim();
        final String userName=txtUserName.getText().toString().trim();
        String confirmPass=txtConfirmPass.getText().toString().trim();
        final String phoneNo=txtPhoneNo.getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(userName) && !TextUtils.isEmpty(confirmPass))
        {
            if(!confirmPass.equals(password))
            {
                Toast.makeText(this, "Please Confirm your Correct Password", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();
                                }

                                else
                                {

                                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users").push();
                                    ref.child("userEmail").setValue(email);
                                    ref.child("userName").setValue(userName);
                                    ref.child("userPhotoUrl").setValue("");

                                    ref.child("userPhoneNo").setValue(phoneNo);

                                    StyleableToast st=new StyleableToast(getApplicationContext(),"Good! Welcome",Toast.LENGTH_SHORT);
                                    st.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                    st.setTextColor(Color.WHITE);
                                    st.setIcon(R.drawable.ic_account);
                                    st.show();
                                    Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                                    intent.putExtra("userName",userName);
                                    startActivity(intent);

                                }

                                // ...
                            }
                        });



            }


        }
        else
        {
            Toast.makeText(this, "Please fill out all the Fields!", Toast.LENGTH_SHORT).show();
        }





    }





    private void initViews() {
        btnRegister=(Button)findViewById(R.id.btnRegister);
        txtEmail=(EditText) findViewById(R.id.txt_email);
        txtUserName=(EditText) findViewById(R.id.txt_userName);
        txtPass=(EditText) findViewById(R.id.txt_pass);
        txtConfirmPass=(EditText) findViewById(R.id.txt_confirm_pass);
        txtPhoneNo=(EditText)findViewById(R.id.txt_phoneNo);
    }
}
