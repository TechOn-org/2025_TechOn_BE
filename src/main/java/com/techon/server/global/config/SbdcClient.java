package com.techon.server.global.config;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SbdcClient {

    private final WebClient defaultWebClient;

    @Value("${app.sbdc.base-url}") String baseUrl;
    @Value("${app.sbdc.service-key}") String serviceKey;
    @Value("${app.sbdc.rows:100}") int rows;

    public Mono<JsonNode> storeListInRadius(double lat, double lng, int radiusMeters,
                                            String indsLclsCd, String indsMclsCd, String indsSclsCd,
                                            int pageNo) {
        return defaultWebClient.mutate().baseUrl(baseUrl).build()
                .get()
                .uri(uri -> uri.path("/storeListInRadius")
                        .queryParam("ServiceKey", serviceKey)
                        .queryParam("type", "json")
                        .queryParam("radius", radiusMeters)
                        .queryParam("cx", lng) // 중심 경도
                        .queryParam("cy", lat) // 중심 위도
                        .queryParam("indsLclsCd", indsLclsCd)
                        .queryParam("indsMclsCd", indsMclsCd)
                        .queryParam("indsSclsCd", indsSclsCd)
                        .queryParam("pageNo", pageNo)
                        .queryParam("numOfRows", rows)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
