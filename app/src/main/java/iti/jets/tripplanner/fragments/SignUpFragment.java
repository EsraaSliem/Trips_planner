package iti.jets.tripplanner.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

import iti.jets.tripplanner.AuthenticationActivity;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.User;
import iti.jets.tripplanner.utils.Constatnts;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private static final int STORAGE_PERMISSION_CODE = 123;
    FirebaseStorage storage;
    StorageReference storageReference;
    EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword;
    ImageView profileImageView;
    User user;
    boolean fnameValidateFlag = false;
    boolean lnameValidateFlag = false;
    boolean emailValidateFlag = false;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean confirmPasswordFlag = false;
    boolean passwordFalg = false;
    FirebaseAuth mAuth;
    DatabaseReference mRefDatabase;
    //ProgressDialog
    ProgressDialog mRegProgress;
    private int GALLERY = 1, CAMERA = 2;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        context = getActivity();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
        user = new User();
        edtFirstName = view.findViewById(R.id.signUp_edtFirstName);
        edtLastName = view.findViewById(R.id.signUp_edtLastName);
        edtEmail = view.findViewById(R.id.signUp_edtEmail);
        edtPassword = view.findViewById(R.id.signUp_edtPassword);
        profileImageView = view.findViewById(R.id.signUp_imageViewProfile);
        edtConfirmPassword = view.findViewById(R.id.signUp_edtConfirmPassword);

        edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtFirstName.getText().toString().trim().length() <= 0) {
                    edtFirstName.setError("please enter a valid name");
                    fnameValidateFlag = false;
                } else {
                    fnameValidateFlag = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtLastName.getText().toString().trim().length() <= 0) {
                    edtLastName.setError("please enter a valid name");
                    lnameValidateFlag = false;
                } else {
                    lnameValidateFlag = true;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        view.findViewById(R.id.signUp_btnSingUp).setOnClickListener(v -> {
            if (fnameValidateFlag && lnameValidateFlag && passwordFalg && confirmPasswordFlag) {
                user.setfName(edtFirstName.getText().toString());
                user.setlName(edtLastName.getText().toString());
                user.setPassword(edtPassword.getText().toString());
                user.setEmail(edtEmail.getText().toString());
                user.setImage("");
                String confirmPassword = edtConfirmPassword.getText().toString();
                if (filePath != null) {
                    uploadImage();
                } else {
                    mRegProgress = new ProgressDialog(context);
                    mRegProgress.setTitle("Logging");
                    mRegProgress.setMessage("Please Wait While Create Login");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    writeNewUser(user);
                }
            }
        });


        edtEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (edtEmail.getText().toString().matches(emailPattern) && s.length() > 0) {

                    emailValidateFlag = true;
                } else {
                    //Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
                    emailValidateFlag = false;
                    edtEmail.setError("Invalid email");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });


        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtPassword.getText().toString().trim().length() < 6) {
                    edtPassword.setError("Required at least 6 digit");
                    passwordFalg = false;
                } else {
                    passwordFalg = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtConfirmPassword.getText().toString().trim().length() == 0) {
                    edtConfirmPassword.setError("Required");
                    confirmPasswordFlag = false;
                } else if (!edtConfirmPassword.getText().toString().trim().equals(edtPassword.getText().toString().trim())) {
                    edtConfirmPassword.setError("missmatch password");
                    confirmPasswordFlag = false;
                } else {
                    confirmPasswordFlag = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        view.findViewById(R.id.signUp_btnImageView).setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), PICK_IMAGE_REQUEST);
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String imageFullPath = UUID.randomUUID().toString() + ".png";
            StorageReference ref = storageReference.child("images/" + imageFullPath);

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(context, "" + storageReference.child("images").child(imageFullPath).getDownloadUrl().toString(), Toast.LENGTH_SHORT).show();
                        user.setImage(imageFullPath);
                        Constatnts.uri = imageFullPath;
                        writeNewUser(user);
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");

                    });
        }
    }

    public void writeNewUser(User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser current_user = mAuth.getCurrentUser();
                    String uId = current_user.getUid();
                    //Firebase Database
                    mRefDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
                    user.setUserId(uId);
                    mRefDatabase.setValue(user);
                    Intent main_intent = new Intent(context, AuthenticationActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    context.startActivity(main_intent);

                } else mRegProgress.dismiss();
            }
        });
    }
}
