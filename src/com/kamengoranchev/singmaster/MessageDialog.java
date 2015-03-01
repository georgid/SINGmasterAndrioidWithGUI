package com.kamengoranchev.singmaster;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

@TargetApi(11)
public class MessageDialog  extends DialogFragment{

	public String infoMessage;
	public MessageDialog(String infoMessage){
		this.infoMessage = infoMessage;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
			 
			// Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(this.infoMessage);
	        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                // User clicked OK button
	            }
	        });
	        
	        // Create the AlertDialog object and return it
	        return builder.create();
	}
	
	
}

