package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloController {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @RequestMapping("/")
    public String index(@Autowired RestTemplate restTemplate) {
        System.out.println("starting trustCertificates() from controller");
        // This should be done during app startup, not as an explicit call here.
        // This is just to help with debugging, to give you a way to invoke this from outside.
        CloudFoundryCertificateTruster.trustCertificates();
        System.out.println("finished trustCertificates()");

        System.out.println("Making https call from controller");
        String result = restTemplate.getForObject("https://api.tacos.sso.identity.team/v2/info", String.class);
        System.out.println("result of https call = " + result);

        return result;
    }
}