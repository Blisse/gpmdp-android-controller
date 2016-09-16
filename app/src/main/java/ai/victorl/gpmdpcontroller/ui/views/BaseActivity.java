package ai.victorl.gpmdpcontroller.ui.views;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

import ai.victorl.gpmdpcontroller.GpmdpControllerApplication;
import ai.victorl.gpmdpcontroller.injection.components.ActivityComponent;
import ai.victorl.gpmdpcontroller.injection.components.ApplicationComponent;
import ai.victorl.gpmdpcontroller.injection.components.DaggerActivityComponent;
import ai.victorl.gpmdpcontroller.injection.modules.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String ACT_UNIQUE_KEY = BaseActivity.class.getName() + ".unique.key";

    private ActivityComponent activityComponent;
    private String uniqueKey;

    public ApplicationComponent getApplicationComponent() {
        return GpmdpControllerApplication.get(this).getApplicationComponent();
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(getApplicationComponent())
                    .build();
        }
        return activityComponent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            onExtractParams(params);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(ACT_UNIQUE_KEY)) {
            uniqueKey = savedInstanceState.getString(ACT_UNIQUE_KEY);
        } else {
            uniqueKey = UUID.randomUUID().toString();
        }

        super.onCreate(savedInstanceState);

        setContentView(layoutId());
        onPostInflate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(ACT_UNIQUE_KEY, uniqueKey);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        uniqueKey = savedInstanceState.getString(ACT_UNIQUE_KEY);
    }

    protected void onExtractParams(@NonNull Bundle params) {

    }

    protected void onPostInflate() {

    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    protected abstract @LayoutRes int layoutId();
}
