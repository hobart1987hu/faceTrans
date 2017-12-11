package org.hobart.facetrans.http_server;

import java.net.Socket;
import java.util.HashMap;

public class Request {

    private String mUri;
    private HashMap<String, String> mHeaderMap = new HashMap<String, String>();
    private Socket mClient;


    public Request() {

    }

    public Socket getClient() {

        return mClient;
    }

    public void setClient(Socket client) {
        this.mClient = client;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String mUri) {
        this.mUri = mUri;
    }

    public void addHeader(String key, String value) {
        this.mHeaderMap.put(key, value);
    }
}
