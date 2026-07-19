package com.heins.familyplanner.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultTest {

    @Test
    void forbidden_mapsToRfc9457ProblemDetail() {
        ProblemDetail problem = Result.forbidden("Family access is not permitted").toProblemDetail();

        assertEquals(403, problem.getStatus());
        assertEquals("Forbidden", problem.getTitle());
        assertEquals("Family access is not permitted", problem.getDetail());
        assertEquals("https://familyplanner.heins.com/errors/forbidden", problem.getType().toString());
    }
}