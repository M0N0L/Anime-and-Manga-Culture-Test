package org.example.backend.job.once;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.esdao.QuestionEsDAO;
import org.example.backend.model.dto.question.esDTO.QuestionESDTO;
import org.example.backend.model.entity.Question;
import org.example.backend.service.QuestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionEsDAO questionEsDAO;
    @Override
    public void run(String... args) throws Exception {
        // 全量获取题目
        List<Question> questionList = questionService.list();
        if(CollUtil.isEmpty(questionList)) {
            return;
        }
        // 转为ES
        List<QuestionESDTO> questionEsDTOList = questionList.stream()
                .map(QuestionESDTO::objToDTO)
                .collect(Collectors.toList());

        // 分批插入ES
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("FullSyncQuestionToES start, total {}", total);

        for(int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDAO.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("FullSyncQuestionToEs end, total {}", total);
    }
}
