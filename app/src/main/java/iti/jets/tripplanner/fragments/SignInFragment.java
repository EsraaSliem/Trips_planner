package iti.jets.tripplanner.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.utils.FireBaseData;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    Button btnLogin;
    EditText edtEmail, edtPassword;
    FireBaseData fireBaseData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        btnLogin = view.findViewById(R.id.signUp_btnSingUp);
        edtEmail = view.findViewById(R.id.signIn_edtEmail);
        edtPassword = view.findViewById(R.id.signUp_edtPassword);
        fireBaseData = new FireBaseData(getActivity());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                fireBaseData.loginUser(email, password);
            }
        });

        return view;
    }

}
