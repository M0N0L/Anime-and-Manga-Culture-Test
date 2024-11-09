package org.example.backend.model.dto.questionBankQuestion;

import lombok.Data;

/**
 * 删除题目题库关系请求
 */
@Data
public class QuestionBankQuestionRemoveRequest {

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
