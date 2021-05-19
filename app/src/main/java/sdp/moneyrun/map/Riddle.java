package sdp.moneyrun.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;


public class Riddle {

    @Nullable
    private final String question;
    @Nullable
    private final String correctAnswer;
    @Nullable
    private final String firstAnswer;
    @Nullable
    private final String secondAnswer;
    @Nullable
    private final String thirdAnswer;
    @Nullable
    private final String fourthAnswer;

    /**
     *
     * @param question The riddle questio
     * @param correctAnswer The correct answer to the riddle
     * @param firstAnswer First possible answer
     * @param secondAnswer Second possible answer
     * @param thirdAnswer Third possible answer
     * @param fourthAnswer Fourth possible answer
     */
    public Riddle(@Nullable String question, @Nullable String correctAnswer, @Nullable String firstAnswer, @Nullable String secondAnswer, @Nullable String thirdAnswer, @Nullable String fourthAnswer) {
        if (question == null || correctAnswer == null) {
            throw new IllegalArgumentException("Null arguments in Riddle constructor");
        }
        if (firstAnswer == null || secondAnswer == null || thirdAnswer == null || fourthAnswer == null) {
            throw new IllegalArgumentException("Null arguments in Riddle constructor");
        }
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.firstAnswer = firstAnswer;
        this.secondAnswer = secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
    }

    @Nullable
    public String getQuestion() {
        return question;
    }

    @Nullable
    public String getAnswer() {
        return correctAnswer;
    }

    // For some reason this method makes the DB
    @NonNull
    public String[] getPossibleAnswers() {
        return new String[]{firstAnswer, secondAnswer, thirdAnswer, fourthAnswer};
    }

    @Override
    public boolean equals(@Nullable Object o) {
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

