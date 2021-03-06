package ai.victorl.gpmdpcontroller.data.media.actions;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import ai.victorl.gpmdpcontroller.R;

public class RepeatAction {
    private static final String GPMDP_ACTION_REPEAT = "ai.victorl.gpmdpcontroller.ACTION_REPEAT";

    private PlaybackStateCompat.CustomAction action;

    public RepeatAction(Context context) {
        String id = GPMDP_ACTION_REPEAT;
        String name = context.getResources().getString(R.string.playbackstate_customaction_repeat);
        int icon = R.drawable.ic_repeat_white_24dp;
        action = new PlaybackStateCompat.CustomAction.Builder(id, name, icon).build();
    }

    public PlaybackStateCompat.CustomAction getAction() {
        return action;
    }

    public static String getName() {
        return GPMDP_ACTION_REPEAT;
    }
}
