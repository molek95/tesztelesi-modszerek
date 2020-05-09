package worms.gui.game.commands;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import worms.gui.game.PlayGameScreen;
import worms.model.IFacade;

import static org.junit.Assert.assertEquals;

public class CommandTest {
    Command command = null;
    IFacade facade = Mockito.mock(IFacade.class);
    PlayGameScreen playGameScreen = Mockito.mock(PlayGameScreen.class);

    @Before
    public void createScene() {
        command = Mockito.mock(Command.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testGetScreen() {
        Mockito.when(command.getScreen()).thenReturn(playGameScreen);
        assertEquals(playGameScreen, command.getScreen());
    }

    @Test
    public void testGetFacade() {
        Mockito.when(command.getFacade()).thenReturn(facade);
        assertEquals(facade, command.getFacade());
    }

    @Test
    public void testIsTerminatedIfIsExecutionCancelled() {
        Mockito.doCallRealMethod().when(command).isExecutionCancelled();
        boolean expectedResult = command.isExecutionCancelled();
        assertEquals(expectedResult, command.isTerminated());
    }

    @Test
    public void testToStringIfStarted() {
        String simpleName = command.getClass().getSimpleName();
        Mockito.when(command.getElapsedTime()).thenReturn(1234567.0);
        Mockito.when(command.hasBeenStarted()).thenReturn(false);
        assertEquals(simpleName + " (queued)", command.toString());
    }

    @Test
    public void testToStringIfNotStarted() {
        String simpleName = command.getClass().getSimpleName();
        Mockito.when(command.getElapsedTime()).thenReturn(1234567.0);
        Mockito.when(command.hasBeenStarted()).thenReturn(true);
        assertEquals(simpleName + " (elapsed: 1234567.00s)", command.toString());
    }
}
