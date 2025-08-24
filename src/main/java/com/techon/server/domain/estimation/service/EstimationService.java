package com.techon.server.domain.estimation.service;

import com.techon.server.domain.estimation.dto.EstimationRequestDTO;
import com.techon.server.domain.estimation.dto.EstimationResponseDTO;
import com.techon.server.domain.estimation.entity.Category;
import com.techon.server.domain.estimation.entity.Estimation;
import com.techon.server.domain.estimation.repository.EstimationRepository;
import com.techon.server.domain.estimation.util.TextNormalizer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EstimationService {

    private final EstimationRepository estimationRepository;

    private static final Map<String, List<String>> SYN = new HashMap<>() {{
        put("화면", List.of(
                "디스플레이","lcd","액정","패널","스크린",
                "화면안나옴","화면 안나옴","블랙스크린","화이트스크린",
                "번인","잔상","노이즈","멍","백화"
        ));
        put("깨짐", List.of(
                "파손","크랙","금감","금","균열","깨졌",
                "금가","금갔","찍힘","파손됨","깨져서","깨짐"
        ));
        put("액정", List.of(
                "lcd","디스플레이","패널","유리",
                "전면유리","프런트글라스","프론트글라스","glass"
        ));
        put("터치", List.of(
                "터치불가","터치 안됨","유령터치","touch","터치오류",
                "터치안돼","터치안되요","터치 먹통","터치지연","터치오작동","터치불량"
        ));
        put("불량", List.of(
                "고장","오류","문제","이상",
                "불안정","오작동","이슈","상태불량","지연"
        ));
        put("줄감", List.of(
                "줄 감","줄이감","줄 무늬","줄생김","줄간섭","라인생김",
                "가로줄","세로줄","줄나옴","화면줄",
                "녹색줄","초록줄","빨간줄","보라줄","컬러줄"
        ));
        put("전원", List.of(
                "부팅","전원불가","전원안켜짐","꺼짐",
                "전원꺼짐","자동꺼짐","먹통","부팅안됨",
                "무한부팅","로고무한","재부팅반복","꺼졌다켜짐"
        ));
        put("배터리", List.of(
                "충전","전지","배터리수명","배터리빨리닳",
                "광탈","빨리 닳아","배터리소모","잔량튐",
                "배터리부풀음","스웰링","팽창","배터리빵빵"
        ));
        put("소음", List.of(
                "팬소음","냉각팬","쿨링","윙윙",
                "잡음","지지직","찌직","삐소리",
                "팬이상","쿨러소음","베어링소음","진동소음"
        ));
        put("충전", List.of("충전안됨","충전 안돼","충전 느림","충전 불량","충전 중단",
                "케이블 인식","포트 헐거움","포트불량","c타입 불량","라이트닝 불량","접촉불량"));
        put("발열", List.of("과열","뜨거움","열남","고온","온도 높음","과열로 종료"));
        put("카메라", List.of("카메라안됨","초점불가","흐림","번짐","카메라오류"));
        put("스피커", List.of("소리안남","소리작음","먹통","울림","왜곡"));
    }};

    private static final Set<String> JOSA = Set.of(
            "이","가","은","는","을","를","도","만","요","이요","에요","이에요","네요","가요","인데","인데요","라","이라"
    );

    private static String tokenPattern(String t) {
        // 토큰 + 조사 허용 (예: "불량", "불량이에요")
        return Pattern.quote(t) + "(?:" + String.join("|", JOSA) + ")?";
    }

    private static int levenshtein(String a, String b) {
        int n=a.length(), m=b.length();
        if (n==0) return m; if (m==0) return n;
        int[] prev=new int[m+1], cur=new int[m+1];
        for (int j=0;j<=m;j++) prev[j]=j;
        for (int i=1;i<=n;i++) {
            cur[0]=i;
            char ca=a.charAt(i-1);
            for (int j=1;j<=m;j++) {
                int cost = (ca==b.charAt(j-1))?0:1;
                cur[j]=Math.min(Math.min(cur[j-1]+1, prev[j]+1), prev[j-1]+cost);
            }
            int[] tmp=prev; prev=cur; cur=tmp;
        }
        return prev[m];
    }

    private boolean flexibleHit(String normText, String kwNorm) {
        String textNoSp = normText.replace(" ", "");
        String keyNoSp  = kwNorm.replace(" ", "");
        if (textNoSp.contains(keyNoSp)) return true; // "터치불량" vs "터치 불량"

        String[] toks = kwNorm.split("\\s+");
        // 1) 토큰 순서 유지, 사이 단어 허용: "터치.*불량"
        if (toks.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<toks.length;i++) {
                if (i>0) sb.append(".*?");
                sb.append(tokenPattern(toks[i]));
            }
            if (Pattern.compile(sb.toString()).matcher(normText).find()) return true;
        }

        // 2) 각 토큰이 텍스트 내에 (조사 허용으로) 존재하면 hit
        boolean allPresent = true;
        for (String tok : toks) {
            String tp = tokenPattern(tok);
            if (!Pattern.compile(tp).matcher(normText).find()) {
                allPresent = false; break;
            }
        }
        if (allPresent && toks.length > 0) return true;

        // 3) 동의어 확장(토큰 단위)
        for (String tok : toks) {
            for (String syn : SYN.getOrDefault(tok, List.of())) {
                String s = TextNormalizer.normalize(syn);
                if (normText.contains(s) || normText.replace(" ","").contains(s.replace(" ",""))) {
                    return true;
                }
            }
        }

        // 4) 오타 허용(레벤슈타인 <= 1): 텍스트 단어들과 비교
        String[] words = normText.split("\\s+");
        outer:
        for (String tok : toks.length==0 ? new String[]{kwNorm} : toks) {
            // 토큰 그대로 있으면 패스
            if (normText.contains(tok)) continue;
            for (String w : words) {
                if (levenshtein(tok, w) <= 1) continue outer; // 토큰별 하나라도 가까우면 통과
            }
            return false; // 이 토큰은 너무 멀다
        }
        return true;
    }

    public EstimationResponseDTO analyze(EstimationRequestDTO request) {

        Category category = request.category();
        String norm = TextNormalizer.normalize(request.text());

        List<Estimation> rules = estimationRepository.findByCategoryOrderByPriorityDescIdAsc(request.category());

        Match best = null;
        for (Estimation r : rules) {
            Match m = score(norm, r);
            if (m != null && (best == null || m.score > best.score)) best = m;
        }

        if (best == null) {
            return EstimationResponseDTO.builder()
                    .estimationId(null)
                    .diagnosis(null)
                    .requiredParts(List.of())
                    .estimateMin(0).estimateMax(0).estimateRaw(null)
                    .category(category)
                    .matchedKeywords(List.of())
                    .score(0)
                    .normalizedText(norm)
                    .build();
        }
        return EstimationResponseDTO.builder()
                .estimationId(best.rule.getId())
                .diagnosis(best.rule.getDiagnosis())
                .requiredParts(TextNormalizer.csvToList(best.rule.getRequiredPartsCsv()))
                .estimateRaw(best.rule.getEstimateRaw())
                .estimateMin(best.estimate[0])
                .estimateMax(best.estimate[1])
                .category(category)
                .matchedKeywords(best.matched)
                .score(best.score)
                .normalizedText(norm)
                .build();
    }

    private Match score(String normText, Estimation rule) {
        var kws = TextNormalizer.csvToList(rule.getSymptomKeywordsCsv());
        if (kws.isEmpty()) return null;

        int score = Optional.ofNullable(rule.getPriority()).orElse(0);
        List<String> matched = new ArrayList<>();

        for (String kw : kws) {
            String nk = TextNormalizer.normalize(kw);
            if (nk.isBlank()) continue;

            boolean hit = flexibleHit(normText, nk);
            if (!hit) {
                for (String token : nk.split(" ")) {
                    if (token.length() < 2) continue;
                    for (String syn : SYN.getOrDefault(token, List.of())) {
                        if (normText.contains(TextNormalizer.normalize(syn))) { hit = true; break; }
                    }
                    if (hit) break;
                }
            }
            if (hit) {
                matched.add(kw);
                score += 10;
                if (normText.contains(nk)) score += 5; // 완전 일치 보너스
                else if (normText.replace(" ","").contains(nk.replace(" ",""))) score += 3; // 공백만 무시
            }
        }

        if (matched.isEmpty()) return null;

        int[] est = TextNormalizer.parseEstimateRange(rule.getEstimateRaw());
        return new Match(rule, matched, score, est);
    }

    private record Match(Estimation rule, List<String> matched, int score, int[] estimate) {}
}
