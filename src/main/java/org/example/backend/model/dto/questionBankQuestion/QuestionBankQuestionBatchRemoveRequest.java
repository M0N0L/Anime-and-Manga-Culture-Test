package org.example.backend.model.dto.questionBankQuestion;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionBankQuestionBatchRemoveRequest implements Serializable {
    /**
     *
     */
    private List<Long> questionIdList;

    /**
     * 题库id
     */
    private Long questionBankId;

    private static final long serialVersionUID = 1L;

}
