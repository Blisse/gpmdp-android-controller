package ai.victorl.gpmdpcontroller.ui.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

import ai.victorl.gpmdpcontroller.GpmdpControllerApplication;
import ai.victorl.gpmdpcontroller.injection.Injector;
import ai.victorl.gpmdpcontroller.injection.components.ActivityComponent;
import ai.victorl.gpmdpcontroller.injection.components.ApplicationComponent;
import ai.victorl.gpmdpcontroller.injection.components.DaggerActivityComponent;
import ai.victorl.gpmdpcontroller.injection.modules.ActivityModule;

public abstract class BaseActivity extends AppCompatActivity {
    private final String ACT_UNIQUE_KEY = getClassName() + ".unique.key";

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(ACT_UNIQUE_KEY)) {
            uniqueKey = savedInstanceState.getString(ACT_UNIQUE_KEY);
        } else {
            uniqueKey = UUID.randomUUID().toString();
        }
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

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesActivityComponentService(name)) {
            return getActivityComponent();
        }
        return super.getSystemService(name);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        onPreInflate();
        super.setContentView(layoutResID);
        onPostInflate();
    }

    protected String getUniqueKey() {
        return uniqueKey;
    }

    abstract void onPreInflate();

    abstract void onPostInflate();

    abstract String getClassName();
}
