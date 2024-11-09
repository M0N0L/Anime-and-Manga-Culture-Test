package org.example.backend.model.dto.questionnaireBank;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新问卷库请求
 */
@Data
public class QuestionnaireBankUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;


    private static final long serialVersionUID = 1L;
}