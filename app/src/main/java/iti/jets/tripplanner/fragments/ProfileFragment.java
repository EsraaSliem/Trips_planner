package iti.jets.tripplanner.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.interfaces.UserInt;
import iti.jets.tripplanner.pojos.User;
import iti.jets.tripplanner.utils.FireBaseData;


public class ProfileFragment extends Fragment implements UserInt {


    public ProfileFragment() {
        // Required empty public constructor
    }

    EditText firstNameTxt, lastNameTxt, emailTxt, passTxt;
    CircleImageView image;
    Button editBtn;
    FireBaseData fireBaseData;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fireBaseData = new FireBaseData(context);
        fireBaseData.getUser(ProfileFragment.this);
    }


    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        user = new User();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firstNameTxt = (EditText) view.findViewById(R.id.fragment_profile_user_firstName);
        lastNameTxt = (EditText) view.findViewById(R.id.fragment_profile_user_lastName);
        emailTxt = (EditText) view.findViewById(R.id.fragment_profile_user_email);
        passTxt = (EditText) view.findViewById(R.id.fragment_profile_user_pass);
        image = (CircleImageView) view.findViewById(R.id.fragment_profile_user_img);
        editBtn = (Button) view.findViewById(R.id.fragment_profile_edit);
        disableTextFields();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editBtn.getText().equals("edit")) {
                    enableTextFields();
                    editBtn.setText("save");
                } else {


                    user.setEmail(emailTxt.getText().toString());
                    user.setPassword(passTxt.getText().toString());
                    user.setfName(firstNameTxt.getText().toString());
                    user.setlName(lastNameTxt.getText().toString());
                    fireBaseData.updateUser(user);
                    disableTextFields();
                    editBtn.setText("edit");
                }
            }
        });

        return view;
    }

    public void disableTextFields() {
        firstNameTxt.setEnabled(false);
        lastNameTxt.setEnabled(false);
        passTxt.setEnabled(false);
        emailTxt.setEnabled(false);
    }

    public void enableTextFields() {
        firstNameTxt.setEnabled(true);
        lastNameTxt.setEnabled(true);
        passTxt.setEnabled(true);
        emailTxt.setEnabled(true);
    }

    @Override
    public void getUser(User _user) {

        firstNameTxt.setText(_user.getfName());
        lastNameTxt.setText(_user.getlName());
        emailTxt.setText(_user.getEmail());
        passTxt.setText(_user.getPassword());
        user.setUserId(_user.getUserId());
        Toast.makeText(context, "" + _user.getfName(), Toast.LENGTH_SHORT).show();
    }
}
