package com.amrit.fraud.service;

import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FraudService {

    public static class Result {
        private int score;
        private String decision;
        private List<String> reasons;

        public Result(int score, String decision, List<String> reasons) {
            this.score = score;
            this.decision = decision;
            this.reasons = reasons;
        }

        public int getScore() { return score; }
        public String getDecision() { return decision; }
        public List<String> getReasons() { return reasons; }
    }

    public Result evaluate(
            int amount,
            String merchant,
            String location,
            int txnCount,
            Integer time,
            Integer avgTxnOverride,
            Integer maxTxnOverride,
            String deviceOverride,
            boolean isRapid
    ) {
        int score = 5; 
        List<String> reasons = new ArrayList<>();

        int avgTxn = 5000;
        int maxTxn = 20000;

        if (avgTxnOverride != null) avgTxn = avgTxnOverride;
        if (maxTxnOverride != null) maxTxn = maxTxnOverride;

        boolean oddTime = (time != null) ? (time < 6) : (LocalTime.now().getHour() < 6);
        boolean newDevice = "new".equals(deviceOverride);

        if (amount > 10 * avgTxn) {
            score += 30;
            reasons.add("Severe deviation from normal spend behavior");
        } else if (amount > 5 * avgTxn) {
            score += 20;
            reasons.add("Moderate deviation from normal spend behavior");
        }

        if (amount > maxTxn) {
            score += 20;
            reasons.add("Amount exceeds historical maximum transaction");
        }

        if (isRapid) {
            score += 20;
            reasons.add("High transaction velocity detected (< 5s)");
        }

        if (txnCount > 5) {
            score += 10;
            reasons.add("High daily transaction volume");
        }

        if (newDevice) {
            score += 15;
            reasons.add("New/unrecognized device used");
        }

        if (oddTime) {
            score += 10;
            reasons.add("Transaction attempted at an unusual hour (12 AM - 6 AM)");
        }

        if ("international".equals(location)) {
            score += 15;
            reasons.add("Unusual transaction location (International)");
        }

        if ("crypto".equals(merchant) || "gaming".equals(merchant) || "jewelry".equals(merchant)) {
            score += 15;
            String displayMerchant = merchant.substring(0, 1).toUpperCase() + merchant.substring(1);
            reasons.add("High-risk merchant category (" + displayMerchant + ")");
        }

        if (score > 100) score = 100;

        String decision = (score >= 70) ? "DECLINED" : "APPROVED";

        return new Result(score, decision, reasons);
    }
}