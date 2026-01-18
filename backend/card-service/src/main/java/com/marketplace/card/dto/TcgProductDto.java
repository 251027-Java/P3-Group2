// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * DTO representing a product from the TCGCSV API.
 * Contains product details like ID, name, image URL, and extended data.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TcgProductDto {
    private Long productId;
    private String name;
    private String cleanName;
    private String imageUrl;
    private Integer categoryId;
    private Integer groupId; // serialization will map this
    private List<ExtendedDataDto> extendedData;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtendedDataDto {
        private String name;
        private String displayName;
        private String value;
    }
}
