package com.guardian.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("ai_review_record")
@EqualsAndHashCode(callSuper = true)
public class AiReviewRecord extends BaseEntity {

    private Long reviewerId;
    private String reviewScene;
    private String dataType;
    private String publishScope;
    private String contentType;
    private String fileName;
    private String sourceContent;
    private String reviewResult;
}
