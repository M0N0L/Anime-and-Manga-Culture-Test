package org.example.backend.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.backend.mapper.QuestionnaireBankMapper;
import org.example.backend.model.dto.questionnaireBank.QuestionnaireBankQueryRequest;
import org.example.backend.model.entity.QuestionnaireBank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class QuestionnaireBankServiceTest {
    @Resource
    QuestionnaireBankService service;


    @Resource
    QuestionnaireBankMapper mapper;

    @Resource
    QuestionnaireBankService questionnaireBankService;
    @Test
    public void test() {
        QuestionnaireBankQueryRequest request = new QuestionnaireBankQueryRequest();
        request.setUserId(1L);
        QueryWrapper<QuestionnaireBank> queryWrapper = service.getQueryWrapper(request);

        QueryWrapper<QuestionnaireBank> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userId", 1L);

        Page<QuestionnaireBank> questionnaireBankPage = questionnaireBankService.page(new Page<>(0, 1),
                queryWrapper);
        Page<QuestionnaireBank> questionnaireBankPage1 = questionnaireBankService.page(new Page<>(0, 1),
                queryWrapper1);
        System.out.println(queryWrapper);
    }

    @Test
    public void test2() {
        List<String> arrList = new ArrayList<>();
        arrList.add("Java");
        arrList.add("C++");
        String jsonStr = JSONUtil.toJsonStr(arrList);
        List<String> jsonList = JSONUtil.toList(jsonStr, String.class);
        String jsonStr2 = JSONUtil.toJsonStr(jsonList);
        System.out.println(jsonStr);
        System.out.println(jsonList);
        System.out.println(jsonStr2);
    }
}
