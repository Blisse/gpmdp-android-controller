package ai.victorl.gpmdpcontroller.ui.views;

import android.app.Activity;
import android.view.ViewGroup;

/** An indirection which allows controlling the root container used for each activity. */
public interface ViewContainer {
    /** The root {@link android.view.ViewGroup} into which the activity should place its contents. */
    ViewGroup forActivity(Activity activity);
}
