package org.example.backend.service;

import org.example.backend.esdao.QuestionEsDAO;
import org.example.backend.model.dto.question.esDTO.QuestionESDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class QuestionEsDaoTest {

    @Resource
    private QuestionEsDAO questionEsDao;

    @Test
    void findByUserId() {
        List<QuestionESDTO> resultList = questionEsDao.findByUserId(1L);
        System.out.println(resultList);
    }
}

