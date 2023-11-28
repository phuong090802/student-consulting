package com.ute.studentconsulting.util;

import com.ute.studentconsulting.entity.Question;
import com.ute.studentconsulting.model.QuestionItemModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class QuestionUtils {
    public List<QuestionItemModel> mapQuestionPageToQuestionModels(Page<Question> questionPage) {
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return questionPage.getContent().stream().map(question ->
                new QuestionItemModel(
                        question.getId(), question.getTitle(),
                        simpleDateFormat.format(question.getDate()),
                        question.getField().getName())).toList();
    }
}
