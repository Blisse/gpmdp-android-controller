package ai.victorl.gpmdpcontroller.data.media.actions;

import android.content.Context;
import android.support.v4.media.session.PlaybackStateCompat;

import ai.victorl.gpmdpcontroller.R;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Repeat;

public class RepeatAction {
    private static final String GPMDP_ACTION_REPEAT = "ai.victorl.gpmdpcontroller.ACTION_REPEAT";

    private PlaybackStateCompat.CustomAction action;

    private int getRepeatDrawable(Repeat repeat) {
        if (repeat == Repeat.SINGLE_REPEAT) {
            return R.drawable.ic_repeat_white_24dp;
        }
        return R.drawable.ic_repeat_one_white_24dp;
    }

    public RepeatAction(Context context, Repeat repeat) {
        String id = GPMDP_ACTION_REPEAT;
        String name = context.getResources().getString(R.string.playbackstate_customaction_repeat);
        int icon = getRepeatDrawable(repeat);
        action = new PlaybackStateCompat.CustomAction.Builder(id, name, icon).build();
    }

    public PlaybackStateCompat.CustomAction getAction() {
        return action;
    }

    public static String getName() {
        return GPMDP_ACTION_REPEAT;
    }
}
