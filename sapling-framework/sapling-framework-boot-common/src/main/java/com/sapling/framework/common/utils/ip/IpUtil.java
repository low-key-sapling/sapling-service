package com.sapling.framework.common.utils.ip;

import cn.hutool.core.convert.Convert;

import java.math.BigInteger;

public class IpUtil {
    public static long ipToLong(String ipStr) {
        if (ipStr.contains(":")) {
            return ipv6ToLong(ipStr);
        }
        return ipv4ToLong(ipStr);
    }

    public static long ipv4ToLong(String ipStr) {
        long[] ip = new long[4];
        int position1 = ipStr.indexOf(".");
        int position2 = ipStr.indexOf(".", position1 + 1);
        int position3 = ipStr.indexOf(".", position2 + 1);

        ip[0] = Long.parseLong(ipStr.substring(0, position1));
        ip[1] = Long.parseLong(ipStr.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(ipStr.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(ipStr.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static long ipv6ToLong(String ipv6Str) {
        byte[] ret = new byte[17];
        ret[0] = 0;
        int ib = 16;
        if (ipv6Str.startsWith(":")) {
            ipv6Str = ipv6Str.substring(1);
        }
        if (ipv6Str.endsWith(":")) {
            ipv6Str = ipv6Str + "0";
        }
        String[] groups = ipv6Str.split(":");
        for (int i = groups.length - 1; i > -1; i--) {
            if ("".equals(groups[i])) {
                int zerolength = 9 - groups.length;
                while (zerolength-- > 0) {
                    ret[(ib--)] = 0;
                    ret[(ib--)] = 0;
                }
            } else {
                int temp = Integer.parseInt(groups[i], 16);
                ret[(ib--)] = Convert.intToByte(temp);
                ret[(ib--)] = Convert.intToByte(temp >> 8);
            }
        }

        return new BigInteger(ret).longValue();
    }
}
