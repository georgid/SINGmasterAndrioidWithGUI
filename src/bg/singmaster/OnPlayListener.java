package bg.singmaster;

import android.view.*;

public class OnPlayListener  implements View.OnClickListener {

	
	
	MainActivity mMainActivity;	
	
	public OnPlayListener(MainActivity mainActivity){
	this.mMainActivity = mainActivity;	
		
	}
	
	@Override
	public void onClick(View v) {
		
		// prepare pitch data for screen dimensions TODO
		
		// load pitch data in graph view TODO
//		artificial data
		 float[][] data1 = {{0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f}, {2.0f, 2.5f, 2.3f, 2.6f, 5.0f, 6.0f, 5.8f, 6.3f, 8.0f, 9.0f, 8.8f, 9.3f}};

	        // The first dataset must be inputted into the graph using setData to replace the placeholder data already there
		 mMainActivity. mVoiceGraph.setData(new float[][][]{data1}, 0, 11, 0, 15);

		
		mMainActivity. mVoiceGraph.setVisibility(View.VISIBLE);
		
		
		// play audio TODO
		
	}

}
