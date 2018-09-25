package com.nibss.cmms.app.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author aakintola
 */
@Component
public class RestClient {

	private static final Logger LOG = Logger.getLogger(RestClient.class.getName());

	public String postToClient(String uri, String request) {
		String response = "";
		try {
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> entity = new HttpEntity<String>(request, headers);
		ResponseEntity<String> t =restTemplate.exchange(uri,HttpMethod.POST, entity, String.class);
			LOG.info("Status   "+t.getStatusCode().toString());
			
			if(t.hasBody()){
				LOG.info("Body   "+t.getBody());	
			}
			
			return t.getStatusCode().name();
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}

		return "";
		// return null;
	}
}
