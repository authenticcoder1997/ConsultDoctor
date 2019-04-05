package com.abhishek360.dev.hellodoctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity
{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    private Toast mToast;


    private EditText mUser,mEmail,mPass,confPassEditText,mQual,mSpecs;
    private Button mBLoginin,mBSignup;
    private CheckBox isDocCheck;
    private ProgressBar detailsProgressbar;
    private RadioGroup radioGroup;
    public static String spKey="docsSP";
    public static String spFullNameKey="FULL_NAME";
    public static String spEmailKey="EMAIL";
    public static  String spIsLoggedIn="IS_USER_LOGGED_IN";
    public static String spUID="UID";
    public static String spIsDoc="IS_DOC";
    private String email_string,fullname_string,pass_string, confpass_string;
    private boolean male=false,isDoc = false;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseAuth=FirebaseAuth.getInstance();
        mToast=new Toast(this);

        sharedPreferences= getSharedPreferences(spKey,MODE_PRIVATE);



        mUser=findViewById(R.id.s_user);
        mEmail=findViewById(R.id.s_email);
        mPass=findViewById(R.id.s_pass);

        mBSignup=findViewById(R.id.s_signup);
        mBLoginin=findViewById(R.id.s_login);
        radioGroup=findViewById(R.id.s_radio_group);
        confPassEditText=findViewById(R.id.s_cofpass_edittext);
        detailsProgressbar=findViewById(R.id.reg_details_progressbar);
        isDocCheck=(CheckBox) findViewById(R.id.s_is_doc);
        mQual=findViewById(R.id.s_qual_edittext);
        mSpecs=findViewById(R.id.s_speciality_edittext);

        isDocCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked&&(mUser.getVisibility()==View.VISIBLE))
                {
                    mQual.setVisibility(View.VISIBLE);
                    mSpecs.setVisibility(View.VISIBLE);

                }
                else
                {
                    mQual.setVisibility(View.GONE);
                    mSpecs.setVisibility(View.GONE);

                }

            }
        });

        mBLoginin.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String n,p;
                n = mEmail.getText().toString();
                p = mPass.getText().toString();
                if("".equals(n))
                    d("Username required");
                else if("".equals(p))
                    d("Password required");
                else
                {
                    isDoc=isDocCheck.isChecked();
                    new ValidateLogin(n,p).execute();

                }
            }
        });

        mBSignup.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(mUser.getVisibility()==View.GONE)
                {
                    mUser.setVisibility(View.VISIBLE);
                    confPassEditText.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);

                }
                else
                {
                    email_string=mEmail.getText().toString();
                    fullname_string=mUser.getText().toString();
                    pass_string=mPass.getText().toString();
                    confpass_string=confPassEditText.getText().toString();
                    validateDetails();
                }

            }
        });

    }

    private void validateDetails()
    {

        if(fullname_string.isEmpty())
        {
            mUser.setError("Name is Required");
            mUser.requestFocus();
            return;
        }

        if(email_string.isEmpty())
        {
            mEmail.setError("Email is Required");
            mEmail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email_string).matches())
        {
            mEmail.setError("Please,Enter a valid  Email.");
            mEmail.requestFocus();
            return;
        }

        if(pass_string.isEmpty())
        {
            mPass.setError("Password is Required");
            mPass.requestFocus();
            return;
        }
        else if(pass_string.length()<6)
        {
            mPass.setError("Min. password length is 6");
            mPass.requestFocus();
            return;
        }

        if(confpass_string.isEmpty())
        {
            confPassEditText.setError("Password not matched");
            confPassEditText.requestFocus();
            return;
        }
        else if(!pass_string.equals(confpass_string))
        {
            confPassEditText.setError("Password not matched");
            confPassEditText.requestFocus();
            return;
        }
        else
        {
            detailsProgressbar.setVisibility(View.VISIBLE);
            isDoc=isDocCheck.isChecked();
            registerUser();

        }
    }

    private void registerUser()
    {

        firebaseAuth.createUserWithEmailAndPassword(email_string,pass_string).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Log.d("Register Page:", "User Id created");
                    d("Registered Successfully!");
                    firebaseUser = firebaseAuth.getCurrentUser();
                    final String uid = firebaseUser.getUid();

                    if (firebaseUser != null)
                    {

                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder().
                                setDisplayName(fullname_string).build();

                        firebaseUser.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Log.d("Profile Name:", "Name saved");

                                    d("Registration Successful! Please Login using the credentials.");

                                    if(isDoc)
                                    {

                                           docRegister(uid);

                                    }
                                    else
                                    {
                                        detailsProgressbar.setVisibility(View.GONE);
                                        finish();
                                        startActivity(getIntent());

                                    }

                                    spEditor = sharedPreferences.edit();
                                    spEditor.putString("FULL_NAME", fullname_string);
                                    spEditor.putString("EMAIL", email_string);
                                    spEditor.putString("UID", uid);
                                    spEditor.putBoolean("IS_USER_LOGGED_IN", true);
                                    spEditor.putBoolean(spIsDoc,isDoc);
                                    spEditor.apply();
                                }
                                else Log.d("Profile Name: ERROR :", "Name not saved");
                            }
                        });
                    }

                } else
                {
                    d("Register Error: " + task.getException().getMessage());
                    detailsProgressbar.setVisibility(View.GONE);
                }
            }


        });




    }

    void docRegister(final String uid)
    {
        Map<String, Object> user = new HashMap<>();

        user.put("name",fullname_string);
        user.put("qual",mQual.getText().toString());
        user.put("specs", mSpecs.getText().toString());
        user.put("docID",uid);
        firebaseFirestore.collection("/DOCTORS/").document(uid).set(user).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d("doc sign Up ","done");
                        detailsProgressbar.setVisibility(View.GONE);
                        finish();
                        startActivity(getIntent());
                    }
                }
        )
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Log.e("doc Sign Up error", e.getMessage());
                        docRegister(uid);

                    }
                });
    }

    public void setGender(View v)
    {
        switch(v.getId())
        {
            case R.id.f:
                male=false;
                break;
            case R.id.m:
                male=true;
        }
    }

    private class ValidateLogin extends AsyncTask<Void,Void,Void>
    {
        private String data,error, email,password;
        private URL url;

        public ValidateLogin(String n,String p)
        {
            email = n;
            password=p;


        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            detailsProgressbar.setVisibility(View.VISIBLE);
            mBLoginin.setClickable(false);
            mBSignup.setClickable(false);


        }

        @Override
        protected void onPostExecute(Void result)
        {

            super.onPostExecute(result);

            if(sharedPreferences.getBoolean(spIsLoggedIn,false))
            {
                if(isDoc)
                {
                    Intent i = new Intent(LoginActivity.this,ChatHistoryActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
            mBLoginin.setClickable(true);
            mBSignup.setClickable(true);
            detailsProgressbar.setVisibility(View.GONE);

        }

        @Override
        protected Void doInBackground(Void[] p1)
        {
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {

                    if(task.isSuccessful())
                    {
                        firebaseUser=firebaseAuth.getCurrentUser();
                        fullname_string=firebaseUser.getDisplayName();
                        email_string=firebaseUser.getEmail();
                        final String uid_string= firebaseUser.getUid();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("FULL_NAME",fullname_string);
                        editor.putString("EMAIL",email_string);
                        editor.putString("UID",uid_string);
                        editor.putBoolean("IS_USER_LOGGED_IN",true);
                        editor.putBoolean(spIsDoc,isDoc);
                        editor.apply();
                        //setResult(RESULT_OK);
                        //finish();

                    }
                    else
                    {
                        mPass.requestFocus();
                        d("Login Failed: "+ task.getException().getMessage());
                    }
                }
            });
            return null;
        }

    }

    private void d(String s)
    {
        mToast.cancel();
        mToast= Toast.makeText(this,s,Toast.LENGTH_LONG);
        mToast.show();
    }
}
