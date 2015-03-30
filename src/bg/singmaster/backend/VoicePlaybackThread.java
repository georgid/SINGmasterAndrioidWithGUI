package bg.singmaster.backend;

import java.io.ByteArrayInputStream;
import java.util.logging.Logger;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

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
                              audioRecord.getAudioFormat(), mAudioProcessor.mBufferSize,
                              AudioTrack.MODE_STREAM);
 
}
	
public void writeAudio() {
	
	if (mAudioProcessor.mAudioOutStream == null){
		Log.d(VoicePlaybackThread.class.getName(), "no audio to play in mAudioProcessor.mAudioOutStream ");
		return;
	}
	
	byte [] rawBytes = mAudioProcessor.mAudioOutStream.toByteArray();
	ByteArrayInputStream audioIputStream = new ByteArrayInputStream(rawBytes);
		
	int numBytesRead;
	byte [] currAudioBuffer = new byte[mAudioProcessor.mBufferSize];
	
	
	while ( (numBytesRead = audioIputStream.read(currAudioBuffer, 0, mAudioProcessor.mBufferSize)) != -1 ){
			
			mAudioTrack.write(currAudioBuffer, 0, numBytesRead);
	}

}

}