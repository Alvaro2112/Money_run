package sdp.moneyrun;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Riddle {

    //They should be final, but that is incompatible with being able to add them to the DB
    private String question;
    private String correctAnswer;
    private String firstAnswer;
    private String secondAnswer;
    private String thirdAnswer;
    private String fourthAnswer;

    /**
     * @param question        This is the question that the user will see and will have to solve
     * @param correctAnswer   This is the unique correct answer to the question
     */
    public Riddle(String question, String correctAnswer, String firstAnswer, String secondAnswer, String thirdAnswer, String fourthAnswer) {
        if (question == null || correctAnswer == null) {
            throw new IllegalArgumentException("Null arguments in Riddle constructor");
        }
        if(firstAnswer == null || secondAnswer == null || thirdAnswer == null || fourthAnswer == null){
            throw new IllegalArgumentException("Null arguments in Riddle constructor");
        }
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

    // For some reason this method makes the DB
    public String[] getPossibleAnswers() {
        return new String[]{firstAnswer, secondAnswer, thirdAnswer, fourthAnswer};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Riddle riddle = (Riddle) o;
        return question.equals(riddle.question) &&
                correctAnswer.equals(riddle.correctAnswer) &&
                firstAnswer.equals(riddle.firstAnswer) &&
                secondAnswer.equals(riddle.secondAnswer) &&
                thirdAnswer.equals(riddle.thirdAnswer) &&
                fourthAnswer.equals(riddle.fourthAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, correctAnswer, firstAnswer, secondAnswer, thirdAnswer, fourthAnswer);
    }
}

