package ai.victorl.gpmdpcontroller.ui.adapters;

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.injection.Injector;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    @Inject Picasso picasso;

    private final EventBus playlistEventBus = EventBus.builder()
            .logNoSubscriberMessages(true)
            .logSubscriberExceptions(true)
            .build();
    private final List<MediaSessionCompat.QueueItem> tracks = new ArrayList<>();
    private int activePosition = -1;

    public PlaylistAdapter(Context context) {
        Injector.activityComponent(context).inject(this);
    }

    public EventBus getEventBus() {
        return playlistEventBus;
    }

    public void setTracks(List<MediaSessionCompat.QueueItem> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        notifyDataSetChanged();
    }

    public void setActive(int activePosition) {
        if (this.activePosition != activePosition) {
            notifyItemChanged(this.activePosition);
            this.activePosition = activePosition;
            notifyItemChanged(this.activePosition);
        }
    }

    public void clearActive() {
        int deactivatedPosition = activePosition;
        activePosition = -1;
        notifyItemChanged(deactivatedPosition);
    }

    public int getActivePosition() {
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
        final MediaSessionCompat.QueueItem queueItem = tracks.get(position);
        MediaDescriptionCompat description = queueItem.getDescription();
        picasso.load(description.getIconUri())
                .fit()
                .into(holder.coverImageView);
        holder.titleTextView.setText(description.getTitle());
        holder.artistTextView.setText(description.getSubtitle());
        holder.durationTextView.setText(DateUtils.formatElapsedTime(0));

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEventBus().post(new PlaylistOnClickEvent(tracks.get(holder.getAdapterPosition()).getQueueId()));
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

    public static class PlaylistOnClickEvent {
        public Long selectedTrackId;

        public PlaylistOnClickEvent(Long trackId) {
            selectedTrackId = trackId;
        }
    }
}
