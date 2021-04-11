package sdp.moneyrun;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class RiddleTest {
    @Test
    public void RiddleThrowsExceptionWhenArgumentsAreNull() {
        try{
            Riddle riddle = new Riddle(null, "blue", "green", "yellow", "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", null, "green", "yellow", "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", null, "yellow", "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", "green", null, "brown", "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", "green", "yellow", null, "a");
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }

        try{
            Riddle riddle = new Riddle("a", "blue", "green", "yellow", "brown", null);
            fail();
        }catch (IllegalArgumentException e){
            assertTrue(true);
        }
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


}
