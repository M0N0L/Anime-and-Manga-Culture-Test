package org.example.backend.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionBankQuestionBatchAddRequest implements Serializable {
    /**
     * 题库id
     */
    private Long questionBankId;

    /**
     * 题目列表id
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
