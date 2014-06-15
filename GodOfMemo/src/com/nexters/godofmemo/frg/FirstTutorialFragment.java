package com.nexters.godofmemo.frg;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nexters.godofmemo.R;

public class FirstTutorialFragment extends Fragment {

	public static FirstTutorialFragment create(int pageNumber) {
		FirstTutorialFragment fragment = new FirstTutorialFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_first_tutorial,
				container, false);
		return rootView;
	}
}
