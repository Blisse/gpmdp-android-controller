package ai.victorl.gpmdpcontroller.data.media.actions;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import ai.victorl.gpmdpcontroller.R;

public class ShuffleAction {
    private static final String GPMDP_ACTION_SHUFFLE = "ai.victorl.gpmdpcontroller.ACTION_SHUFFLE";

    private PlaybackStateCompat.CustomAction action;

    public ShuffleAction(Context context) {
        String id = GPMDP_ACTION_SHUFFLE;
        String name = context.getResources().getString(R.string.playbackstate_customaction_shuffle);
        int icon = R.drawable.ic_shuffle_white_24dp;
        action = new PlaybackStateCompat.CustomAction.Builder(id, name, icon).build();
    }

    public PlaybackStateCompat.CustomAction getAction() {
        return action;
    }

    public static String getName() {
        return GPMDP_ACTION_SHUFFLE;
    }
}
