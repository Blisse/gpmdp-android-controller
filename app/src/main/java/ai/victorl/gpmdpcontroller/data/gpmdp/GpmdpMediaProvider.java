package ai.victorl.gpmdpcontroller.data.gpmdp;

import android.content.res.Resources;
import android.support.v4.media.MediaBrowserCompat;

import java.util.List;

public interface GpmdpMediaProvider {
    String MEDIA_ID_ROOT = "ai.victorl.gpmdp_controller.__root__";
    String MEDIA_ID_QUEUE = "ai.victorl.gpmdp_controller.__queue__";

    List<MediaBrowserCompat.MediaItem> getChildren(String mediaId, Resources resources);
}
