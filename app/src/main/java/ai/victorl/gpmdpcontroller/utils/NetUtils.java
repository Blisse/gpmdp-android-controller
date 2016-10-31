package ai.victorl.gpmdpcontroller.utils;

import android.bluetooth.BluetoothAdapter;
import android.util.Patterns;

public class NetUtils {

    // Credit: https://euniceadu.wordpress.com/2013/08/15/how-to-validate-an-ip-address-android/
    public static Boolean isValidIpAddress(String address) {
        return Patterns.IP_ADDRESS.matcher(address).matches();
    }

    public static String getDeviceName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }
}
