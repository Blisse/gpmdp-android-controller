package ai.victorl.gpmdpcontroller.utils;

import android.bluetooth.BluetoothAdapter;

public class StringUtils {
    public static String DOT = ".";
    public static String COLON = ".";

    public static String getDeviceName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }
}
