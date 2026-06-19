package com.guardian.service;

import com.guardian.common.BusinessException;
import com.guardian.vo.CaptchaVO;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    private static final String CODES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final long EXPIRE_SECONDS = 300;

    private final SecureRandom random = new SecureRandom();
    private final Map<String, CaptchaItem> captchaStore = new ConcurrentHashMap<>();

    public CaptchaVO generate() {
        cleanExpired();
        String code = randomCode();
        String key = UUID.randomUUID().toString();
        captchaStore.put(key, new CaptchaItem(code, Instant.now().plusSeconds(EXPIRE_SECONDS).toEpochMilli()));
        return new CaptchaVO(key, drawImage(code));
    }

    public void validate(String captchaKey, String inputCode) {
        CaptchaItem item = captchaStore.remove(captchaKey);
        if (item == null || item.expiredAt() < System.currentTimeMillis()) {
            throw new BusinessException(400, "验证码已过期，请刷新后重试");
        }
        if (inputCode == null || !item.code().equalsIgnoreCase(inputCode.trim())) {
            throw new BusinessException(400, "验证码错误");
        }
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            builder.append(CODES.charAt(random.nextInt(CODES.length())));
        }
        return builder.toString();
    }

    private String drawImage(String code) {
        try {
            int width = 132;
            int height = 44;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(239, 246, 255));
            graphics.fillRect(0, 0, width, height);
            graphics.setFont(new Font("Arial", Font.BOLD, 26));

            for (int i = 0; i < 18; i++) {
                graphics.setColor(new Color(120 + random.nextInt(90), 130 + random.nextInt(80), 145 + random.nextInt(70)));
                int x1 = random.nextInt(width);
                int y1 = random.nextInt(height);
                graphics.drawLine(x1, y1, random.nextInt(width), random.nextInt(height));
            }

            for (int i = 0; i < code.length(); i++) {
                graphics.setColor(new Color(20 + random.nextInt(80), 80 + random.nextInt(80), 110 + random.nextInt(80)));
                graphics.drawString(String.valueOf(code.charAt(i)), 18 + i * 26, 31 + random.nextInt(5));
            }
            graphics.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception ex) {
            throw new BusinessException("验证码生成失败：" + ex.getMessage());
        }
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        captchaStore.entrySet().removeIf(entry -> entry.getValue().expiredAt() < now);
    }

    private record CaptchaItem(String code, long expiredAt) {
    }
}
