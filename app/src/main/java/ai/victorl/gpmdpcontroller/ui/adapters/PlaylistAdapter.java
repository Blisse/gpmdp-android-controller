package ai.victorl.gpmdpcontroller.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;
import ai.victorl.gpmdpcontroller.injection.Injector;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    @Inject Picasso picasso;

    private final List<Track> tracks = new ArrayList<>();
    private int activePosition = -1;

    public PlaylistAdapter(Context context) {
        Injector.activityComponent(context).inject(this);
    }

    public void setTracks(List<Track> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        this.notifyDataSetChanged();
    }

    public void setCurrentTrack(Track currentTrack) {
        if (currentTrack != null) {
            notifyItemChanged(activePosition);

            activePosition = -1;
            for (Track track : tracks) {
                if (currentTrack.equals(track)) {
                    activePosition = tracks.indexOf(track);
                    break;
                }
            }
            notifyItemChanged(activePosition);
        }
    }

    public int getCurrentTrackIndex() {
        return activePosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.track_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Track track = tracks.get(position);
        picasso.load(track.albumArt)
                .fit()
                .into(holder.coverImageView);
        holder.titleTextView.setText(track.title);
        holder.artistTextView.setText(track.artist);
        holder.durationTextView.setText(DateUtils.formatElapsedTime(track.duration / 1000));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        holder.setActive(position == activePosition);
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.root) public View rootView;
        @BindView(R.id.cover) public ImageView coverImageView;
        @BindView(R.id.title) public TextView titleTextView;
        @BindView(R.id.artist) public TextView artistTextView;
        @BindView(R.id.duration) public TextView durationTextView;
        @BindColor(R.color.pacifica) int activeColor;
        @BindColor(R.color.slate) int inactiveColor;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void setActive(boolean active) {
            if (active) {
                rootView.setBackgroundColor(activeColor);
            } else {
                rootView.setBackgroundColor(inactiveColor);
            }
        }
    }
}
