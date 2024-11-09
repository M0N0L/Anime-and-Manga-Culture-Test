package org.example.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankQueryRequest;
import org.example.backend.model.entity.QuestionnaireBank;
import org.example.backend.model.vo.QuestionnaireBankVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 问卷库服务
 *
 */
public interface QuestionnaireBankService extends IService<QuestionnaireBank> {

    /**
     * 校验数据
     *
     * @param questionnaireBank
     * @param add 对创建的数据进行校验
     */
    void validQuestionnaireBank(QuestionnaireBank questionnaireBank, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionnaireBankQueryRequest
     * @return
     */
    QueryWrapper<QuestionnaireBank> getQueryWrapper(QuestionnaireBankQueryRequest questionnaireBankQueryRequest);

    /**
     * 获取问卷库封装
     *
     * @param questionnaireBank
     * @param request
     * @return
     */
    QuestionnaireBankVO getQuestionnaireBankVO(QuestionnaireBank questionnaireBank, HttpServletRequest request);

    /**
     * 分页获取问卷库封装
     *
     * @param questionnaireBankPage
     * @param request
     * @return
     */
    Page<QuestionnaireBankVO> getQuestionnaireBankVOPage(Page<QuestionnaireBank> questionnaireBankPage, HttpServletRequest request);



}
