package ch.heigvd.gen.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.heigvd.gen.R;
import ch.heigvd.gen.interfaces.ICustomCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment implements ICustomCallback{


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    @Override
    public void update() {

    }
}
