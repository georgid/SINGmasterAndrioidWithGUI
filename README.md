

This is current version of SING-master prototype for android. 
For more info about the idea of the project refer to:
https://www.facebook.com/SINGmasterapp/info



Pitch extraction works with AMDF alg from
http://0110.be/posts/TarsosDSP_on_Android_-_Audio_Processing_in_Java_on_Android
(PitchDetector example here: https://github.com/srubin/TarsosDSP)

-----------------------------------------
STATUS:
recording from mic works. pitch extraction works and is done realtime. 
tested under API LEVEL 16 (4.1) with device emulator

IMPORTANT: 
we are using sample rate 8k which only supports in emulator. In device use samplerate to 44.1k for better quality.

SUPPORT for API LEVEL: 
DialogFragment (class MessageDialog) requires API 11.

-------------------------------------------------

DEBUGING: 
To have impression of real-time speed do "run" instead of "debug"
if virtual devices does not start from eclipse, do: 
shell> emulator -avd AVD4.1
or
Eclipse-> virtual device manager -> start AVD4.1 -> mark "wipe user data" on Launch Setting   

if emulator starts, but cannot be detected as running on subsequent launches do: 
adb start-server


PROBLEMS: 


GUI  animation works but timing of color bars is not precise. it has not set priority: only runOnUiThread()
 audio recording thread has setThreadPriority(URGENT_AUDIO)



TarsosDSP is VEEEERY slow! 

