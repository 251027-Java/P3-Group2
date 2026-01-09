package org.example.jenkinstest;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JenkinsTestApplicationTests {

    @Test
    void basic() {
        assertTrue(true);
    }
}
