package com.spr.sec.server;

import com.spr.sec.web.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicAuthenticationTest {

    @LocalServerPort
    int port;

    RestTemplate client = new RestTemplate();
    TestRestTemplate testClient = new TestRestTemplate("v", "1");

    private String greetingUrl(){
       return "http://localhost:"+port+"/rest/greeting";
    }

    @Test
    void test1_basicFail(){
        /**
         * Error 발생하여 401 확인
         */
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, ()->{
            client.getForObject(greetingUrl(), String.class);
        });
        assertEquals(401, exception.getRawStatusCode());
    }

    @Test
    void test2_basicSuccess(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic "+ Base64.getEncoder().encodeToString(
                "user:1111".getBytes()
        ));
        HttpEntity entity = new HttpEntity(null, headers);
        ResponseEntity<String> res = client.exchange(greetingUrl(), HttpMethod.GET, entity, String.class);
        assertEquals("hello",res.getBody());
    }

    @Test
    void test3_finalTest(){
        ResponseEntity<List<User>> res = testClient.exchange("http://localhost:" + port + "/rest/user/list",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
        });
        //assertEquals(3,res.getBody().size());
        System.out.println(res.getBody());
    }
}
