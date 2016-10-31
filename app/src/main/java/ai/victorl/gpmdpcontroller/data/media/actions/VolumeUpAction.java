package ai.victorl.gpmdpcontroller.data.media.actions;


import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import ai.victorl.gpmdpcontroller.R;

public class VolumeUpAction {
    private static final String GPMDP_ACTION_VOLUMEUP = "ai.victorl.gpmdpcontroller.ACTION_VOLUMEUP";

    private PlaybackStateCompat.CustomAction action;

    public VolumeUpAction(Context context) {
        String id = GPMDP_ACTION_VOLUMEUP;
        String name = context.getResources().getString(R.string.playbackstate_customaction_volumeup);
        int icon = R.drawable.ic_skip_next_white_24dp;
        action = new PlaybackStateCompat.CustomAction.Builder(id, name, icon).build();
    }

    public PlaybackStateCompat.CustomAction getAction() {
        return action;
    }

    public static String getName() {
        return GPMDP_ACTION_VOLUMEUP;
    }
}
