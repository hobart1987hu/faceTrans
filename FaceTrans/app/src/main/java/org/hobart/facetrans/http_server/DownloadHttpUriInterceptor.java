package org.hobart.facetrans.http_server;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class DownloadHttpUriInterceptor implements HttpUriInterceptor {

    private static final String DOWNLOAD_PREFIX = "/download/";


    public DownloadHttpUriInterceptor() {

    }

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(DOWNLOAD_PREFIX);
    }

    @Override
    public void interceptor(Request request) {

        String uri = request.getUri();

        final String filePath = uri.replace("/download/", "");

        Socket socket = request.getClient();
        OutputStream os = null;
        PrintStream printStream = null;
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
        } catch (IOException e) {
            e.printStackTrace();
            showFileNoFound(printStream);
            return;
        }

        if (TextUtils.isEmpty(filePath)) {
            showFileNoFound(printStream);
            return;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            showFileNoFound(printStream);
            return;
        }
        printStream.println("HTTP/1.1 200 OK");
        printStream.println("Content-Length:" + file.length());
        printStream.println("Content-Type:application/octet-stream");
        printStream.println();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            showFileNoFound(printStream);
            return;
        }
        try {
            int len = 0;
            byte[] bytes = new byte[2048];
            while ((len = fis.read(bytes)) != -1) {
                printStream.write(bytes, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showFileNoFound(printStream);
            return;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    fis = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        printStream.flush();
        printStream.close();

    }

    private void showFileNoFound(PrintStream printStream) {
        printStream.println("HTTP/1.1 404 NotFound");
        printStream.println();
        printStream.flush();
        printStream.close();
    }

    @Override
    public void destroy() {
    }
}
