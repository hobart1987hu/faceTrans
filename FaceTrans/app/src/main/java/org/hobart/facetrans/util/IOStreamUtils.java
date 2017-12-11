package org.hobart.facetrans.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by huzeyin on 2017/12/9.
 */

public class IOStreamUtils {

    public static String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int a = 0, b = 0;
        while ((b != -1) && !(a == '\r' && b == '\n')) {
            a = b;
            b = is.read();
            sb.append((char) (b));
        }

        String line = sb.toString();

        if (line == null || line.equals("\r\n")) {
            return null;
        }

        return line;
    }

    public static String inputStreamToString(InputStream is) {
        if (is == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] bytes = new byte[2048];
        try {
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }

            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
