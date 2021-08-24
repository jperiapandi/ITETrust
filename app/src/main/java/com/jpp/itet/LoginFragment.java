package com.jpp.itet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(AppCons.TAG, "Login Fragment onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //
        //Get current auth state
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null){
            //User has logged in
            //Check whether this user is already registered or not
            //If they are not yet registered ask them to register.
            Log.i(AppCons.TAG, currentUser.getDisplayName()+" is logged in");
            Log.i(AppCons.TAG, currentUser.getUid());
            NavHostFragment nhf = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);


            //Read user profile
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(AppCons.USERS).
                    whereEqualTo(AppCons.UID, currentUser.getUid()).
                    get().
                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.getDocuments().size()==0){
                                    //No user found with matching the currentuser uid
                                    //Registration is needed.
                                    nhf.getNavController().navigate(R.id.action_loginFragment_to_registrationFragment);
                                }else{
                                    //User profile found in database
                                    //Now check whether their registration is approved or not
                                    Map<String, Object> user= documentSnapshot.getDocuments().get(0).getData();
                                    boolean isRegApproved = (boolean) user.get(AppCons.IS_REG_APPROVED);
                                    if(isRegApproved){
                                        //Since user is already approved send them to Dashboard Fragment
                                        String role = (String) user.get(AppCons.ROLE);
                                        switch (role){
                                            case AppCons.MEMBER:
                                                nhf.getNavController().navigate(R.id.action_loginFragment_to_memberDashFragment);
                                                break;
                                            case AppCons.ADMIN:
                                                nhf.getNavController().navigate(R.id.action_loginFragment_to_adminDashFragment);
                                                break;
                                            case AppCons.SUPER_ADMIN:
                                                nhf.getNavController().navigate(R.id.action_loginFragment_to_superAdminDashFragment);
                                                break;
                                            default:
                                                //All other roles
                                                nhf.getNavController().navigate(R.id.action_loginFragment_to_publicDashFragment);
                                                break;
                                        }
                                    }else{
                                        //Registration approval is not yet done. So send them to Registration Pending Fragment
                                        nhf.getNavController().navigate(R.id.action_loginFragment_to_regPendingFragment);
                                    }
                                }
                            }else{
                                Toast.makeText(getContext(), "Reading User data failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }else{
            Log.i(AppCons.TAG, " No one is logged in");
        }
        //
        getActivity().findViewById(R.id.login_google_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Login providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
                // Create and launch sign-in intent
                getActivity().startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .build(),
                        AppCons.GOOGLE_LOGIN);
            }
        });
    }
}