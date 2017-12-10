package org.hobart.facetrans.micro_server;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.util.IOStreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class IndexResUriHandler implements ResUriHandler {

    @Override
    public boolean matches(String uri) {
        if (uri == null || uri.equals("") || uri.equals("/")) {
            return true;
        }
        return false;
    }

    @Override
    public void handler(Request request) {
        String indexHtml = null;
        try {
            InputStream is = FaceTransApplication.getApp().getAssets().open("index.html");
            indexHtml = IOStreamUtils.inputStreamToString(is);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (request.getClient() != null && indexHtml != null) {
            OutputStream outputStream = null;
            PrintStream printStream = null;
            try {
                outputStream = request.getClient().getOutputStream();
                printStream = new PrintStream(outputStream);
                printStream.println("HTTP/1.1 200 OK");
//                printStream.println("Content-Length:" + indexHtml.length());
                printStream.println("Content-Type:text/html");
                printStream.println("Cache-Control:no-cache");
                printStream.println("Pragma:no-cache");
                printStream.println("Expires:0");
                printStream.println();

                indexHtml = convert(indexHtml);

                byte[] bytes = indexHtml.getBytes("UTF-8");
                printStream.write(bytes);

                printStream.flush();
                printStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                        outputStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (printStream != null) {
                    try {
                        printStream.close();
                        printStream = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void destroy() {
    }

    public String convert(String indexHtml) {
        return indexHtml;
    }
}
