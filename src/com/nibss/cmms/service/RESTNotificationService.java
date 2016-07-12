package com.nibss.cmms.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nibss.cmms.web.domain.MandateRequest;
import com.nibss.cmms.web.domain.MandateUpdateRequest;


@Component
public class RESTNotificationService {
    public void postMandateUpdate(String uri, MandateUpdateRequest mandateUpdateRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MandateUpdateRequest> reqEntity = new HttpEntity<>(mandateUpdateRequest);
        ResponseEntity<Void> res = restTemplate.exchange(uri, HttpMethod.POST, reqEntity, Void.class);

    }
    public void postMandateCreation(String uri, MandateRequest mandateRequest) {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MandateRequest> reqEntity = new HttpEntity<>(mandateRequest);
        ResponseEntity<Void> res = restTemplate.exchange(uri, HttpMethod.POST, reqEntity, Void.class);

    }
}
