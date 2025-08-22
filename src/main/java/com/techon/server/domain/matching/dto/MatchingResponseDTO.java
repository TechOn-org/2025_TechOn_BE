package com.techon.server.domain.matching.dto;

public record MatchingResponseDTO(
        String bizesId,   // 상가업소번호
        String bizesNm,   // 상호명
        String indsScls,  // 약어(MOB/COM/APP/DEV)로 변환해 전달
        String lnoAdr,    // 지번주소
        String rdnmAdr,   // 도로명주소
        String flrNo      // 층정보
) {}
