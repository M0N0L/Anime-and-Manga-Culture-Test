package org.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.example.backend.model.entity.Question;

import java.util.Date;
import java.util.List;

/**
* @author sjtuj
* @description 针对表【question(题目信息)】的数据库操作Mapper
* @createDate 2024-11-07 15:49:18
* @Entity org.example.backend.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询题目列表
     */
    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);
}




