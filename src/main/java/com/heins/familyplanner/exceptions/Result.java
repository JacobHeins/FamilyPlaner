package com.heins.familyplanner.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

public sealed interface Result<T> permits Result.Success, Result.Failure {

    enum ErrorType { NOT_FOUND, BAD_REQUEST, CONFLICT }

    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(ErrorType errorType, String error) implements Result<T> {
        public int httpStatus() {
            return switch (errorType) {
                case NOT_FOUND -> 404;
                case BAD_REQUEST -> 400;
                case CONFLICT -> 409;
            };
        }

        public ProblemDetail toProblemDetail() {
            HttpStatus status = HttpStatus.valueOf(httpStatus());
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, error);
            problem.setTitle(status.getReasonPhrase());
            problem.setType(URI.create("https://familyplanner.heins.com/errors/" + errorType.name().toLowerCase().replace('_', '-')));
            return problem;
        }
    }

    static <T> Result<T> success(T value) { return new Success<>(value); }
    static <T> Result<T> notFound(String error) { return new Failure<>(ErrorType.NOT_FOUND, error); }
    static <T> Result<T> badRequest(String error) { return new Failure<>(ErrorType.BAD_REQUEST, error); }
    static <T> Result<T> conflict(String error) { return new Failure<>(ErrorType.CONFLICT, error); }

    default boolean isSuccess() { return this instanceof Success; }
}
