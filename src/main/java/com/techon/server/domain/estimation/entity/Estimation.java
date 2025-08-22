package com.techon.server.domain.estimation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estimation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estimation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 모델명
    private String modelExample;

    // MOB/COM/APP/DEV
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 8)
    private Category category;

    // 증상 키워드 csv
    @Column(nullable = false, length = 1000)
    private String symptomKeywordsCsv;

    // 진단 결과
    @Column(nullable = false, length = 200)
    private String diagnosis;

    // 필요한 부품 csv
    @Column(length = 500)
    private String requiredPartsCsv;

    // 견적 범위 원문
    @Column(length = 100)
    private String estimateRaw;

    // 룰 우선순위 (겹칠 때 높은 값 우선)
    @Column(nullable = false)
    @Builder.Default
    private Integer priority = 0;
}
