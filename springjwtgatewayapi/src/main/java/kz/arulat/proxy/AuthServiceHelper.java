package kz.arulat.proxy;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceHelper {

    public String verifyJwt(String accessToken){
        // request auth service and get result
        String url = "http://localhost:8082/hello";
        RestTemplate restTemplate = new RestTemplate();
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add(HttpHeaders.AUTHORIZATION, accessToken);
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.set(HttpHeaders.AUTHORIZATION, accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> res = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

//        JSONObject json = JSONObject.fromObject(res.getBody());

        return res.getBody();
    }



}
