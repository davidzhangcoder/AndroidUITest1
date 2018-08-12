package com.test1.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test1.R;
import com.test1.databinding.FragmentMainLeftPanelBinding;

public class MainLeftPanelFragment extends Fragment
{

    private FragmentMainLeftPanelBinding binding;

    private OnFragmentInteractionListener mListener;

    public MainLeftPanelFragment() {
        // Required empty public constructor
    }

    public static MainLeftPanelFragment newInstance() {
        MainLeftPanelFragment fragment = new MainLeftPanelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentMainLeftPanelBinding.inflate( inflater ,container , false );

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public FragmentMainLeftPanelBinding getBinding() {
        return binding;
    }
}
