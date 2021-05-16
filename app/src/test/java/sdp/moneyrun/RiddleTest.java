package sdp.moneyrun;

import org.junit.Test;

import java.util.Objects;

import sdp.moneyrun.map.Riddle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;


public class RiddleTest {
    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> new Riddle(null, "blue", "green", "yellow", "brown", "a"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("color of the sky?", null, "green", "yellow", "brown", "a"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("color of the sky?", "blue", null, "yellow", "brown", "a"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("color of the sky?", "blue", "green", null, "brown", "a"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("color of the sky?", "blue", "green", "yellow", null, "a"));

        assertThrows(IllegalArgumentException.class, () -> new Riddle("color of the sky?", "blue", "green", "yellow", "brown", null));

        assertThrows(IllegalArgumentException.class, () -> new Riddle(null, "blue", "green", "yellow", "brown", "a"));
    }

    @Test
    public void basicRiddleTest() {
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        String[] possibleAnswers = {"blue", "green", "yellow", "brown"};

        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        assertEquals(question, riddle.getQuestion());
        assertEquals(correctAnswer, riddle.getAnswer());
        assertArrayEquals(possibleAnswers, riddle.getPossibleAnswers());
    }

    @Test
    public void EqualsCompleteTest() {
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        Riddle sameContent = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        Riddle nul = null;
        assertEquals(riddle, riddle);
        assertEquals(riddle, sameContent);
        assertNotEquals(riddle, nul);
    }

    @Test
    public void hashWorks() {
        String question = "What is the color of the sky";
        String correctAnswer = "blue";
        Riddle riddle = new Riddle(question, correctAnswer, "blue", "green", "yellow", "brown");
        int hash = Objects.hash(question, correctAnswer, "blue", "green", "yellow", "brown");
        assertEquals(hash, riddle.hashCode());
    }

}
