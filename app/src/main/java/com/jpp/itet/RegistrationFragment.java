package com.jpp.itet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialCalendar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistrationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistrationFragment newInstance(String param1, String param2) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_registration, container, false);

        //Set the First name and last name default vlaues
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextInputLayout fnTxtLayout = (TextInputLayout) view.findViewById(R.id.firstname_txtLayout);
        TextInputLayout lnTxtLayout = (TextInputLayout) view.findViewById(R.id.lastname_txtLayout);
        TextInputLayout addrLn1TxtLayout = (TextInputLayout) view.findViewById(R.id.addressLn1_txtLayout);
        TextInputLayout localityTxtLayout = (TextInputLayout) view.findViewById(R.id.locality_txtLayout);
        TextInputLayout distTxtLayout = (TextInputLayout) view.findViewById(R.id.district_txtLayout);

        final Long[] dobSelection = new Long[1];

        //Show Date Picker when DOB field is clicked
        MaterialDatePicker<Long> matDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getActivity().getResources().getString(R.string.dob))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        TextInputLayout dobTxtLayout = (TextInputLayout) view.findViewById(R.id.dob_txtLayout);
        dobTxtLayout.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matDatePicker.show(getActivity().getSupportFragmentManager(), AppCons.TAG);
            }
        });
        matDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                dobSelection[0] = selection;
                dobTxtLayout.getEditText().setText(matDatePicker.getHeaderText());
            }
        });

        //Populate the Genders Dropdown items
        String[] genders = getActivity().getResources().getStringArray(R.array.genders);
        ArrayAdapter<String> gendersAdapter = new ArrayAdapter<String>(requireContext(), R.layout.genders_ddl_item, genders);
        AutoCompleteTextView genderTextView = (AutoCompleteTextView) view.findViewById(R.id.genderTextView);
        genderTextView.setAdapter(gendersAdapter);

        //Populate the Qualifications Dropdown items
        String[] qualifications =getActivity().getResources().getStringArray(R.array.qualifications);
        ArrayAdapter<String> qualiAdapter = new ArrayAdapter<String>(requireContext(), R.layout.genders_ddl_item, qualifications);
        AutoCompleteTextView qualiTextView = (AutoCompleteTextView) view.findViewById(R.id.qualificationTextView);
        qualiTextView.setAdapter(qualiAdapter);

        //Watch for Mobile number errors
        TextInputLayout mobileNumTxtLayout = (TextInputLayout) view.findViewById(R.id.mob_num_txtLayout);
        mobileNumTxtLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>10){
                    mobileNumTxtLayout.setError(getActivity().getResources().getString(R.string.error_mobnum_maxsize));
                }else{
                    mobileNumTxtLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Watch for Postal code errors
        TextInputLayout postalcodeTxtLayout = (TextInputLayout) view.findViewById(R.id.postalcode_txtLayout);
        postalcodeTxtLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>6){
                    postalcodeTxtLayout.setError(getActivity().getResources().getString(R.string.error_postcode_maxsize));
                }else{
                    postalcodeTxtLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Add lister for Submit button
        Button submitBtn = view.findViewById(R.id.reg_submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Collect the user data and save it in DB
                Map<String, Object> user = new HashMap<>();
                user.put(AppCons.FIRST_NAME, fnTxtLayout.getEditText().getText().toString());
                user.put(AppCons.LAST_NAME, lnTxtLayout.getEditText().getText().toString());
                user.put(AppCons.DOB, new Timestamp(dobSelection[0]));
                user.put(AppCons.GENDER, genderTextView.getText().toString());
                user.put(AppCons.QUALIFICATION, qualiTextView.getText().toString());
                user.put(AppCons.MOBILE_NUMBER, mobileNumTxtLayout.getEditText().getText().toString());
                user.put(AppCons.POSTAL_CODE, postalcodeTxtLayout.getEditText().getText().toString());
                user.put(AppCons.ADDRESS_LINE1, addrLn1TxtLayout.getEditText().getText().toString());
                user.put(AppCons.LOCALITY, localityTxtLayout.getEditText().getText().toString());
                user.put(AppCons.DISTRICT, distTxtLayout.getEditText().getText().toString());
                //default values
                user.put(AppCons.UID, currentUser.getUid());
                user.put(AppCons.IS_REG_APPROVED, false);
                user.put(AppCons.REG_APPROVED_ON, null);
                user.put(AppCons.ROLE_REQUESTED, "public");
                user.put(AppCons.ROLE_APPROVED, null);
                user.put(AppCons.REG_REQUESTED_ON, new Timestamp(new Date().getTime()));
                Log.i(AppCons.TAG, "New User Data "+user);
                //Save in DB
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(AppCons.USERS)
                        .document(currentUser.getUid())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Request Submitted !", Toast.LENGTH_SHORT).show();
                                NavHostFragment nhf = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                                nhf.getNavController().navigate(R.id.action_registrationFragment_to_regPendingFragment);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to save your profile. Please try again.", Toast.LENGTH_SHORT).show();
                                Log.e(AppCons.TAG, e.getMessage(), e);
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(AppCons.TAG, "Registration Fragment created");
        TextView userNameTxt = (TextView) getActivity().findViewById(R.id.reg_username);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userNameTxt.setText(currentUser.getDisplayName());
    }
}