package com.lambdaschool.starthere.services;

import com.lambdaschool.starthere.exceptions.ResourceNotFoundException;
import com.lambdaschool.starthere.models.Question;
import com.lambdaschool.starthere.models.SmsRequest;
import com.lambdaschool.starthere.repository.QuestionRepository;
import com.lambdaschool.starthere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service(value = "questionService")
public class QuestionServiceImpl implements QuestionService
{
    @Autowired
    private SmsSender smsSender;

    @Value("${twilio.trial-number.path}")
    private String trialNumber;


    @Autowired
    private UserRepository userrepos;


    @Autowired
    private QuestionRepository questionrepos;

    @Override
    public List<Question> findAll()
    {
        List<Question> list = new ArrayList<>();
        questionrepos.findAll().iterator().forEachRemaining(list::add);
        return list;
    }

    @Override
    public Question findQuestionById(long id)
    {
        return questionrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Long.toString(id)));
    }

    @Override
    public void delete(long id)
    {
        if (questionrepos.findById(id).isPresent())
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (questionrepos.findById(id).get().getUser().getUsername().equalsIgnoreCase(authentication.getName()))
            {
                questionrepos.deleteById(id);
            }
            else
            {
                throw new ResourceNotFoundException(Long.toString(id) + " " + authentication.getName());
            }
        }
        else
        {
            throw new ResourceNotFoundException(Long.toString(id));
        }
    }

    @Transactional
    @Override
    public Question save(Question question, Authentication authentication)
    {
        question.setUser(userrepos.findByUsername(authentication.getName()));
        Question saveQuestion =  questionrepos.save(question);
        smsSender.sendSms(new SmsRequest("919-438-9115", "MentorMe has sent you a new question " + saveQuestion.getQuestion()));
        return saveQuestion;
    }

    @Override
    public List<Question> findByUserName(String username)
    {
        List<Question> list = new ArrayList<>();
        questionrepos.findAll().iterator().forEachRemaining(list::add);

        list.removeIf(q -> !q.getUser().getUsername().equalsIgnoreCase(username));
        return list;
    }
}
