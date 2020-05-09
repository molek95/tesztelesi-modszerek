package worms.gui.game.commands;

import org.junit.Test;
import org.junit.runner.notification.Failure;
import worms.gui.WormsGUI;
import worms.gui.game.IActionHandler;
import worms.gui.game.PlayGameScreen;
import worms.model.IFacade;
import worms.model.Program;
import worms.model.World;
import worms.model.programs.ParseOutcome;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AddNewWormTest {
    IFacade facade = mock(IFacade.class);
    PlayGameScreen playGameScreen = mock(PlayGameScreen.class);
    World world = mock(World.class);
    AddNewWorm addWorm = null;
    AddNewWorm addWormSpy = null;

    @Test
    public void testCanStart() {
        addWorm = new AddNewWorm(facade, true, playGameScreen);
        addWormSpy = spy(addWorm);
        assertTrue(addWormSpy.canStart());
    }

    @Test
    public void testDoStartExecutionProgramTextTrueAndReturnedStringIsNull() {
        addWorm = new AddNewWorm(facade, true, playGameScreen);
        addWormSpy = spy(addWorm);
        doReturn(null).when(addWormSpy).readProgramText();
        addWormSpy.doStartExecution();
        verify(addWormSpy, times(1)).cancelExecution();
    }

    @Test
    public void testDoStartExecutionProgramTextFalse() {
        addWorm = new AddNewWorm(facade, false, playGameScreen);
        addWormSpy = spy(addWorm);
        doReturn(facade).when(addWormSpy).getFacade();
        doReturn(world).when(addWormSpy).getWorld();
        doReturn(null).when(addWormSpy).readProgramText();
        addWormSpy.doStartExecution();
        verify(facade, times(1)).addNewWorm(world, null);
    }

    @Test
    public final void testDoStartExecutionProgramTextTrueWithSomeStringParsedIsNotNullSuccessIsTrue() {
        addWorm= new AddNewWorm(facade, true, playGameScreen);
        ParseOutcome.Success parsedOutcome = mock(ParseOutcome.Success.class);
        Program program = mock(Program.class);
        WormsGUI mockGUI = mock(WormsGUI.class);
        PlayGameScreen mockPlayGameScreen = mock(PlayGameScreen.class);

        addWormSpy = spy(addWorm);
        doReturn("").when(addWormSpy).readProgramText();
        doReturn(facade).when(addWormSpy).getFacade();
        doReturn(parsedOutcome).when(facade).parseProgram(anyString(), any(IActionHandler.class));
        doReturn(true).when(parsedOutcome).isSuccess();
        doReturn(program).when(parsedOutcome).getResult();
        doNothing().when(addWormSpy).cancelExecution();
        doReturn(mockPlayGameScreen).when(addWormSpy).getScreen();
        doReturn(mockGUI).when(mockPlayGameScreen).getGUI();
        doNothing().when(mockGUI).showError(anyString());
        doReturn(false).when(facade).isWellFormed(program);
        addWormSpy.doStartExecution();
        verify(addWormSpy, times(1)).cancelExecution();
    }

    @Test
    public final void testDoStartExecutionProgramTextTrueWithSomeStringParsedIsNull() {
        addWorm= new AddNewWorm(facade, true, playGameScreen);
        ParseOutcome.Success parsedOutcome = mock(ParseOutcome.Success.class);
        addWormSpy = spy(addWorm);
        doReturn("").when(addWormSpy).readProgramText();
        doReturn(facade).when(addWormSpy).getFacade();
        doReturn(world).when(addWormSpy).getWorld();
        doReturn(null).when(facade).parseProgram(anyString(), any(IActionHandler.class));
        addWormSpy.doStartExecution();
    }
}
