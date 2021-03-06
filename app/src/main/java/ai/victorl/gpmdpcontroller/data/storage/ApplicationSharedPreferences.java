package ai.victorl.gpmdpcontroller.data.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationSharedPreferences implements LocalSettings {
    private static final String SHARED_PREFERENCES_NAME = "ai.victorl.gpmdpcontroller.prefs";

    private final SharedPreferences preferences;

    public ApplicationSharedPreferences(Context context) {
        preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }
}
