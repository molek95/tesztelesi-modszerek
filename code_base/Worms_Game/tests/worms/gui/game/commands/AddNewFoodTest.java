package worms.gui.game.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import worms.gui.game.PlayGameScreen;
import worms.model.IFacade;

public class AddNewFoodTest {
    IFacade facade = mock(IFacade.class);
    PlayGameScreen screen = mock(PlayGameScreen.class);
    AddNewFood food = new AddNewFood(facade, screen);

    @Test
    public void testCanStart() {
        assertEquals(true, food.canStart());
    }

    @Test
    public void testDoStartExecution() {
        food.doStartExecution();
        verify(facade, times(1)).addNewFood(null);
    }
}
