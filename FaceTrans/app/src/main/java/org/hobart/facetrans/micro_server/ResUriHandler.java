package org.hobart.facetrans.micro_server;

public interface ResUriHandler {

    boolean matches(String uri);

    void handler(Request request);

    void destroy();
}
