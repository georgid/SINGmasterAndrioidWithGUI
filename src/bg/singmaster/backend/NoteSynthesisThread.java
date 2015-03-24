package bg.singmaster.backend;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * used to synthesize the reference notes code adapted from
 * https://audioprograming
 * .wordpress.com/2012/10/18/a-simple-synth-in-android-step
 * -by-step-guide-using-the-java-sdk/ 
 * TODO: organize in class as in
 * https://gist.github.com/MasterEx/2784265
 * */
public class NoteSynthesisThread extends AudioPlaybackThread {

	// inherited:
	// public AudioTrack mAudioTrack;

	// public int synthSampRate = 16000;
	public int synthSampRate = 8000;
	int amp = 10000;
	double mFrequency;

	public int mBuffSize;

	short[] mSamplesBuffer;

	public NoteSynthesisThread() {

		// set the buffer size
		mBuffSize = AudioTrack.getMinBufferSize(synthSampRate,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		// create an audiotrack object
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, synthSampRate,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				mBuffSize, AudioTrack.MODE_STREAM);

		mSamplesBuffer = new short[mBuffSize];

	}

	public void setFreq(double freq) {
		mFrequency = freq;

	}


	public void writeAudio() {

		double ph = 0.0;
		double twopi = 8. * Math.atan(1.);

		int totalSamplesCount = 0;
		// synth loop
		while (totalSamplesCount <= synthSampRate) { // one second

			ph = generateSamples(ph, twopi);
			mAudioTrack.write(mSamplesBuffer, 0, mBuffSize);

			totalSamplesCount += mBuffSize;
		}
	}
	
	

	/**
	 * generate the samples for one buffer
	 * 
	 * @param ph
	 *            - phase changes continuously from one buffer to another
	 * */
	private double generateSamples(double ph, double twopi) {

		for (int i = 0; i < mBuffSize; i++) {
			mSamplesBuffer[i] = (short) (amp * Math.sin(ph));
			// update phase
			ph += twopi * mFrequency / synthSampRate;
		}
		return ph;
	}

	

}
