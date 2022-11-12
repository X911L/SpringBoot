//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.xl.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

public class ApiSignUtilBase {
    private static final String CONTENT_CHARSET = "UTF-8";
    private static final String HMAC_ALGORITHM = "HmacSHA1";

    public ApiSignUtilBase() {
    }

    public static String getTimeStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(new Date());
    }

    public static String getAuth(String secretId, String secretKey, String timeStr, String source) throws Exception {
        StringBuilder sign = (new StringBuilder("date: ")).append(timeStr);
        if (StringUtils.isNotEmpty(source)) {
            sign.append("\n").append("source: ").append(source);
        }

        Mac mac1 = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac1.getAlgorithm());
        mac1.init(secretKeySpec);
        byte[] hash = mac1.doFinal(sign.toString().getBytes("UTF-8"));
        String sig = Base64.encode(hash);
        StringBuilder auth = (new StringBuilder("hmac id=\"")).append(secretId).append("\", algorithm=\"hmac-sha1\"");
        if (StringUtils.isNotEmpty(source)) {
            auth.append(", headers=\"date source\", signature=\"");
        } else {
            auth.append(", headers=\"date\", signature=\"");
        }

        return auth.append(sig).append("\"").toString();
    }
}
