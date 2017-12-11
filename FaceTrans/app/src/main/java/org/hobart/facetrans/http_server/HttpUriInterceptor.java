package org.hobart.facetrans.http_server;

public interface HttpUriInterceptor {

    boolean matches(String uri);

    void interceptor(Request request);

    void destroy();
}
