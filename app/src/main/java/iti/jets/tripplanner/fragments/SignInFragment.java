package iti.jets.tripplanner.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import iti.jets.tripplanner.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    Button btnLogin;
    EditText edtEmail, edtPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        btnLogin = view.findViewById(R.id.signIn_btnSingIn);
        edtEmail = view.findViewById(R.id.signIn_edtEmail);
        edtPassword = view.findViewById(R.id.signIn_edtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

            }
        });

        return view;
    }

}
