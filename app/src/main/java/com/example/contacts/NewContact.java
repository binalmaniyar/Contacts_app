package com.example.contacts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class NewContact extends AppCompatActivity {

    EditText etName, etPhone, etMail;
    Button btnNewContact;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);


        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etMail);
        btnNewContact = findViewById(R.id.btnNewContact);

        btnNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etName.getText().toString().isEmpty() || etPhone.getText().toString().isEmpty() || etMail.getText().toString().isEmpty())
                {
                    Toast.makeText(NewContact.this, "Please enter all fields..!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String name = etName.getText().toString().trim();
                    String mail = etMail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();

                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setEmail(mail);
                    contact.setPhone(phone);
                    contact.setUserEmail(ApplicationClass.user.getEmail());

                    showProgress(true);
                    tvLoad.setText("Creating new contact..Please wait..");

                    Backendless.Persistence.save(contact, new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {

                            Toast.makeText(NewContact.this, "New contact saved successfully..", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                            etName.setText("");
                            etMail.setText("");
                            etPhone.setText("");

                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(NewContact.this, "Error : " + fault, Toast.LENGTH_SHORT).show();
                            showProgress(false);

                        }
                    });

                }

            }
        });

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
