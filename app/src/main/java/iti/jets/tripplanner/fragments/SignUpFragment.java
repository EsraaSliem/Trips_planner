package iti.jets.tripplanner.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.User;
import iti.jets.tripplanner.utils.Constatnts;
import iti.jets.tripplanner.utils.FireBaseData;

import static android.app.Activity.RESULT_OK;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Bitmap bitmap;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bundle extras;
    EditText edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword;
    ImageView profileImageView;
    User user;
    FireBaseData fireBaseData;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        context = getActivity();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = new User();
        edtFirstName = view.findViewById(R.id.signUp_edtFirstName);
        edtLastName = view.findViewById(R.id.signUp_edtLastName);
        edtEmail = view.findViewById(R.id.signUp_edtEmail);
        edtPassword = view.findViewById(R.id.signUp_edtPassword);
        profileImageView = view.findViewById(R.id.signUp_imageViewProfile);
        edtConfirmPassword = view.findViewById(R.id.signUp_edtConfirmPassword);
        fireBaseData = new FireBaseData(getActivity());

        view.findViewById(R.id.signUp_btnSingUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setfName(edtFirstName.getText().toString());
                user.setlName(edtLastName.getText().toString());
                user.setPassword(edtPassword.getText().toString());
                user.setEmail(edtEmail.getText().toString());
                String confirmPassword = edtConfirmPassword.getText().toString();
                if (user.getPassword().equals(confirmPassword)) {
                    uploadImage();
                } else {
                    Toast.makeText(getActivity(), "password is not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
        view.findViewById(R.id.signUp_btnImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
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
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String imageFullPath = UUID.randomUUID().toString()+".png";
            StorageReference ref = storageReference.child("images/"+imageFullPath );


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(context, ""+ storageReference.child("images" ).child(imageFullPath).getDownloadUrl().toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getContext(), "Uploaded"+uri.toString(), Toast.LENGTH_LONG).show();
                            user.setImage(imageFullPath);
                            Constatnts.uri=imageFullPath;
                            fireBaseData.writeNewUser(user);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());


                            progressDialog.setMessage("Uploaded "+(int)progress+"%");

                        }
                    });
        }
    }
}
