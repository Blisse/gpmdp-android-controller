package ai.victorl.gpmdpcontroller.data.gpmdp.api.requests;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import ai.victorl.gpmdpcontroller.data.gpmdp.GpmdpState;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequest;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponse;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.GpmdpRequestResponseCallback;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Playlist;
import ai.victorl.gpmdpcontroller.data.gpmdp.api.responses.Track;

public class PlaylistsRequest extends GpmdpRequest {
    protected PlaylistsRequest(String method, GpmdpRequestResponseCallback callback) {
        super("playlists", method, callback);
    }

    protected PlaylistsRequest(String method) {
        this(method, null);
    }

    public static class Factory {
        public static PlaylistsRequest getAllPlaylistsRequest() {
            return new PlaylistsRequest("getAll", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {
                    state.playlists.playlists = (List<Playlist>) requestResponse.value;
                    eventBus.post(state.playlists);
                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            });
        }

        public static GpmdpRequest playRequest(Playlist playlist) {
            return new PlaylistsRequest("play", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            }).withArgument(playlist);
        }

        public static GpmdpRequest playWithTrackRequest(Playlist playlist, Track track) {
            return new PlaylistsRequest("playWithTrack", new GpmdpRequestResponseCallback() {
                @Override
                public void onSuccess(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }

                @Override
                public void onError(GpmdpRequestResponse requestResponse, GpmdpState state, EventBus eventBus) {

                }
            }).withArgument(playlist).withArgument(track);
        }
    }
}
