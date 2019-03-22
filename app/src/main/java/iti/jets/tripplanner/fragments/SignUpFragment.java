package iti.jets.tripplanner.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.User;
import iti.jets.tripplanner.utils.FireBaseData;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private static final int RESULT_LOAD_IMG = 100;
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
                    if (user.getImage() == null) {
                        user.setImage(convertBitmapToBase64(
                                BitmapFactory.decodeResource(getResources(), R.drawable.profile)));
                    }
                    fireBaseData.writeNewUser(user);
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
                startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), RESULT_LOAD_IMG);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (reqCode == RESULT_LOAD_IMG) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                user.setImage(convertBitmapToBase64(selectedImage));
                profileImageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(context, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap convertBase64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
