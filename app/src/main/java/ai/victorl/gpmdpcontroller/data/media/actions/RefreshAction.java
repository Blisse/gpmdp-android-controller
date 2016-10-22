package ai.victorl.gpmdpcontroller.data.media.actions;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import ai.victorl.gpmdpcontroller.R;

public class RefreshAction {
    private static final String GPMDP_ACTION_REFRESH = "ai.victorl.gpmdpcontroller.ACTION_REFRESH";

    private PlaybackStateCompat.CustomAction action;

    public RefreshAction(Context context) {
        String id = GPMDP_ACTION_REFRESH;
        String name = context.getResources().getString(R.string.playbackstate_customaction_refresh);
        int icon = R.drawable.ic_fast_forward_white_24dp;
        action = new PlaybackStateCompat.CustomAction.Builder(id, name, icon).build();
    }

    public PlaybackStateCompat.CustomAction getAction() {
        return action;
    }

    public static String getName() {
        return GPMDP_ACTION_REFRESH;
    }
}
