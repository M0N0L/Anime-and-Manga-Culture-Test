package org.example.backend.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑关系表请求
 */
@Data
public class QuestionBankQuestionEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}