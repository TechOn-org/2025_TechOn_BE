package com.techon.server.domain.matching.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShopSubcat {

    MOB("S2", "S202", "S20201"),
    COM("S2", "S201", "S20101"),
    APP("S2", "S205", "S20501"),
    DEV("S2", "S206", "S20699");

    private final String lcls; // indsLclsCd
    private final String mcls; // indsMclsCd
    private final String scls; // indsSclsCd

    public static ShopSubcat fromCode(String code) {
        return ShopSubcat.valueOf(code.toUpperCase());
    }

    public static String toSubcatCode(String indsSclsCd) {
        for (ShopSubcat s : values()) if (s.getScls().equalsIgnoreCase(indsSclsCd)) return s.name();
        return indsSclsCd; // 매칭 안 되면 원본 유지
    }
}
