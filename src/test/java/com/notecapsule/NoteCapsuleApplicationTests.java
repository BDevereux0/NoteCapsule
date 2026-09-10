package com.notecapsule;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest tells Junit to simulate spring application context.
@SpringBootTest
public class NoteCapsuleApplicationTests {

    /*
    Smoke test. This tells me the application can at least start. If I'm missing something like a dependency or
    datasource in application.properties, this will fail.

     @Test tells JUNIT to run this method, it does not get
     */
    @Test
    void contextLoad(){}

}
