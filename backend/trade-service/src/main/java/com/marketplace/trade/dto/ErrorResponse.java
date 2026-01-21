// Generated with Assistance By Clause Opus 4.5
// Reviewed and modified by Marcus Wright

package com.marketplace.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response DTO for API errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;

    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
