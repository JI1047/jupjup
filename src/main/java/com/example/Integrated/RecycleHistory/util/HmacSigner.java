package com.example.Integrated.RecycleHistory.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class HmacSigner {

    @Value("${security.hmac-secret}")
    private String secretKey;

    private SecretKeySpec secretKeySpec;

    @PostConstruct
    void init() {
        // 비밀키를 기반으로 HMAC 서명기 초기화
        this.secretKeySpec = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        log.info("✅ HmacSigner initialized successfully");
    }

    /**
     * 메시지를 받아 HMAC-SHA256 서명 생성
     */
    public String sign(String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC sign error", e);
        }
    }

    /**
     * 검증용 (서명 일치 확인)
     */
    public boolean verify(String message, String signature) {
        String expected = sign(message);
        return expected.equals(signature);
    }
}
