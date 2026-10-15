package com.achhecode.browser_pilot.website.instahyre.service;

import com.achhecode.browser_pilot.website.instahyre.InstahyrePageFactory;
import com.achhecode.browser_pilot.website.instahyre.InstahyrePageResolver;
import com.achhecode.browser_pilot.website.instahyre.page.QuestionnairePage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstahyreQuestionnaireService {

    private final InstahyrePageResolver pageResolver;
    private final InstahyrePageFactory pageFactory;
    private final QuestionnaireAnswerProvider answerProvider;

    public void completeQuestionnaire() {
        QuestionnairePage questionnaire =
                pageFactory.questionnaire(pageResolver.findPage());

        if (!questionnaire.isQuestionnairePage()) {
            throw new IllegalStateException(
                    "The current Instahyre page is not a questionnaire."
            );
        }

        boolean allAnswered = true;
        int questionCount = questionnaire.getQuestionCount();

        log.info("Total question: {}", questionCount);

        for (int i = 0; i < questionCount; i++) {
            String question = questionnaire.getQuestionText(i);

            log.info("Current question is {}", question);

            log.info("Question {}: {}", i + 1, question);

            if (answerProvider.isResumeQuestion(question)) {
                questionnaire.uploadResume(
                        i,
                        answerProvider.getResumePath()
                );

                log.info("Resume uploaded for question {}", i + 1);
                continue;
            }

            Map<String, String> answers =
                    answerProvider.resolveAnswers(question);

            if (answers.isEmpty()) {
                log.warn(
                        "No configured answers for question {}: {}",
                        i + 1,
                        question
                );

                allAnswered = false;
                continue;
            }

            questionnaire.debugQuestion(i);
            questionnaire.fillAnswers(i, answers);

            log.info("Answers filled for question {}", i + 1);
        }

        if (!allAnswered) {
            log.warn(
                    "Questionnaire submission skipped because some questions remain unanswered."
            );
            return;
        }

        questionnaire.submit();

        log.info("Questionnaire submitted.");

        if(questionnaire.verifySubmissionSuccess()){
            log.info("Questionnaire has been sent!");
        }
    }
}