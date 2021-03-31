package sdp.moneyrun;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class Riddle {

    private final String question;
    private final String correctAnswer;
    private final String firstAnswer;
    private final String secondAnswer;
    private final String thirdAnswer;
    private final String fourthAnswer;



    /**
     * @param question        This is the question that the user will see and will have to solve
     * @param correctAnswer   This is the unique correct answer to the question
     */
    public Riddle(String question, String correctAnswer, String firstAnswer, String secondAnswer, String thirdAnswer, String fourthAnswer) {
        if (question == null || firstAnswer == null || correctAnswer == null || secondAnswer == null)
            throw new IllegalArgumentException("Null arguments in Riddle constructor");

        this.question = question;
        this.correctAnswer = correctAnswer;
        this.firstAnswer = firstAnswer;
        this.secondAnswer= secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return correctAnswer;
    }

    public String[] getPossibleAnswers() {
        return new String[]{firstAnswer, secondAnswer, thirdAnswer, fourthAnswer};
    }

}

