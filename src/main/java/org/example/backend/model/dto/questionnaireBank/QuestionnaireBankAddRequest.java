package org.example.backend.model.dto.questionnaireBank;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建问卷库请求
 */
@Data
public class QuestionnaireBankAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    private static final long serialVersionUID = 1L;
}