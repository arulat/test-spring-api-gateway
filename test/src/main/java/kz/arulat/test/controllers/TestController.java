package kz.arulat.test.controllers;

import kz.arulat.test.models.TestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(method = RequestMethod.GET, value = "/test")
    public ResponseEntity<TestResponse> test(){
        return ResponseEntity.ok().body(new TestResponse("Fuck yeah! You did it! "));
    }
}
