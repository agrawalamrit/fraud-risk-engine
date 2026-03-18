package com.amrit.fraud.controller;

import com.amrit.fraud.service.FraudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FraudController {

    @Autowired
    private FraudService service;

    public static class FraudRequest {
        private int amount;
        private String merchant;
        private String location;
        private Integer avgTxn;
        private Integer maxTxn;
        private String device;
        private Integer time;

        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }
        public String getMerchant() { return merchant; }
        public void setMerchant(String merchant) { this.merchant = merchant; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getAvgTxn() { return avgTxn; }
        public void setAvgTxn(Integer avgTxn) { this.avgTxn = avgTxn; }
        public Integer getMaxTxn() { return maxTxn; }
        public void setMaxTxn(Integer maxTxn) { this.maxTxn = maxTxn; }
        public String getDevice() { return device; }
        public void setDevice(String device) { this.device = device; }
        public Integer getTime() { return time; }
        public void setTime(Integer time) { this.time = time; }
    }

    public static class TxnRecord {
        public String time;
        public int amount;
        public String merchant;
        public int score;
        public String decision;

        public TxnRecord(String time, int amount, String merchant, int score, String decision) {
            this.time = time; this.amount = amount; this.merchant = merchant; 
            this.score = score; this.decision = decision;
        }
    }

    @PostMapping("/check")
    public FraudService.Result check(@RequestBody FraudRequest request, HttpSession session) {
        
        Integer txnCount = (Integer) session.getAttribute("txnCount");
        if (txnCount == null) txnCount = 0;
        session.setAttribute("txnCount", txnCount + 1);

        Long lastTxnTime = (Long) session.getAttribute("lastTxnTime");
        long now = System.currentTimeMillis();
        boolean isRapid = (lastTxnTime != null && (now - lastTxnTime < 5000));
        session.setAttribute("lastTxnTime", now);

        FraudService.Result result = service.evaluate(
                request.getAmount(), request.getMerchant(), request.getLocation(),
                txnCount, request.getTime(), request.getAvgTxn(), 
                request.getMaxTxn(), request.getDevice(), isRapid
        );

        @SuppressWarnings("unchecked")
        List<TxnRecord> history = (List<TxnRecord>) session.getAttribute("history");
        if (history == null) history = new ArrayList<>();
        
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        history.add(0, new TxnRecord(timestamp, request.getAmount(), request.getMerchant(), result.getScore(), result.getDecision()));
        session.setAttribute("history", history);

        return result;
    }

    @GetMapping("/history")
    public List<TxnRecord> getHistory(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<TxnRecord> history = (List<TxnRecord>) session.getAttribute("history");
        return history == null ? new ArrayList<>() : history;
    }

    @PostMapping("/reset")
    public void resetSession(HttpSession session) {
        session.invalidate();
    }
}