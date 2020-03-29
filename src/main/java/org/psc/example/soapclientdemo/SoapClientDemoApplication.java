package org.psc.example.soapclientdemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.psc.soap.soapdemo.schema.GetStatusRequest;
import org.psc.soap.soapdemo.schema.GetStatusResponse;
import org.psc.soap.soapdemo.schema.KeyValue;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Collections;

@Slf4j
@SpringBootApplication
public class SoapClientDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoapClientDemoApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller() {{
            setContextPath("org.psc.soap.soapdemo.schema");
        }};

        DynamicaUsernameResolvingClientInterceptor dynamicaUsernameResolvingClientInterceptor =
                new DynamicaUsernameResolvingClientInterceptor();

        WebServiceTemplate webServiceTemplate =
                new WebServiceTemplateBuilder()
                        .interceptors(dynamicaUsernameResolvingClientInterceptor,
                                new Wss4jSecurityInterceptor() {{
                                    setSecurementUsername("testNo");
                                    setSecurementPassword("password123");
                                    setSecurementActions(WSHandlerConstants.USERNAME_TOKEN);
                                }})
                        .customizers(
                                template -> ((SaajSoapMessageFactory) template.getMessageFactory()).setMessageProperties(
                                        Collections.singletonMap(Wss4jSecurityInterceptor.SECUREMENT_USER_PROPERTY_NAME,
                                                "test")))
                        //                        .setWebServiceMessageFactory(new SaajSoapMessageFactory() {{
                        //                            setMessageProperties(
                        //                                    Collections.singletonMap(Wss4jSecurityInterceptor
                        //                                    .SECUREMENT_USER_PROPERTY_NAME,
                        //                                            "test"));
                        //                        }})
                        .setMarshaller(jaxb2Marshaller)
                        .setUnmarshaller(jaxb2Marshaller)
                        .build();

        return args -> {
            GetStatusResponse response =
                    (GetStatusResponse) webServiceTemplate.marshalSendAndReceive("http://localhost:8080/service/soap",
                            new JAXBElement<>(
                                    new QName("http://schema.soapdemo.soap.psc.org", "getStatusRequest", "psc"),
                                    GetStatusRequest.class,
                                    new GetStatusRequest() {{
                                        getKeyValue().add(new KeyValue() {{
                                            setKey("prop1");
                                            setValue("testValue");
                                        }});
                                    }}),
                            message -> {
                                //dynamicaUsernameResolvingClientInterceptor.username.set("test");
                                log.info("");

                            }
                    );
            log.info("RESPONSE 1 RECEIVED: {}", response.getStatus());

            GetStatusResponse response2 =
                    (GetStatusResponse) webServiceTemplate.marshalSendAndReceive("http://localhost:8080/service/soap",
                            new JAXBElement<>(
                                    new QName("http://schema.soapdemo.soap.psc.org", "getStatusRequest", "psc"),
                                    GetStatusRequest.class,
                                    new GetStatusRequest() {{
                                        getKeyValue().add(new KeyValue() {{
                                            setKey("prop1");
                                            setValue("testValue");
                                        }});
                                    }}),
                            message -> {
                                dynamicaUsernameResolvingClientInterceptor.username.set("test");
                                log.info("");

                            }
                    );
            log.info("RESPONSE 2 RECEIVED: {}", response2.getStatus());

            GetStatusResponse response3 =
                    (GetStatusResponse) webServiceTemplate.marshalSendAndReceive("http://localhost:8080/service/soap",
                            new JAXBElement<>(
                                    new QName("http://schema.soapdemo.soap.psc.org", "getStatusRequest", "psc"),
                                    GetStatusRequest.class,
                                    new GetStatusRequest() {{
                                        getKeyValue().add(new KeyValue() {{
                                            setKey("prop1");
                                            setValue("testValue");
                                        }});
                                    }}),
                            message -> {
                                dynamicaUsernameResolvingClientInterceptor.username.set("test2");
                                log.info("");

                            }
                    );
            log.info("RESPONSE 3 RECEIVED: {}", response3.getStatus());
        };
    }

    private class DynamicaUsernameResolvingClientInterceptor implements ClientInterceptor {
        private ThreadLocal<String> username = new ThreadLocal<>();

        @Override
        public boolean handleRequest(MessageContext messageContext) throws
                WebServiceClientException {
            String usernameForRequest = username.get();
            messageContext.setProperty(Wss4jSecurityInterceptor.SECUREMENT_USER_PROPERTY_NAME,
                    StringUtils.isBlank(usernameForRequest) ? "defaultUser" : usernameForRequest);
            return true;
        }

        @Override
        public boolean handleResponse(MessageContext messageContext) throws
                WebServiceClientException {
            return true;
        }

        @Override
        public boolean handleFault(MessageContext messageContext) throws WebServiceClientException {
            return true;
        }

        @Override
        public void afterCompletion(MessageContext messageContext, Exception ex) throws
                WebServiceClientException {

        }
    }

}
