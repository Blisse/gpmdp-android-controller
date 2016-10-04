package ai.victorl.gpmdpcontroller.ui.views;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import ai.victorl.gpmdpcontroller.R;

public final class Intents {
    /**
     * Attempt to launch the supplied {@link Intent}. Queries on-device packages before launching and
     * will display a simple message if none are available to handle it.
     */
    public static boolean maybeStartActivity(Context context, Intent intent) {
        return maybeStartActivity(context, intent, null, false);
    }

    public static boolean maybeStartActivity(Context context, Intent intent, Bundle bundle) {
        return maybeStartActivity(context, intent, bundle, false);
    }

    /**
     * Attempt to launch Android's chooser for the supplied {@link Intent}. Queries on-device
     * packages before launching and will display a simple message if none are available to handle
     * it.
     */
    public static boolean maybeStartChooser(Context context, Intent intent) {
        return maybeStartActivity(context, intent, null, true);
    }

    public static boolean maybeStartChooser(Context context, Intent intent, Bundle bundle) {
        return maybeStartActivity(context, intent, bundle, true);
    }

    private static boolean maybeStartActivity(Context context, Intent intent, Bundle bundle, boolean chooser) {
        if (hasHandler(context, intent)) {
            if (chooser) {
                intent = Intent.createChooser(intent, null);
            }

            context.startActivity(intent, bundle);
            return true;
        } else {
            Toast.makeText(context, R.string.no_intent_handler, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Queries on-device packages for a handler for the supplied {@link Intent}.
     */
    private static boolean hasHandler(Context context, Intent intent) {
        List<ResolveInfo> handlers = context.getPackageManager().queryIntentActivities(intent, 0);
        return !handlers.isEmpty();
    }

    private Intents() {
    }
}
