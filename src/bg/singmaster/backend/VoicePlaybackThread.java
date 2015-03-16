package bg.singmaster.backend;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;

public class VoicePlaybackThread extends AudioPlaybackThread{

	public AudioProcessor mAudioProcessor;

	/**
	 * create playback track complying to the params used for audio recording
	 * */
public VoicePlaybackThread(AudioProcessor audioProc){
	
	mAudioProcessor = audioProc;
	AudioRecord audioRecord = audioProc.mRecorder;
	
	int channelConf = AudioFormat.CHANNEL_OUT_MONO;
	// create an audio track object
	if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_IN_MONO)
			channelConf = AudioFormat.CHANNEL_OUT_MONO;
	else
			channelConf = AudioFormat.CHANNEL_OUT_STEREO;

	
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				audioRecord.getSampleRate(), channelConf,
                              audioRecord.getAudioFormat(), audioProc.mBufferSize,
                              AudioTrack.MODE_STREAM);
 
}
	
public void writeAudio() {

	
		for (byte [] currAudioBuffer : this.mAudioProcessor.mRecordedAudio){
			
			mAudioTrack.write(currAudioBuffer, 0, currAudioBuffer.length);
	
		}

}

}