package audio.sodhankit.com.mediaplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private List<Song> songs = new ArrayList<>();
    RecyclerView songRecyclerView;
    TextView songCurrentPosition, songTotalDuration;
    SeekBar songProgress;
    ImageButton btnPlay, btnNext, btnPrevious;
    MediaPlayer mMediaPlayer;
    static int songPosition=0;
    MySongAdapter songAdapter;
    Handler mHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songTotalDuration=(TextView)findViewById(R.id.songTotalDuration);
        songCurrentPosition=(TextView)findViewById(R.id.songCurrentPosition);
        songProgress=(SeekBar)findViewById(R.id.songProgress);
        btnPlay=(ImageButton)findViewById(R.id.btnPlay);
        btnNext=(ImageButton)findViewById(R.id.btnNext);
        btnPrevious=(ImageButton)findViewById(R.id.btnPrevious);

        songProgress.setOnSeekBarChangeListener(this);

        songRecyclerView=(RecyclerView) findViewById(R.id.songRecyclerView);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSongs();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playMySong(songs.get(songPosition));
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataChanged(songPosition,false);
                songPosition++;
                if(songPosition>songs.size())
                {
                    songPosition--;
                }

                if(mMediaPlayer!=null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                playMySong(songs.get((songPosition)));
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataChanged(songPosition,false);
                songPosition--;
                if(songPosition<0)
                {
                    songPosition=0;
                }

                if(mMediaPlayer!=null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                playMySong(songs.get((songPosition)));
            }
        });
    }

    private void notifyDataChanged(int position, boolean playing) {
        Song song=songs.get(position);
        song.setPlaying(playing);
        songs.set(position,song);
        songAdapter.notifyDataSetChanged();
    }

    private void loadSongs() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else*/ {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else
        {
            fetchAllSongs();
        }
    }

    private void fetchAllSongs() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);


        while(cursor.moveToNext()) {
            songs.add(cursorToSong(cursor));
        }

        songAdapter=new MySongAdapter(MainActivity.this,songs);
        songRecyclerView.setAdapter(songAdapter);
    }

    private Song cursorToSong(Cursor cursor)
    {
        Song song=new Song();
        song.setId(cursor.getString(0));
        song.setArtist(cursor.getString(1));
        song.setTitle(cursor.getString(2));
        song.setData(cursor.getString(3));
        song.setDisplayName(cursor.getString(4));
        song.setDuration(cursor.getString(5));
        song.setPlaying(false);
        return song;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchAllSongs();
                    // permission was granted, yay! Do the
                    // songs-related task you need to do.

                } else {
                    Toast.makeText(MainActivity.this, "Sorry We Can't Show Songs!!!", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
                
        }
    }

    public void playRandomSong(int position)
    {
        notifyDataChanged(songPosition,false);
        songPosition=position;
        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        playMySong(songs.get(songPosition));
    }

    private void playMySong(Song song) {
        /*if(currentSongId!=null && song.getId()!=currentSongId)
        {
            mMediaPlayer.release();
            mMediaPlayer=null;
        }*/
        notifyDataChanged(songPosition,true);
        if (mMediaPlayer == null)
        {
           // currentSongId=song.getId();
            mMediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(song.getData()));
            mMediaPlayer.start();
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            songTotalDuration.setText(milliSecondsToTimer(song.getDuration()));
            updateSongProgress();

        }else
        {
            if(mMediaPlayer.isPlaying())
            {
                notifyDataChanged(songPosition,false);
                mMediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            }else
            {
                notifyDataChanged(songPosition,true);
                mMediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    }

    private void updateSongProgress()
    {
        mHandler.postDelayed(runnable,1000);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            int currentDuration=mMediaPlayer.getCurrentPosition();
            int totalDuration=mMediaPlayer.getDuration();

            songCurrentPosition.setText(milliSecondsToTimer(String.valueOf(currentDuration)));

            songProgress.setProgress(getSongProgress(totalDuration,currentDuration));

            mHandler.postDelayed(this,1000);
        }
    };

    private int getSongProgress(int totalDuration, int currentDuration) {
        return (int)(currentDuration*100)/totalDuration;
    }

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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(b) {
            mMediaPlayer.seekTo(getTimeFromProgress(seekBar.getProgress(), mMediaPlayer.getDuration()));
        }
    }

    private int getTimeFromProgress(int progress, int duration) {
        int songDuration=(int)((duration*progress)/100);
        return songDuration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
