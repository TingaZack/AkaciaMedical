package com.android.msqhealthpoc1.fragments.profile.create;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.msqhealthpoc1.R;
import com.android.msqhealthpoc1.activities.WelcomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileOnboardingExplainerFragment extends Fragment {

    Button btnProceed;


    public ProfileOnboardingExplainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_onboarding_explainer, container, false);

        btnProceed = (Button) view.findViewById(R.id.proceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WelcomeActivity) getActivity()).moveToNext();
            }
        });

        return view;
    }

}
