package ai.victorl.gpmdpcontroller.data.media.events;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

public class QueueEvent {
    public String queueName;
    public List<MediaSessionCompat.QueueItem> queueItems;

    public QueueEvent(String queueName, List<MediaSessionCompat.QueueItem> queueItems) {
        this.queueName = queueName;
        this.queueItems = queueItems;
    }
}
