package com.example.contacts;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ContactInfo extends AppCompatActivity {

    EditText etName, etMail, etTel;
    TextView tvChar, tvName;
    ImageView ivCall,ivMail,ivDelete,ivEdit;
    Button btnSubmit;

    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        tvLoad = findViewById(R.id.tvLoad);

        etName = findViewById(R.id.etName);
        etMail = findViewById(R.id.etMail);
        etTel = findViewById(R.id.etTel);

        tvChar = findViewById(R.id.tvChar);
        tvName = findViewById(R.id.tvName);

        ivCall = findViewById(R.id.ivCall);
        ivDelete = findViewById(R.id.ivDelete);
        ivEdit = findViewById(R.id.ivEdit);
        ivMail = findViewById(R.id.ivMail);

        btnSubmit = findViewById(R.id.btnSubmit);

        etName.setVisibility(View.GONE);
        etTel.setVisibility(View.GONE);
        etMail.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);


        final int index = getIntent().getIntExtra("index",0);

        etName.setText(ApplicationClass.contacts.get(index).getName());
        etMail.setText(ApplicationClass.contacts.get(index).getEmail());
        etTel.setText(ApplicationClass.contacts.get(index).getPhone());

        tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
        tvName.setText(ApplicationClass.contacts.get(index).getName());

        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = "tel:" + ApplicationClass.contacts.get(index).getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);

            }
        });

        ivMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL,ApplicationClass.contacts.get(index).getEmail());
                startActivity(Intent.createChooser(intent,"Send mail to " + ApplicationClass.contacts.get(index).getEmail()));

            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit = !edit;

                if(edit)
                {
                    etName.setVisibility(View.VISIBLE);
                    etTel.setVisibility(View.VISIBLE);
                    etMail.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                }
                else
                {
                    etName.setVisibility(View.GONE);
                    etTel.setVisibility(View.GONE);
                    etMail.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.GONE);
                }

            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder dialog = new AlertDialog.Builder(ContactInfo.this);
                dialog.setMessage("Are you sure you want to delete this contact ?");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showProgress(true);
                        tvLoad.setText("Deleting contact..Please wait..!!");

                        Backendless.Persistence.of(Contact.class).remove(ApplicationClass.contacts.get(index), new AsyncCallback<Long>() {
                            @Override
                            public void handleResponse(Long response) {

                                ApplicationClass.contacts.remove(index);
                                Toast.makeText(ContactInfo.this, "Contact successfully deleted..", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                ContactInfo.this.finish();

                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {

                                Toast.makeText(ContactInfo.this, "Error : " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                showProgress(false);

                            }
                        });
                    }
                });

                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etName.getText().toString().isEmpty() || etMail.getText().toString().isEmpty() || etTel.getText().toString().isEmpty())
                {
                    Toast.makeText(ContactInfo.this, "Please enter all details !!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ApplicationClass.contacts.get(index).setName(etName.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setEmail(etMail.getText().toString().trim());
                    ApplicationClass.contacts.get(index).setPhone(etTel.getText().toString().trim());

                    showProgress(true);
                    tvLoad.setText("Updating contact..Please wait..!!");

                    Backendless.Persistence.save(ApplicationClass.contacts.get(index), new AsyncCallback<Contact>() {
                        @Override
                        public void handleResponse(Contact response) {

                            tvChar.setText(ApplicationClass.contacts.get(index).getName().toUpperCase().charAt(0)+"");
                            tvName.setText(ApplicationClass.contacts.get(index).getName());
                            Toast.makeText(ContactInfo.this, "Contact successfully updated..!!", Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {

                            Toast.makeText(ContactInfo.this, "Error : " + fault.getMessage(), Toast.LENGTH_SHORT).show();
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
