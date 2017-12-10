package org.hobart.facetrans.micro_server;

import android.app.Activity;

import org.hobart.facetrans.model.FTFile;
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

public class DownloadResUriHandler implements ResUriHandler {

    private static final String DOWNLOAD_PREFIX = "/download/";


    public DownloadResUriHandler() {

    }

    @Override
    public boolean matches(String uri) {
        return uri.startsWith(DOWNLOAD_PREFIX);
    }

    @Override
    public void handler(Request request) {
        //1.get the image file name from the uri
        String uri = request.getUri();
        String fileName = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
        //bug :resolve chinese incorrect code
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //查找
        FTFile fileInfo = FileUtils.getFileInfo(fileName);

        //2.check the local system has the file. if has, return the image file, else return 404 to the client
        Socket socket = request.getClient();
        OutputStream os = null;
        PrintStream printStream = null;
        try {
            os = socket.getOutputStream();
            printStream = new PrintStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileInfo == null) {//not exist this file
            printStream.println("HTTP/1.1 404 NotFound");
            printStream.println();
        } else {
            printStream.println("HTTP/1.1 200 OK");
//            image/jpeg
            printStream.println("Content-Length:" + fileInfo.getSize());
//            printStream.println("Content-Type:image/png");
//            multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*
            printStream.println("Content-Type:application/octet-stream");
//            printStream.println("Content-Type:multipart/mixed,text/html,image/png,image/jpeg,image/gif,image/x-xbitmap,application/vnd.oma.dd+xml,*/*");
            printStream.println();

            File file = null;
            FileInputStream fis = null;
            try {
                file = new File(fileInfo.getFilePath());
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                int len = 0;
                byte[] bytes = new byte[2048];
                while ((len = fis.read(bytes)) != -1) {
                    printStream.write(bytes, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        }
        printStream.flush();
        printStream.close();

    }

    @Override
    public void destroy() {
    }
}
