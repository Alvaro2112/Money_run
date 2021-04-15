package sdp.moneyrun;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class RiddleTest {
    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle(null, "blue", "green", "yellow", "brown", "a");
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle("color of the sky?", null, "green", "yellow", "brown", "a");
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle("color of the sky?", "blue", null, "yellow", "brown", "a");
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle("color of the sky?", "blue", "green", null, "brown", "a");
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle("color of the sky?", "blue", "green", "yellow", null, "a");
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle("color of the sky?", "blue", "green", "yellow", "brown", null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                new Riddle(null, "blue", "green", "yellow", "brown", "a");
            });
    }

    @Test
    public void basicRiddleTest() {
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        String[] possibleAnswers = {"blue", "green", "yellow", "brown"};

        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        assertEquals(question, riddle.getQuestion());
        assertEquals(correctAnswer, riddle.getAnswer());
        assertEquals(possibleAnswers, riddle.getPossibleAnswers());
    }

    @Test
    public void EqualsCompleteTest(){
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        Riddle sameRef = riddle;
        Riddle sameContent = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        Riddle nul = null;
        assertTrue(riddle.equals(sameRef));
        assertTrue(riddle.equals(sameContent));
        assertFalse(riddle.equals(nul));
    }

    @Test
    public void hashWorks(){
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        int hash = Objects.hash(question, correctAnswer, "blue", "green", "yellow", "brown");
        assertEquals(hash, riddle.hashCode());
    }

}
