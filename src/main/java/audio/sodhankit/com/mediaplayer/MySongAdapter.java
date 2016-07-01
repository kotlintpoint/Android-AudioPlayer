package audio.sodhankit.com.mediaplayer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Admin on 6/30/2016.
 */
public class MySongAdapter extends RecyclerView.Adapter<MySongAdapter.MyViewHolder> {

    List<Song> songList;

    MainActivity mainActivity;


    public MySongAdapter(MainActivity mainActivity,List<Song> songList) {
        this.songList=songList;
        this.mainActivity=mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_view,
                parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Song song=songList.get(position);
        holder.songTitle.setText(song.getTitle());
        if(song.isPlaying())
        {
            holder.btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            holder.songTitle.setTextColor(Color.BLUE);
            holder.songTitle.setTypeface(null, Typeface.BOLD);

        }else
        {
            holder.btnPlay.setImageResource(android.R.drawable.ic_media_play);
            holder.songTitle.setTextColor(Color.BLACK);
            holder.songTitle.setTypeface(null, Typeface.NORMAL);
        }
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!song.isPlaying())
                {
                    mainActivity.playRandomSong(position);
                }
            }
        });
    }

   /*
*/
    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView songTitle;
        ImageButton btnPlay;

        public MyViewHolder(View itemView) {
            super(itemView);

            songTitle=(TextView)itemView.findViewById(R.id.songTitle);
            btnPlay=(ImageButton) itemView.findViewById(R.id.btnPlay);

        }
    }
}
