package ai.victorl.gpmdpcontroller.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class GpmdpSharedPreferences implements LocalSettings {
    private static final String SHARED_PREFERENCES_NAME = "gpmdpcontroller_prefs";

    private final SharedPreferences preferences;

    public GpmdpSharedPreferences(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }
}
