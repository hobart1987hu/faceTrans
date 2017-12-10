package org.hobart.facetrans.micro_server;

import org.hobart.facetrans.util.IOStreamUtils;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzeyin on 2017/12/9.
 */

public class AndroidMicroServer {

    private static final String LOGO_PREFIX = "AndroidMicroServer ->";

    private int mPort;


    private ServerSocket mServerSocket;


    private ExecutorService mThreadPool = Executors.newCachedThreadPool();


    private List<ResUriHandler> mResUriHandlerList = new ArrayList<ResUriHandler>();

    private volatile boolean monitor = true;


    public AndroidMicroServer(int port) {
        this.mPort = port;
    }

    public void registerResUriHandler(ResUriHandler resUriHandler) {
        this.mResUriHandlerList.add(resUriHandler);
    }

    public void unregisterResUriHandlerList() {
        for (ResUriHandler resUriHandler : mResUriHandlerList) {
            resUriHandler.destroy();
            resUriHandler = null;
        }
        mResUriHandlerList.clear();
    }

    public void start() {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mServerSocket = new ServerSocket(mPort);
                    while (monitor) {
                        Socket socket = mServerSocket.accept();
                        handlerSocketAsync(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        if (monitor) {
            monitor = false;
        }
        unregisterResUriHandlerList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mServerSocket != null) {
                    try {
                        mServerSocket.close();
                        mServerSocket = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void handlerSocketAsync(final Socket socket) {
        mThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Request request = createRequest(socket);
                for (ResUriHandler resUriHandler : mResUriHandlerList) {
                    if (!resUriHandler.matches(request.getUri())) {
                        continue;
                    }
                    resUriHandler.handler(request);
                }
            }
        });
    }

    private Request createRequest(Socket socket) {
        Request request = new Request();
        request.setClient(socket);
        try {
            SocketAddress socketAddress = socket.getRemoteSocketAddress();
            InputStream is = socket.getInputStream();
            String requestLine = IOStreamUtils.readLine(is);
            LogcatUtils.d(LOGO_PREFIX + "createRequest requestLine:" + requestLine);
            String requestType = requestLine.split(" ")[0];
            String requestUri = requestLine.split(" ")[1];
            requestUri = URLDecoder.decode(requestUri, "UTF-8");
            request.setUri(requestUri);
            String header = "";
            while ((header = IOStreamUtils.readLine(is)) != null) {
                String headerKey = header.split(":")[0];
                String headerVal = header.split(":")[1];
                request.addHeader(headerKey, headerVal);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }
}
