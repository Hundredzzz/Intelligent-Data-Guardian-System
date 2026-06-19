package com.guardian.common;

public enum RiskLevel {
    LOW("低风险", 20),
    MEDIUM("中风险", 50),
    HIGH("高风险", 75),
    CRITICAL("严重风险", 90);

    private final String label;
    private final int baseScore;

    RiskLevel(String label, int baseScore) {
        this.label = label;
        this.baseScore = baseScore;
    }

    public String getLabel() {
        return label;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public static RiskLevel fromScore(int score) {
        if (score >= CRITICAL.baseScore) {
            return CRITICAL;
        }
        if (score >= HIGH.baseScore) {
            return HIGH;
        }
        if (score >= MEDIUM.baseScore) {
            return MEDIUM;
        }
        return LOW;
    }
}
