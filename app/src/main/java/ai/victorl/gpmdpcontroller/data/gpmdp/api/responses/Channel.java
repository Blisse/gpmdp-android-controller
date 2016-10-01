package ai.victorl.gpmdpcontroller.data.gpmdp.api.responses;

import com.google.gson.annotations.SerializedName;

public enum Channel {
    @SerializedName("API_VERSION")
    API_VERSION("API_VERSION"),

    @SerializedName("connect")
    CONNECT("connect"),

    @SerializedName("playState")
    PLAY_STATE("playState"),

    @SerializedName("track")
    TRACK("track"),

    @SerializedName("lyrics")
    LYRICS("lyrics"),

    @SerializedName("time")
    TIME("time"),

    @SerializedName("rating")
    RATING("rating"),

    @SerializedName("shuffle")
    SHUFFLE("shuffle"),

    @SerializedName("repeat")
    REPEAT("repeat"),

    @SerializedName("playlists")
    PLAYLISTS("playlists"),

    @SerializedName("queue")
    QUEUE("queue"),

    @SerializedName("search-results")
    SEARCH_RESULTS("search-results"),

    @SerializedName("settings:theme")
    SETTINGS_THEME("settings:theme"),

    @SerializedName("settings:themeType")
    SETTINGS_THEMETYPE("settings:themeType"),

    @SerializedName("settings:themeColor")
    SETTINGS_THEMECOLOR("settings:themeColor"),

    @SerializedName("library")
    LIBRARY("library");

    public String value;
    Channel(String channel) {
        this.value = channel;
    }

    public static Channel fromValue(String value) {
        for (Channel channel: Channel.values()) {
            if (channel.value.equals(value)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("No Channel with value " + value + " found.");
    }
}
