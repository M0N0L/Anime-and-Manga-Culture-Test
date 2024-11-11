package org.example.backend.esdao;

import org.example.backend.model.dto.question.esDTO.QuestionESDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface QuestionEsDAO extends ElasticsearchRepository<QuestionESDTO, Long> {
    /**
     * 根据UserId查询数据
     */
    List<QuestionESDTO> findByUserId(Long userId);
}
