package com.envyguard.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for Spring context loading.
 * Disabled by default as it requires full infrastructure setup.
 * Enable and run locally when validating application context.
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires full infrastructure - run locally for integration testing")
class EnvyGuardBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
