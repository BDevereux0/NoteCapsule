package com.notecapsule;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class NoteCapsuleApplicationTests {

    /*
    Smoke test. This tells me the application can at least start. If I'm missing something like a dependency or
    datasource in application.properties, this will fail.
     */
    @Test
    void contextLoad(){}
}
