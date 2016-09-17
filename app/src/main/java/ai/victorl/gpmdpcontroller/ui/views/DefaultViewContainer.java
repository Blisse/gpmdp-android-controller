package ai.victorl.gpmdpcontroller.ui.views;

import android.app.Activity;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class DefaultViewContainer implements ViewContainer {
    @Override
    public ViewGroup forActivity(Activity activity) {
        return ButterKnife.findById(activity, android.R.id.content);
    }
}
