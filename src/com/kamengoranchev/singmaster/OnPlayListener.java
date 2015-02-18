package com.kamengoranchev.singmaster;

import java.util.ArrayList;

import bg.singmaster.backend.DetectedPitch;
import bg.singmaster.gui.util.GUIParameters;
import android.util.Log;
import android.view.*;

public class OnPlayListener  implements View.OnClickListener {

	
	
	MainActivity mMainActivity;
	float [] mPitchSeriesTimes;
	float [] mPitchSeriesMIDIs;
	
	
	public OnPlayListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;	
	
	}
	
	@Override
	public void onClick(View v) {
		
		
			
		ArrayList<DetectedPitch> pitchSeries = mMainActivity.mAudioProcessor.mPitchExtractor.mDetectedPitchArray;
//		ArrayList<DetectedPitch> pitchSeries = createTestPitchSeries();
		
		if (pitchSeries.size() == 0){Log.e("TAG", "no pitch detected.");
		// TODO runOngui. no pitch detected.
			System.exit(1);
			}
		
		
		// prepare pitch data for screen dimensions TODO
		pitchSeries2graphData(pitchSeries);
		 
		// load pitch data in graph view TODO
		float[][] data1 = {mPitchSeriesTimes, mPitchSeriesMIDIs };

	        // The first dataset must be input into the graph using setData to replace the placeholder data already there
		 mMainActivity. mVoiceGraph.setData(new float[][][]{data1},  GUIParameters.minTime, GUIParameters.maxTime, GUIParameters.minPitch, GUIParameters.maxPitch);

		
		mMainActivity. mVoiceGraph.setVisibility(View.VISIBLE);
		
		
		// play audio TODO
		
	}

	
	/**
	 * turn pitches into x-array and y-array to be ready for visualization display
	 * */
	public void pitchSeries2graphData(ArrayList<DetectedPitch> pitchSeries){
		int sizePitchSeries = pitchSeries.size();
		
		this.mPitchSeriesTimes = new float[sizePitchSeries];
		this.mPitchSeriesMIDIs = new float[sizePitchSeries];
		
		for(int i = 0; i < sizePitchSeries; i++) {
			double currTs = pitchSeries.get(i).mTimeStamp;
			this.mPitchSeriesTimes[i] = (float)currTs;
			
			double currPitchMIDI = pitchSeries.get(i).mPitchScaleNumber;
			this.mPitchSeriesMIDIs[i] = (float)currPitchMIDI;
			
		}
			
	}
	/**
	 * stub method: test pitch series to visualize from onClick
	 * */
	public ArrayList<DetectedPitch>  createTestPitchSeries(){
		ArrayList<DetectedPitch> pistSeries = new ArrayList<DetectedPitch>();
		
		DetectedPitch detPitch= new DetectedPitch(68.0f, 0.02);
		detPitch.setPitchScaleNumber(80.0);
		pistSeries.add(detPitch);
		
		DetectedPitch detPitch2= new DetectedPitch(68.0f, 1.5);
		detPitch2.setPitchScaleNumber(75.0);
		pistSeries.add(detPitch2);

		
		DetectedPitch detPitch3= new DetectedPitch(68.0f, 2.02);
		detPitch3.setPitchScaleNumber(77.0);
		pistSeries.add(detPitch3);
		
		return pistSeries;
		
	}

}
