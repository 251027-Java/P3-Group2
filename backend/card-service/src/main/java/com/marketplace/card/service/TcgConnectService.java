// Generated with assistance from Antigravity through Gemini
// Reviewed and modified by Liam Ruiz
package com.marketplace.card.service;

import com.marketplace.card.dto.TcgPriceDto;
import com.marketplace.card.dto.TcgProductDto;
import com.marketplace.card.dto.TcgWrapperDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * Service for connecting to the TCGCSV API.
 * Handles HTTP requests to fetch product and price data.
 * Also provides logic to distinguish single cards from sealed products.
 */
@Service
public class TcgConnectService {

    private static final Logger log = LoggerFactory.getLogger(TcgConnectService.class);
    private final RestClient restClient;
    private static final String BASE_URL = "https://tcgcsv.com/tcgplayer";

    public TcgConnectService() {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    /**
     * Fetches the list of products for a given group from the API.
     *
     * @param categoryId Category ID.
     * @param groupId    Group/Set ID.
     * @return List of TcgProductDto.
     */
    public List<TcgProductDto> fetchProducts(Integer categoryId, Integer groupId) {
        String url = "/{categoryId}/{groupId}/products";
        log.info("Fetching products from: {}", url);
        try {
            TcgWrapperDto<TcgProductDto> response = restClient
                    .get()
                    .uri(url, categoryId, groupId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null && response.isSuccess()) {
                return response.getResults();
            }
        } catch (Exception e) {
            log.error("Error fetching products", e);
        }
        return Collections.emptyList();
    }

    /**
     * Fetches current market prices for a given group from the API.
     *
     * @param categoryId Category ID.
     * @param groupId    Group/Set ID.
     * @return List of TcgPriceDto.
     */
    public List<TcgPriceDto> fetchPrices(Integer categoryId, Integer groupId) {
        String url = "/{categoryId}/{groupId}/prices";
        log.info("Fetching prices from: {}", url);
        try {
            TcgWrapperDto<TcgPriceDto> response = restClient
                    .get()
                    .uri(url, categoryId, groupId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null && response.isSuccess()) {
                return response.getResults();
            }
        } catch (Exception e) {
            log.error("Error fetching prices", e);
        }
        return Collections.emptyList();
    }

    /**
     * Determines if a product is a single card.
     * Uses heuristics based on extended data (e.g., presence of "Number") and name exclusions (e.g., "Booster Box").
     *
     * @param product The product to evaluate.
     * @return true if it is likely a single card, false otherwise.
     */
    public boolean isCard(TcgProductDto product) {
        // Heuristic 1: Check extended data for "Number" or "Rarity"
        if (product.getExtendedData() != null) {
            boolean hasNumber =
                    product.getExtendedData().stream().anyMatch(ed -> "Number".equalsIgnoreCase(ed.getName()));
            if (hasNumber) return true;
        }

        // Heuristic 2: Name blacklist (Sealed products)
        String name = product.getName().toLowerCase();
        if (name.contains("booster")
                || name.contains("box")
                || name.contains("pack")
                || name.contains("tin")
                || name.contains("case")
                || name.contains("display")
                || name.contains("collection")
                || name.contains("deck")
                || name.contains("bundle")) {
            return false;
        }

        return true; // Default to true if not explicitly excluded
    }
}
