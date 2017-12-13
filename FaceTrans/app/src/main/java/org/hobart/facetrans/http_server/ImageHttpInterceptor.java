package org.hobart.facetrans.http_server;

import android.util.Log;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;

public class ImageHttpInterceptor implements HttpUriInterceptor {

    public static final String IMAGE_PREFIX = "/image/";

    public ImageHttpInterceptor() {
    }

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(IMAGE_PREFIX);
    }

    @Override
    public void interceptor(Request request) {

        String uri = request.getUri();

        String imageUrl = uri.replace("/image/", "");

        try {
            imageUrl = URLDecoder.decode(imageUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File imageFile = null;
        if (imageUrl.equals("appIcon/")) {
            imageFile = new File(FileUtils.getWebTransferImage("", FTType.IMAGE));
        } else if (imageUrl.startsWith("apk/")) {
            imageUrl = imageUrl.replace("apk/", "");
            imageFile = new File(FileUtils.getWebTransferImage(imageUrl, FTType.APK));
        } else if (imageUrl.startsWith("music/")) {
            imageUrl = imageUrl.replace("music/", "");
            imageFile = new File(FileUtils.getWebTransferImage(imageUrl, FTType.MUSIC));
        } else if (imageUrl.startsWith("video/")) {
            imageUrl = imageUrl.replace("video/", "");
            imageFile = new File(FileUtils.getWebTransferImage(imageUrl, FTType.VIDEO));
        } else {
            imageFile = new File(imageUrl);
        }
        Socket socket = request.getClient();
        OutputStream os = null;
        PrintStream printStream = null;
        if (!imageFile.exists()) {
            showFileNoFound(printStream);
            return;
        }
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        printStream.println("HTTP/1.1 200 OK");
        printStream.println("Content-Length:" + imageFile.length());
        printStream.println("Content-Type:multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*");
        printStream.println();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imageFile);
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
