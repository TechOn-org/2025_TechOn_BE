package com.techon.server.domain.estimation.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextNormalizer {

    private static final Pattern NON_KO_ALNUM = Pattern.compile("[^0-9A-Za-z가-힣 ]+");

    public static String normalize(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFKC).toLowerCase(Locale.KOREAN);
        t = NON_KO_ALNUM.matcher(t).replaceAll(" ");
        t = t.replaceAll("\\s+", " ").trim();
        return t;
    }

    public static List<String> csvToList(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        String[] arr = csv.split("\\s*,\\s*");
        List<String> out = new ArrayList<>();
        for (String a : arr) if (!a.isBlank()) out.add(a.trim());
        return out;
    }

    public static int[] parseEstimateRange(String raw) {
        if (raw == null) return new int[]{0,0};
        String only = raw.replaceAll("[^0-9~\\-]", "");
        String[] p = only.split("[~\\-]");
        try {
            if (p.length == 2) return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
            if (p.length == 1) { int v = Integer.parseInt(p[0]); return new int[]{v, v}; }
        } catch (NumberFormatException ignored) {}
        return new int[]{0,0};
    }
}
