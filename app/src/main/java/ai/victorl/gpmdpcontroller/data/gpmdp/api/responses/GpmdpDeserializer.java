package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class GpmdpDeserializer implements JsonDeserializer<GpmdpResponse> {
    private static final Map<Channel, Class> channelResponseMap = new HashMap<>();

    static {
        channelResponseMap.put(Channel.API_VERSION, ApiVersionResponse.class);
        channelResponseMap.put(Channel.CONNECT, ConnectResponse.class);
        channelResponseMap.put(Channel.LYRICS, LyricsResponse.class);
        channelResponseMap.put(Channel.PLAY_STATE, PlayStateResponse.class);
        channelResponseMap.put(Channel.PLAYLISTS, PlaylistsResponse.class);
        channelResponseMap.put(Channel.QUEUE, QueueResponse.class);
        channelResponseMap.put(Channel.RATING, RatingResponse.class);
        channelResponseMap.put(Channel.REPEAT, RepeatResponse.class);
        channelResponseMap.put(Channel.SEARCH_RESULTS, SearchResultsResponse.class);
        channelResponseMap.put(Channel.SHUFFLE, ShuffleResponse.class);
        channelResponseMap.put(Channel.TIME, TimeResponse.class);
        channelResponseMap.put(Channel.TRACK, TrackResponse.class);
    }

    @Override
    public GpmdpResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonElement jsonChannel = jsonObject.get("channel");
        String channelString = jsonChannel.getAsString();
        Channel channel = Channel.fromValue(channelString);
        Class responseClass = channelResponseMap.get(channel);
        return context.deserialize(json, responseClass);
    }
}
