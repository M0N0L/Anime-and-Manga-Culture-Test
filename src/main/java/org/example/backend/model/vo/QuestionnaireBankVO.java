package org.example.backend.model.vo;

import lombok.Data;
import org.example.backend.model.entity.Question;
import org.example.backend.model.entity.QuestionnaireBank;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;
import java.util.Date;

/**
 * 问卷库视图
 */
@Data
public class QuestionnaireBankVO implements Serializable {


    /**
     * id
     */
    private Long id;

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


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 用户信息
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 题库里的题目列表（分页）
     */
    Page<Question> questionPage;

    /**
     * 封装类转对象
     *
     * @param questionnaireBankVO
     * @return
     */
    public static QuestionnaireBank voToObj(QuestionnaireBankVO questionnaireBankVO) {
        if (questionnaireBankVO == null) {
            return null;
        }
        QuestionnaireBank questionnaireBank = new QuestionnaireBank();
        BeanUtils.copyProperties(questionnaireBankVO, questionnaireBank);
        return questionnaireBank;
    }

    /**
     * 对象转封装类
     *
     * @param questionnaireBank
     * @return
     */
    public static QuestionnaireBankVO objToVo(QuestionnaireBank questionnaireBank) {
        if (questionnaireBank == null) {
            return null;
        }
        QuestionnaireBankVO questionnaireBankVO = new QuestionnaireBankVO();
        BeanUtils.copyProperties(questionnaireBank, questionnaireBankVO);
        return questionnaireBankVO;
    }
}
