package org.example.backend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.annotation.AuthCheck;
import org.example.backend.common.BaseResponse;
import org.example.backend.common.DeleteRequest;
import org.example.backend.common.ErrorCode;
import org.example.backend.common.ResultUtils;
import org.example.backend.constant.UserConstant;
import org.example.backend.exception.BusinessException;
import org.example.backend.exception.ThrowUtils;
import org.example.backend.mapper.QuestionnaireBankMapper;
import org.example.backend.model.dto.question.QuestionQueryRequest;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankAddRequest;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankEditRequest;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankQueryRequest;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankUpdateRequest;
import org.example.backend.model.entity.Question;
import org.example.backend.model.entity.QuestionnaireBank;
import org.example.backend.model.entity.User;
import org.example.backend.model.vo.QuestionnaireBankVO;
import org.example.backend.service.QuestionService;
import org.example.backend.service.QuestionnaireBankService;
import org.example.backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 问卷库接口
 */
@RestController
@RequestMapping("/questionnaireBank")
@Slf4j
public class QuestionnaireBankController {

    @Resource
    private QuestionnaireBankService questionnaireBankService;

    @Resource
    private UserService userService;

    @Resource
    QuestionnaireBankMapper questionnaireBankMapper;

    @Resource
    private QuestionService questionService;

    private Page<QuestionnaireBankVO> cachedPage;

    // region 增删改查

    /**
     * 创建问卷库(仅管理员可用）
     *
     * @param questionnaireBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addQuestionnaireBank(@RequestBody QuestionnaireBankAddRequest questionnaireBankAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionnaireBankAddRequest == null, ErrorCode.PARAMS_ERROR);
        QuestionnaireBank questionnaireBank = new QuestionnaireBank();
        BeanUtils.copyProperties(questionnaireBankAddRequest, questionnaireBank);
        // 数据校验
        questionnaireBankService.validQuestionnaireBank(questionnaireBank, true);
        User loginUser = userService.getLoginUser(request);
        questionnaireBank.setUserId(loginUser.getId());
        questionnaireBank.setCreateTime(new Date());
        // 写入数据库
        boolean result = questionnaireBankService.save(questionnaireBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionnaireBankId = questionnaireBank.getId();
        return ResultUtils.success(newQuestionnaireBankId);
    }

    /**
     * 删除问卷库（仅管理员可用）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionnaireBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionnaireBank oldQuestionnaireBank = questionnaireBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionnaireBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestionnaireBank.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionnaireBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新问卷库（仅管理员可用）
     *
     * @param questionnaireBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionnaireBank(@RequestBody QuestionnaireBankUpdateRequest questionnaireBankUpdateRequest) {
        if (questionnaireBankUpdateRequest == null || questionnaireBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionnaireBank questionnaireBank = new QuestionnaireBank();
        BeanUtils.copyProperties(questionnaireBankUpdateRequest, questionnaireBank);
        questionnaireBank.setUpdateTime(new Date());
        // 数据校验
        questionnaireBankService.validQuestionnaireBank(questionnaireBank, false);
        // 判断是否存在
        long id = questionnaireBankUpdateRequest.getId();
        QuestionnaireBank oldQuestionnaireBank = questionnaireBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionnaireBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionnaireBankService.updateById(questionnaireBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @GetMapping("/get/vo")
    public BaseResponse<QuestionnaireBankVO> getQuestionBankVOById(QuestionnaireBankQueryRequest questionBankQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = questionBankQueryRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionnaireBank questionBank = questionnaireBankService.getById(id);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 查询题库封装类
        QuestionnaireBankVO questionBankVO = questionnaireBankService.getQuestionnaireBankVO(questionBank, request);
        // 是否要关联查询题库下的题目列表
        boolean needQueryQuestionList = questionBankQueryRequest.isNeedQueryQuestionList();
        if (needQueryQuestionList) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setQuestionBankId(id);
            Page<Question> questionPage = questionService.listQuestionByPage(questionQueryRequest);
            questionBankVO.setQuestionPage(questionPage);
        }
        // 获取封装类
        return ResultUtils.success(questionBankVO);
    }


    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionnaireBank>> listQuestionnaireBankByPage(@RequestBody QuestionnaireBankQueryRequest questionnaireBankQueryRequest) {
        long current = questionnaireBankQueryRequest.getCurrent();
        long size = questionnaireBankQueryRequest.getPageSize();
        System.out.println(questionnaireBankQueryRequest);


        Page<QuestionnaireBank> questionnaireBankPage = questionnaireBankService.page(new Page<>(current, size),
                questionnaireBankService.getQueryWrapper(questionnaireBankQueryRequest));
        return ResultUtils.success(questionnaireBankPage);
    }

    /**
     * 分页获取问卷库列表（封装类）
     *
     * @param questionnaireBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @SentinelResource(value = "listQuestionnaireBankVOByPage",
            blockHandler = "handleBlockException",
            fallback = "handleFallback")
    public BaseResponse<Page<QuestionnaireBankVO>> listQuestionnaireBankVOByPage(@RequestBody QuestionnaireBankQueryRequest questionnaireBankQueryRequest,
                                                                                 HttpServletRequest request) {
        long current = questionnaireBankQueryRequest.getCurrent();
        long size = questionnaireBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 30, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionnaireBank> questionnaireBankPage = questionnaireBankService.page(new Page<>(current, size),
                questionnaireBankService.getQueryWrapper(questionnaireBankQueryRequest));
        // 缓存页面
        cachedPage = questionnaireBankService.getQuestionnaireBankVOPage(questionnaireBankPage, request);
        // 获取封装类
        return ResultUtils.success(questionnaireBankService.getQuestionnaireBankVOPage(questionnaireBankPage, request));
    }

    /**
     * 分页获取当前登录用户创建的问卷库列表
     *
     * @param questionnaireBankQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionnaireBankVO>> listMyQuestionnaireBankVOByPage(@RequestBody QuestionnaireBankQueryRequest questionnaireBankQueryRequest,
                                                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(questionnaireBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionnaireBankQueryRequest.setUserId(loginUser.getId());
        long current = questionnaireBankQueryRequest.getCurrent();
        long size = questionnaireBankQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<QuestionnaireBank> questionnaireBankPage = questionnaireBankService.page(new Page<>(current, size),
                questionnaireBankService.getQueryWrapper(questionnaireBankQueryRequest));
        // 获取封装类
        return ResultUtils.success(questionnaireBankService.getQuestionnaireBankVOPage(questionnaireBankPage, request));
    }

    /**
     * 编辑问卷库（给用户使用）
     *
     * @param questionnaireBankEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionnaireBank(@RequestBody QuestionnaireBankEditRequest questionnaireBankEditRequest, HttpServletRequest request) {
        if (questionnaireBankEditRequest == null || questionnaireBankEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionnaireBank questionnaireBank = new QuestionnaireBank();
        BeanUtils.copyProperties(questionnaireBankEditRequest, questionnaireBank);
        questionnaireBank.setEditTime(new Date());
        // 数据校验
        questionnaireBankService.validQuestionnaireBank(questionnaireBank, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = questionnaireBankEditRequest.getId();
        QuestionnaireBank oldQuestionnaireBank = questionnaireBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionnaireBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestionnaireBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = questionnaireBankService.updateById(questionnaireBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * listQuestionBankVOByPage 降级操作：直接返回本地数据
     */
    public BaseResponse<Page<QuestionnaireBankVO>> handleFallback(@RequestBody QuestionnaireBankQueryRequest questionBankQueryRequest,
                                                             HttpServletRequest request, Throwable ex) {
        System.out.println("触发降级操作");
        // 可以返回本地数据或空数据
        return ResultUtils.success(cachedPage);
    }

    /**
     * listQuestionBankVOByPage 流控操作
     * 限流：提示“系统压力过大，请耐心等待”
     */
    public BaseResponse<Page<QuestionnaireBankVO>> handleBlockException(@RequestBody QuestionnaireBankQueryRequest questionBankQueryRequest,
                                                                   HttpServletRequest request, BlockException ex) {
        // 降级操作
        if (ex instanceof DegradeException) {
            return handleFallback(questionBankQueryRequest, request, ex);
        }
        // 限流操作
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统压力过大，请耐心等待");
    }
}
