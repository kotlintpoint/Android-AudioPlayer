# Android-AudioPlayer
Read Songs From Device and Play


**Play local raw resource (application's res/raw/ directory)**
```
MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sound_file_1);
mediaPlayer.start(); // no need to call prepare(); create() does that for you
```

**Play from URI available in locally system(obtained through a Content Resolver)**
```
Uri myUri = ....; // initialize Uri here
MediaPlayer mediaPlayer = new MediaPlayer();
mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
mediaPlayer.setDataSource(getApplicationContext(), myUri);
mediaPlayer.prepare();
mediaPlayer.start();
```

**Play from Remote URL via HTTP**
```
String url = "http://........"; // your URL here
MediaPlayer mediaPlayer = new MediaPlayer();
mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
mediaPlayer.setDataSource(url);
mediaPlayer.prepare(); // might take long! (for buffering, etc)
mediaPlayer.start();
```
**Releasing MediaPlayer**
> A MediaPlayer can consume valuable system resources. Therefore, you should always take extra precautions to make sure you are not hanging on to a MediaPlayer instance longer than necessary. 
```
mediaPlayer.release();
mediaPlayer = null;
```

**Create Handler for Timer**
```
Handler mHandler=new Handler();

if(mediaPlayer==null)
{
	...
	updateProgressBar();
	...
}
...
public void updateProgressBar()
    {
        mHandler.postDelayed(mUpdateTimerTask,1000);
    }

 private Runnable mUpdateTimerTask=new Runnable() {
        @Override
        public void run() {
            long totalDuration=mediaPlayer.getDuration();
            long currentDuration=mediaPlayer.getCurrentPosition();

            songTotalDurationLabel.setText(milliSecondsToTimer(totalDuration));
            songCurrentDurationLabel.setText(milliSecondsToTimer(currentDuration));

            mHandler.postDelayed(this,1000);

        }
    };

private String milliSecondsToTimer(String songDuration) {
        int duration=Integer.parseInt(songDuration);
        int hour=(int)(duration/(1000*60*60));
        int minute=(int)((duration%(1000*60*60))/(1000*60));
        int seconds=(int)(((duration%(1000*60*60))%(1000*60))/(1000));
        String finalString="";
        if(hour<10)
            finalString+="0";
        finalString+=hour+":";
        if(minute<10)
            finalString+="0";
        finalString+=minute+":";
        if(seconds<10)
            finalString+="0";
        finalString+=seconds;

        return finalString;
    }
```

**Adding SeekBar to Show Song ProgressBar**
```
if(mediaPlayer==null)
{
	...
	songProgressBar.setProgress(0);
        songProgressBar.setMax(100);
	...
}

 private Runnable mUpdateTimerTask=new Runnable() {
        @Override
        public void run() {

	...
            songProgressBar.setProgress(getProgressPercentage(totalDuration,currentDuration));

	...
        }
    };


    private int getProgressPercentage(int totalDuration, int currentDuration) {
        return (int)(currentDuration*100)/totalDuration;
    }
```
**Handle Seekbar event**
```
public class MediaPlayerDemo extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
	...
}

 @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	      if(fromUser) {
            mMediaPlayer.seekTo(getTimeFromProgress(seekBar.getProgress(), mMediaPlayer.getDuration()));
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private int getTimeFromProgress(int progress, int duration) {
        int songDuration=(int)((duration*progress)/100);
        return songDuration;
    }
```
![screenshot_audioplayer](https://cloud.githubusercontent.com/assets/20207324/16520855/97fc3d2e-3fb0-11e6-8f84-29b5ecb7571e.png)
