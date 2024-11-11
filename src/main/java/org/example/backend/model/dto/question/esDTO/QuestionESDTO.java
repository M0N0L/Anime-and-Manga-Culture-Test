package org.example.backend.model.dto.question.esDTO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.example.backend.model.entity.Question;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * 用于Elastic Search中查询信息
 */

@Document(indexName = "question")
@Data
public class QuestionESDTO {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

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
    private String content;

    /**
     * 答案
     */
    private String answer;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    /**
     * 对象转包装类
     */
    public static QuestionESDTO objToDTO(Question question) {
        if(question == null) {
            return null;
        }
        QuestionESDTO questionESDTO = new QuestionESDTO();
        BeanUtils.copyProperties(question, questionESDTO);
        String tagStr = question.getTags();
        if(StringUtils.isNotBlank(tagStr)) {
            questionESDTO.setTags(JSONUtil.toList(tagStr, String.class));
        }
        return questionESDTO;
    }

    /**
     * 包装类转对象
     */
    public static Question dtoToObj(QuestionESDTO questionESDTO) {
        if(questionESDTO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionESDTO, question);
        List<String> tagList = questionESDTO.getTags();
        if(CollUtil.isNotEmpty(tagList)) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        return question;
    }
}
