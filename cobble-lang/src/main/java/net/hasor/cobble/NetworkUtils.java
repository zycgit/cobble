/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2014年11月17日
 */
public class NetworkUtils {
    /**
     * Converts a Unicode or ASCII DNS name to lowercase ASCII, encoding
     * internationalized labels as Punycode A-labels.
     *
     * <p>Uses bundled Unicode 17.0.0 data for UTS #46 nontransitional processing
     * with STD3, Bidi, ContextJ and ContextO checks. Results do not depend on the
     * JDK's Unicode version or an optional ICU installation. This operation is
     * thread-safe and does not perform DNS lookups.</p>
     *
     * <p>Validates the complete DNS name and its encoded length. A single trailing
     * root dot is preserved. Empty names, empty interior labels, wildcards and URL
     * syntax are rejected; inputs are interpreted as DNS names, not IP addresses
     * or URLs.</p>
     *
     * @param name a complete DNS name, optionally ending in a root dot
     * @return the validated ASCII DNS name, preserving an optional root dot
     * @throws IllegalArgumentException if the name is null, empty or invalid
     * @see #toDNSUnicode(String)
     */
    public static String toDNSAscii(String name) {
        return IdnCodec.convert(name, true);
    }

    /**
     * Decodes DNS Punycode A-labels and returns the normalized Unicode DNS name.
     * Unicode input is also mapped and normalized; the original spelling or
     * casing is not necessarily preserved.
     *
     * <p>Uses the same Unicode data, validation and encoded DNS length limits as
     * {@link #toDNSAscii(String)}. A single trailing root dot is preserved.
     * Invalid A-labels cause an exception rather than being returned unchanged.
     * This operation is thread-safe and does not perform DNS lookups.</p>
     *
     * <p>Validation establishes DNS syntax, not whether a name is safe to display
     * as a trusted identity; visually confusable names can still be distinct.</p>
     *
     * @param name a complete ASCII or Unicode DNS name
     * @return the validated Unicode DNS name, preserving an optional root dot
     * @throws IllegalArgumentException if the name is null, empty or invalid
     */
    public static String toDNSUnicode(String name) {
        return IdnCodec.convert(name, false);
    }

    private static void isPortAvailable(String host, int port) throws Exception {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /** 测试端口是否被占用 */
    public static boolean isPortAvailable(int port) {
        try {
            isPortAvailable("0.0.0.0", port);
            isPortAvailable(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 根据IP字节数据转换为int */
    public static byte[] ipStrToBytes(String ipData) {
        byte[] ipParts = new byte[4];
        String[] splitArrays = ipData.split("\\.");
        for (int i = 0; i < splitArrays.length; i++) {
            ipParts[i] = (byte) Integer.parseInt(splitArrays[i]);
        }
        return ipParts;
    }

    /** 根据名字获取地址，local代表本机（如果本机有多网卡那么请明确指定ip） */
    public static InetAddress finalBindAddress(String hostString) throws UnknownHostException, SocketException {
        if ("local".equalsIgnoreCase(hostString)) {
            List<String> localAddr = localAddrForIPv4();
            if (localAddr.isEmpty()) {
                return InetAddress.getLocalHost();
            } else {
                return InetAddress.getByName(localAddr.get(0));
            }
        } else {
            return InetAddress.getByName(hostString);
        }
    }

    /** 根据掩码长度获取掩码字符串形式 */
    public static String maskToStringByPrefixLength(int length) {
        return ipDataToString(ipDataByInt(maskByPrefixLength(length)));
    }

    /** 根据掩码长度获取子网掩码值 */
    public static int maskByPrefixLength(int length) {
        if (length > 32) {
            throw new IndexOutOfBoundsException("mask length max is 32.");
        }
        return -1 << (32 - length);
    }

    /** 根据IP值分解IP为字节数组 */
    public static byte[] ipDataByInt(int ipData) {
        byte[] ipParts = new byte[4];
        for (int i = 0; i < ipParts.length; i++) {
            int pos = ipParts.length - 1 - i;
            ipParts[pos] = (byte) (ipData >> (i * 8));
        }
        return ipParts;
    }

    /** 根据IP字节数据转换为int */
    public static int ipDataByBytes(byte[] ipData) {
        int[] ipParts = new int[4];
        ipParts[0] = 0xFFFFFFFF & (ipData[0] << 24);
        ipParts[1] = 0x00FFFFFF & (ipData[1] << 16);
        ipParts[2] = 0x0000FFFF & (ipData[2] << 8);
        ipParts[3] = 0x000000FF & (ipData[3] << 0);
        int intIP = 0;
        for (int ipPart : ipParts) {
            intIP = intIP | ipPart;
        }
        return intIP;
    }

    /** 将分解的IP数据转换为字符串 */
    public static String ipDataToString(int ipData) {
        return ipDataToString(ipDataByInt(ipData));
    }

    /** 将分解的IP数据转换为字符串 */
    public static String ipDataToString(byte[] ipData) {
        StringBuilder result = new StringBuilder();
        result.append(toStr(ipData[0]));
        for (int i = 1; i < ipData.length; i++) {
            result.append(".").append(toStr(ipData[i]));
        }
        return result.toString();
    }

    private static String toStr(byte byteData) {
        return "" + ((byteData < 0) ? 256 + byteData : byteData);
    }

    /** 获取本机地址 */
    public static List<String> localAddrForIPv4() throws SocketException {
        List<String> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            Enumeration<InetAddress> ipAddrEnum = ni.getInetAddresses();
            while (ipAddrEnum.hasMoreElements()) {
                InetAddress addr = ipAddrEnum.nextElement();
                if (addr.isLoopbackAddress()) {
                    continue;
                }
                String ip = addr.getHostAddress();
                if (ip.contains(":")) {
                    //skip the IPv6 addr
                    continue;
                }
                if (!ipList.contains(ip)) {
                    ipList.add(ip);
                }
            }
        }
        return ipList;
    }
}
