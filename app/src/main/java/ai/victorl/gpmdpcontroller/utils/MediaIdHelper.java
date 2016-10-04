package ai.victorl.gpmdpcontroller.utils;


import android.support.annotation.NonNull;

import java.util.Arrays;

/**
 * Utility class to help on queue related tasks.
 */
public class MediaIdHelper {

    // Media IDs used on browseable items of MediaBrowser
    public static final String MEDIA_ID_ROOT = "__ROOT__";

    private static final char CATEGORY_SEPARATOR = '/';
    private static final char LEAF_SEPARATOR = '|';

    /**
     * Create a String value that represents a playable or a browsable media.
     *
     * Encode the media browseable categories, if any, and the unique music ID, if any,
     * into a single String mediaID.
     *
     * MediaIDs are of the form <categoryType>/<categoryValue>|<musicUniqueId>, to make it easy
     * to find the category (like genre) that a music was selected from, so we
     * can correctly build the playing queue. This is specially useful when
     * one music can appear in more than one list, like "by genre -> genre_1"
     * and "by artist -> artist_1".

     * @param musicID Unique music ID for playable items, or null for browseable items.
     * @param categories hierarchy of categories representing this item's browsing parents
     * @return a hierarchy-aware media ID
     */
    public static String createMediaID(String musicID, String... categories) {
        StringBuilder sb = new StringBuilder();
        if (categories != null) {
            for (int i=0; i < categories.length; i++) {
                if (!isValidCategory(categories[i])) {
                    throw new IllegalArgumentException("Invalid category: " + categories[0]);
                }
                sb.append(categories[i]);
                if (i < categories.length - 1) {
                    sb.append(CATEGORY_SEPARATOR);
                }
            }
        }
        if (musicID != null) {
            sb.append(LEAF_SEPARATOR).append(musicID);
        }
        return sb.toString();
    }

    private static boolean isValidCategory(String category) {
        return category == null ||
                (
                        category.indexOf(CATEGORY_SEPARATOR) < 0 &&
                                category.indexOf(LEAF_SEPARATOR) < 0
                );
    }

    /**
     * Extracts unique musicID from the mediaID. mediaID is, by this sample's convention, a
     * concatenation of category (eg "by_genre"), categoryValue (eg "Classical") and unique
     * musicID. This is necessary so we know where the user selected the music from, when the music
     * exists in more than one music list, and thus we are able to correctly build the playing queue.
     *
     * @param mediaID that contains the musicID
     * @return musicID
     */
    public static String extractMusicIDFromMediaID(@NonNull String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            return mediaID.substring(pos+1);
        }
        return null;
    }

    /**
     * Extracts category and categoryValue from the mediaId. mediaId is, by this sample's
     * convention, a concatenation of category (eg "by_genre"), categoryValue (eg "Classical") and
     * mediaId. This is necessary so we know where the user selected the music from, when the music
     * exists in more than one music list, and thus we are able to correctly build the playing queue.
     *
     * @param mediaId that contains a category and categoryValue.
     */
    public static @NonNull String[] getHierarchy(@NonNull String mediaId) {
        int pos = mediaId.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            mediaId = mediaId.substring(0, pos);
        }
        return mediaId.split(String.valueOf(CATEGORY_SEPARATOR));
    }

    public static String extractBrowseCategoryValueFromMediaID(@NonNull String mediaID) {
        String[] hierarchy = getHierarchy(mediaID);
        if (hierarchy.length == 2) {
            return hierarchy[1];
        }
        return null;
    }

    public static boolean isBrowseable(@NonNull String mediaID) {
        return mediaID.indexOf(LEAF_SEPARATOR) < 0;
    }

    public static String getParentMediaID(@NonNull String mediaID) {
        String[] hierarchy = getHierarchy(mediaID);
        if (!isBrowseable(mediaID)) {
            return createMediaID(null, hierarchy);
        }
        if (hierarchy.length <= 1) {
            return MEDIA_ID_ROOT;
        }
        String[] parentHierarchy = Arrays.copyOf(hierarchy, hierarchy.length-1);
        return createMediaID(null, parentHierarchy);
    }
}
