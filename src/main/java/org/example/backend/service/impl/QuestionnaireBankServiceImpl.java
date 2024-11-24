package org.example.backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.backend.common.ErrorCode;
import org.example.backend.constant.CommonConstant;
import org.example.backend.exception.ThrowUtils;
import org.example.backend.mapper.QuestionnaireBankMapper;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankQueryRequest;
import org.example.backend.model.entity.QuestionnaireBank;
import org.example.backend.model.entity.User;
import org.example.backend.model.vo.QuestionnaireBankVO;
import org.example.backend.model.vo.UserVO;
import org.example.backend.service.QuestionBankQuestionService;
import org.example.backend.service.QuestionnaireBankService;
import org.example.backend.service.UserService;
import org.example.backend.utils.SqlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 问卷库服务实现
 */
@DS("question")
@Service
@Slf4j
public class QuestionnaireBankServiceImpl extends ServiceImpl<QuestionnaireBankMapper, QuestionnaireBank> implements QuestionnaireBankService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionnaireBankMapper questionnaireBankMapper;


    /**
     * 校验数据
     *
     * @param questionnaireBank
     * @param add               对创建的数据进行校验
     */
    @Override
    public void validQuestionnaireBank(QuestionnaireBank questionnaireBank, boolean add) {
        ThrowUtils.throwIf(questionnaireBank == null, ErrorCode.PARAMS_ERROR);
        // 校验标题
        String title = questionnaireBank.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(questionnaireBankMapper.selectOne(new QueryWrapper<QuestionnaireBank>().eq("title", title)) != null, ErrorCode.PARAMS_ERROR, "该问卷库已经存在");

        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionnaireBankQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionnaireBank> getQueryWrapper(QuestionnaireBankQueryRequest questionnaireBankQueryRequest) {
        QueryWrapper<QuestionnaireBank> queryWrapper = new QueryWrapper<>();
        if (questionnaireBankQueryRequest == null) {
            return queryWrapper;
        }

        Long id = questionnaireBankQueryRequest.getId();
        Long notId = questionnaireBankQueryRequest.getNotId();
        String title = questionnaireBankQueryRequest.getTitle();
        String description = questionnaireBankQueryRequest.getDescription();
        String searchText = questionnaireBankQueryRequest.getSearchText();
        String sortField = questionnaireBankQueryRequest.getSortField();
        String sortOrder = questionnaireBankQueryRequest.getSortOrder();
        Long userId = questionnaireBankQueryRequest.getUserId();

        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);

        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);

        // 排序规则
        if (StringUtils.isNotBlank(sortField)) {
            queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                    sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                    sortField);
        }
        return queryWrapper;
    }

    /**
     * 获取问卷库封装
     *
     * @param questionnaireBank
     * @param request
     * @return
     */
    @Override
    public QuestionnaireBankVO getQuestionnaireBankVO(QuestionnaireBank questionnaireBank, HttpServletRequest request) {
        // 对象转封装类
        QuestionnaireBankVO questionnaireBankVO = QuestionnaireBankVO.objToVo(questionnaireBank);

        // 将用户信息填入问卷表视图
        Long userId = questionnaireBank.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionnaireBankVO.setUser(userVO);

        return questionnaireBankVO;
    }

    /**
     * 分页获取问卷库封装
     *
     * @param questionnaireBankPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionnaireBankVO> getQuestionnaireBankVOPage(Page<QuestionnaireBank> questionnaireBankPage, HttpServletRequest request) {
        List<QuestionnaireBank> questionnaireBankList = questionnaireBankPage.getRecords();
        Page<QuestionnaireBankVO> questionnaireBankVOPage = new Page<>(questionnaireBankPage.getCurrent(), questionnaireBankPage.getSize(), questionnaireBankPage.getTotal());
        if (CollUtil.isEmpty(questionnaireBankList)) {
            return questionnaireBankVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionnaireBankVO> questionnaireBankVOList = questionnaireBankList.stream().map(questionnaireBank -> QuestionnaireBankVO.objToVo(questionnaireBank)).collect(Collectors.toList());


        Set<Long> userIdSet = questionnaireBankList.stream().map(QuestionnaireBank::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        questionnaireBankVOList.forEach(questionnaireBankVO -> {
            Long userId = questionnaireBankVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionnaireBankVO.setUser(userService.getUserVO(user));
        });

        questionnaireBankVOPage.setRecords(questionnaireBankVOList);
        return questionnaireBankVOPage;
    }


}
