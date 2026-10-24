package com.achhecode.instahyreservice.page;

import com.achhecode.instahyreservice.InstahyreUrls;
import com.achhecode.instahyreservice.handler.WebsitePage;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.Map;

@Slf4j
public class QuestionnairePage extends WebsitePage {

        private static final String QUESTIONS_SELECTOR = "div.questions-container div.questionnaire-questions > div[ng-repeat]";

        private static final String QUESTION_TEXT_SELECTOR = ".question-label span";

        // private static final String ANSWER_INPUT_SELECTOR =
        // ".answer-container input[type='text']";

        private static final String ANSWER_INPUT_SELECTOR = ".answer-container input:not([type='hidden']), "
                        + ".answer-container textarea, " + ".answer-container select";

        private static final String RESUME_UPLOAD_BUTTON_SELECTOR = ".answer-container .add-file button";

        private static final String SUBMIT_BUTTON_SELECTOR = "button[ng-click='submitQuestionnaire()']";

        public QuestionnairePage(Page page) {
                super(page);
        }

        public boolean isQuestionnairePage() {
                return !page.isClosed() && page.url().startsWith(InstahyreUrls.QUESTIONNAIRE);
        }

        public int getQuestionCount() {
                ensureQuestionnairePage();
                return page.locator(QUESTIONS_SELECTOR).count();
        }

        public String getQuestionText(int index) {
                Locator questionText = getQuestion(index).locator(QUESTION_TEXT_SELECTOR);

                if (questionText.count() == 0) {
                        return "";
                }

                return questionText.innerText().trim();
        }

        public void fillAnswer(int index, String answer) {
                Locator inputs = getAnswerInputs(index);
                int inputCount = inputs.count();

                if (inputCount != 1) {
                        throw new IllegalStateException("Expected one answer input for question " + (index + 1)
                                        + ", found " + inputCount);
                }

                fillAndVerify(inputs.first(), answer, index);
        }

        public void fillAnswers(int index, Map<String, String> answers) {
                if (answers == null || answers.isEmpty()) {
                        throw new IllegalArgumentException("Answers cannot be empty for question " + (index + 1));
                }

                Locator inputs = getAnswerInputs(index);
                int inputCount = inputs.count();

                if (inputCount != 1) {
                        throw new IllegalStateException("Expected one answer input for question " + (index + 1)
                                        + ", found " + inputCount);
                }

                String answer = String.join(" : ", answers.values());

                log.info("Filling question {} with answer: {}", index + 1, answer);

                fillAndVerify(inputs.first(), answer, index);
        }

        public void uploadResume(int index, String resumePath) {
                ensureQuestionnairePage();

                Path path = Path.of(resumePath).toAbsolutePath().normalize();

                if (!path.toFile().isFile()) {
                        throw new IllegalArgumentException("Resume file does not exist: " + path);
                }

                Locator uploadButton = getQuestion(index).locator(RESUME_UPLOAD_BUTTON_SELECTOR);

                if (uploadButton.count() == 0) {
                        throw new IllegalStateException("Resume upload button not found for question " + (index + 1));
                }

                FileChooser fileChooser = page.waitForFileChooser(uploadButton::click);

                fileChooser.setFiles(path);
        }

        public void submit() {
                ensureQuestionnairePage();

                Locator submitButton = page.locator(SUBMIT_BUTTON_SELECTOR);

                submitButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

                if (!submitButton.isEnabled()) {
                        throw new IllegalStateException("Questionnaire submit button is disabled.");
                }

                submitButton.click();
        }

        public void close() {
                if (!page.isClosed()) {
                        page.close();
                }
        }

        private Locator getAnswerInputs(int questionIndex) {
                return getQuestion(questionIndex).locator(ANSWER_INPUT_SELECTOR);
        }

        private void fillAndVerify(Locator input, String answer, int questionIndex) {
                input.fill(answer);

                if (!answer.equals(input.inputValue())) {
                        throw new IllegalStateException(
                                        "Answer verification failed for question " + (questionIndex + 1));
                }
        }

        private Locator getQuestion(int index) {
                ensureQuestionnairePage();

                Locator questions = page.locator(QUESTIONS_SELECTOR);
                int questionCount = questions.count();

                if (index < 0 || index >= questionCount) {
                        throw new IllegalArgumentException("Question index " + index
                                        + " is out of range. Question count: " + questionCount);
                }

                return questions.nth(index);
        }

        private void ensureQuestionnairePage() {
                if (!isQuestionnairePage()) {
                        throw new IllegalStateException("Current tab is not an Instahyre questionnaire page.");
                }
        }

        public boolean verifySubmissionSuccess() {
                ensureQuestionnairePage();

                Locator successMessage = page.locator("div.questionnaire-sent");

                successMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

                Locator heading = successMessage.locator("h4");

                return heading.isVisible() && "Questionnaire has been sent".equals(heading.innerText().trim());
        }

        public void debugQuestion(int index) {
                Locator question = getQuestion(index);

                log.info("Question text: {}", question.locator(QUESTION_TEXT_SELECTOR).innerText());

                log.info("Question HTML:\n{}", question.evaluate("el => el.outerHTML"));

                log.info("All inputs: {}", question.locator("input").count());

                log.info("Text inputs: {}", question.locator("input[type='text']").count());

                log.info("Textareas: {}", question.locator("textarea").count());

                log.info("Selects: {}", question.locator("select").count());

                log.info("Buttons: {}", question.locator("button").count());
        }

        public boolean isDisplayed() {
                return page.url().contains(InstahyreUrls.QUESTIONNAIRE);
        }

}