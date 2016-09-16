package ai.victorl.gpmdpcontroller.utils;

import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class NetUtils {
    public static Boolean isValidIpAddress(String address) {
        if (!TextUtils.isEmpty(address)) {
            try {
                Object netAddressObject = InetAddress.getByName(address);
                return (netAddressObject instanceof Inet4Address)
                        || (netAddressObject instanceof Inet6Address);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
