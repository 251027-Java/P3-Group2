// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO representing price information from the TCGCSV API.
 * Contains market price, low/mid/high prices, and subtype information.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcgPriceDto {
    private Long productId;
    private BigDecimal marketPrice;
    private BigDecimal lowPrice;
    private BigDecimal midPrice;
    private BigDecimal highPrice;
    private String subTypeName;
}
