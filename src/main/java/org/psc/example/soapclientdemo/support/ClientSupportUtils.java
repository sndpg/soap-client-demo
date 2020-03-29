package org.psc.example.soapclientdemo.support;

import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.net.http.HttpClient;

public class ClientSupportUtils {

    public HttpComponentsMessageSender httpComponentsMessageSender() {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();

        HttpClient httpClient = HttpClient.newBuilder().sslContext(null).build();
        httpComponentsMessageSender.setHttpClient(null);

        return null;
    }

    public WebServiceTemplate createWebServiceTemplate() {
        return new WebServiceTemplateBuilder().interceptors()
                .build();
    }

    public Wss4jSecurityInterceptor wss4jSecurityInterceptor() {
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setSecurementActions(WSHandlerConstants.SIGNATURE);
        securityInterceptor.setSecurementSignatureCrypto(new Merlin());
        return securityInterceptor;
    }

}
