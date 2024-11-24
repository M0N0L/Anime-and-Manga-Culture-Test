package org.example.backend.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionGenerateRequest implements Serializable{
    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 主题
     */
    private String theme;

    private static final long serialVersionUID = 1L;
}
