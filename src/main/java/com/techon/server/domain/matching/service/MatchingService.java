package com.techon.server.domain.matching.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.techon.server.domain.matching.dto.MatchingResponseDTO;
import com.techon.server.domain.matching.entity.ShopSubcat;
import com.techon.server.global.config.KakaoLocalClient;
import com.techon.server.global.config.SbdcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final KakaoLocalClient kakao;
    private final SbdcClient sbdc;

    public Mono<List<MatchingResponseDTO>> find(String si, String gu, String dong, String cat, int radiusMeters) {
        ShopSubcat sub = ShopSubcat.fromCode(cat);
        String query = (si + " " + gu + " " + dong).trim();

        return kakao.geocode(query)
                .flatMap(ll -> fetchFirstPage(ll[0], ll[1], radiusMeters, sub))
                .map(items -> mapToResponse(items, sub));
    }

    private Mono<List<JsonNode>> fetchFirstPage(double lat, double lng, int radiusMeters, ShopSubcat sub) {
        return sbdc.storeListInRadius(lat, lng, radiusMeters, sub.getLcls(), sub.getMcls(), sub.getScls(), 1)
                .map(this::extractItems);
    }

    private List<JsonNode> extractItems(JsonNode root) {
        List<JsonNode> items = new ArrayList<>();
        JsonNode body = root.path("body");
        if (body.isMissingNode()) body = root;

        JsonNode raw = body.path("items");
        if (raw.isMissingNode()) raw = body.path("item");

        if (raw.isArray()) raw.forEach(items::add);
        else if (raw.isObject()) {
            JsonNode inner = raw.path("item");
            if (inner.isArray()) inner.forEach(items::add);
            else if (!inner.isMissingNode()) items.add(inner);
            else items.add(raw);
        }
        return items;
    }

    private List<MatchingResponseDTO> mapToResponse(List<JsonNode> items, ShopSubcat requested) {
        List<MatchingResponseDTO> out = new ArrayList<>();
        for (JsonNode it : items) {
            String bizesId  = txt(it, "bizesId");
            String bizesNm  = txt(it, "bizesNm");
            String indsSclsCd = txt(it, "indsSclsCd");
            String alias    = ShopSubcat.toSubcatCode(indsSclsCd); // S20201 -> MOB
            String lnoAdr   = txt(it, "lnoAdr");
            String rdnmAdr  = txt(it, "rdnmAdr");
            String flrNo    = txt(it, "flrNo");

            // 요청 소분류와 다르면 필터링
            if (!requested.name().equals(alias)) continue;

            out.add(new MatchingResponseDTO(bizesId, bizesNm, alias, lnoAdr, rdnmAdr, flrNo));
        }
        return out;
    }

    private static String txt(JsonNode n, String f) {
        JsonNode v = n.path(f);
        return v.isMissingNode() || v.isNull() ? null : v.asText();
    }
}
