package bg.singmaster.backend;

import android.os.Environment;

public class Parameters {
	// recording sample rate
	public static final int SAMPLE_RATE = 8000;
//	 public static final int SAMPLE_RATE = 16000;

//	public static final int SAMPLE_RATE = 44100;

	
	// in seconds. depends on tempo
	public static final int RECORDING_DURATION = 3; 
	
	// initial wait time in seconds
	public static final int INITIAL_WAIT_TIME = 0;
	
	public static final String FILE_URI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/singMasterRecorded.wav";

}
