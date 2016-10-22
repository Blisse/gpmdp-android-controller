package ai.victorl.gpmdpcontroller.data.media;

import android.os.Bundle;

import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Repeat;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Shuffle;

public final class GpmdpMediaExtras {
    private static final String KEY_STRING_REPEAT = "ai.victorl.gpmdpcontroller.KEY_REPEAT";
    private static final String KEY_STRING_SHUFFLE = "ai.victorl.gpmdpcontroller.KEY_SHUFFLE";

    private Bundle bundle;

    private GpmdpMediaExtras(Bundle bundle) {
        this.bundle = bundle;
    }

    public static GpmdpMediaExtras fromBundle(Bundle bundle) {
        return new GpmdpMediaExtras(bundle);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Repeat getRepeat() {
        if (bundle.containsKey(KEY_STRING_REPEAT)) {
            return Repeat.valueOf(bundle.getString(KEY_STRING_REPEAT));
        }
        return Repeat.NO_REPEAT;
    }

    public Shuffle getShuffle() {
        if (bundle.containsKey(KEY_STRING_SHUFFLE)) {
            return Shuffle.valueOf(bundle.getString(KEY_STRING_SHUFFLE));
        }
        return Shuffle.NO_SHUFFLE;
    }

    public static class Builder {
        private Bundle bundle;

        public Builder() {
            bundle = new Bundle();
        }

        public Builder withRepeat(Repeat repeat) {
            bundle.putString(KEY_STRING_REPEAT, repeat.name());
            return this;
        }

        public Builder withShuffle(Shuffle shuffle) {
            bundle.putString(KEY_STRING_SHUFFLE, shuffle.name());
            return this;
        }

        public GpmdpMediaExtras build() {
            return new GpmdpMediaExtras(bundle);
        }
    }

}
