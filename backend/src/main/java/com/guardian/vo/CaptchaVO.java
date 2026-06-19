package com.guardian.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaVO {

    private String captchaKey;
    private String imageBase64;
}
