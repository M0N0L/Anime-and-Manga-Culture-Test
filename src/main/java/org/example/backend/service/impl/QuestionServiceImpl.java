package org.example.backend.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.backend.common.ErrorCode;
import org.example.backend.constant.CommonConstant;
import org.example.backend.exception.ThrowUtils;
import org.example.backend.mapper.QuestionMapper;
import org.example.backend.model.dto.question.QuestionQueryRequest;
import org.example.backend.model.entity.Question;
import org.example.backend.model.entity.QuestionBankQuestion;
import org.example.backend.model.entity.User;
import org.example.backend.model.vo.QuestionVO;
import org.example.backend.model.vo.UserVO;
import org.example.backend.service.QuestionBankQuestionService;
import org.example.backend.service.QuestionService;
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
 * 问题服务实现
 */
@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        String title = question.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
            ThrowUtils.throwIf(questionMapper.selectOne(new QueryWrapper<Question>().eq("title", title)) != null, ErrorCode.PARAMS_ERROR, "该题目已经存在");
        }
        // 修改数据时，有参数则校验
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }

        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        List<String> tagList = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();

        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取问题封装
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // region 可选
        // 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);

        return questionVO;
    }

    /**
     * 分页获取问题封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());

        // region 可选
        // 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));

        // 填充信息
        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
        });

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    @Override
    public Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 题目表的查询条件
        QueryWrapper<Question> queryWrapper = this.getQueryWrapper(questionQueryRequest);
        // 根据题库查询题目列表接口
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        if (questionBankId != null) {
            // 查询题库内的题目 id
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .select(QuestionBankQuestion::getQuestionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            List<QuestionBankQuestion> questionList = questionBankQuestionService.list(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(questionList)) {
                // 取出题目 id 集合
                Set<Long> questionIdSet = questionList.stream()
                        .map(QuestionBankQuestion::getQuestionId)
                        .collect(Collectors.toSet());
                // 复用原有题目表的查询条件
                queryWrapper.in("id", questionIdSet);
            }
        }
        // 查询数据库
        Page<Question> questionPage = this.page(new Page<>(current, size), queryWrapper);
        return questionPage;
    }

}
