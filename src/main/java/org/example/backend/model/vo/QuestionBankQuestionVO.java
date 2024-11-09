package org.example.backend.model.vo;

import lombok.Data;
import org.example.backend.model.entity.QuestionBankQuestion;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

@Data
public class QuestionBankQuestionVO implements Serializable {
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

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private UserVO userVO;

    private QuestionVO questionVO;

    private QuestionnaireBankVO questionnaireBankVO;

    /**
     * 封装类转对象
     *
     * @param questionnaireBankVO
     * @return
     */
    public static QuestionBankQuestion voToObj(QuestionBankQuestionVO questionnaireBankVO) {
        if (questionnaireBankVO == null) {
            return null;
        }
        QuestionBankQuestion questionnaireBank = new QuestionBankQuestion();
        BeanUtils.copyProperties(questionnaireBankVO, questionnaireBank);
        return questionnaireBank;
    }

    /**
     * 对象转封装类
     *
     * @param questionnaireBank
     * @return
     */
    public static QuestionBankQuestionVO objToVo(QuestionBankQuestion questionnaireBank) {
        if (questionnaireBank == null) {
            return null;
        }
        QuestionBankQuestionVO questionnaireBankVO = new QuestionBankQuestionVO();
        BeanUtils.copyProperties(questionnaireBank, questionnaireBankVO);
        return questionnaireBankVO;
    }
}
