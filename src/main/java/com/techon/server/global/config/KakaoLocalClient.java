package com.techon.server.global.config;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private final WebClient defaultWebClient;

    @Value("${app.kakao.rest-api-key}") String kakaoKey;

    public Mono<double[]> geocode(String query) {
        return defaultWebClient.mutate()
                .baseUrl("https://dapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoKey)
                .build()
                .get()
                .uri(uri -> uri.path("/v2/local/search/address.json").queryParam("query", query).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    var docs = json.path("documents");
                    if (!docs.isArray() || docs.size() == 0) throw new IllegalArgumentException("주소 결과 없음");
                    var first = docs.get(0);
                    double lat = first.path("y").asDouble();
                    double lng = first.path("x").asDouble();
                    return new double[]{lat, lng};
                });
    }
}
