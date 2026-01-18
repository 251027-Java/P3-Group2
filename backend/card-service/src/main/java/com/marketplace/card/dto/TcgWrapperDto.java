// Generated with assistance from Antigravity
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Generic wrapper DTO for TCGCSV API responses.
 * Standardizes handling of success status, errors, and the result payload.
 *
 * @param <T> The type of the result list items (e.g. TcgProductDto, TcgPriceDto).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcgWrapperDto<T> {
    private boolean success;
    private List<String> errors;
    private List<T> results;
}
