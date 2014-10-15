package com.bifi.cellspottingapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Class that implements the Dialog Fragments
 */
public class CellSpottingDialog extends DialogFragment {
	
	byte dialogToOpen;
	
	public CellSpottingDialog (byte dialogId){

		dialogToOpen = dialogId;
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view;
		
		if (dialogToOpen == 0) {
			view = inflater.inflate(R.layout.cell_spotting_general_info_dialog, container);
			getDialog().setTitle(R.string.general_info_title);
		}
		else if (dialogToOpen == 1) {
			view = inflater.inflate(R.layout.cell_spotting_alive_step_dialog, container);
			
			
// Add images
			
			
			getDialog().setTitle(R.string.alive_step_info_title);
		}
		else {
			view = inflater.inflate(R.layout.cell_spotting_release_step_dialog, container);
			
			
// Add images
			
			
			
			getDialog().setTitle(R.string.release_step_info_title);
		}
		return view;
	}
}