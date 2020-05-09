package worms.gui.game.commands;

import org.junit.Before;
import org.junit.Test;
import worms.gui.game.PlayGameScreen;
import worms.gui.game.sprites.WormSprite;
import worms.gui.messages.MessageType;
import worms.model.IFacade;
import worms.model.ModelException;
import worms.model.Worm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class JumpTest {
    IFacade facade = mock(IFacade.class);
    PlayGameScreen playGameScreen = mock(PlayGameScreen.class);
    Worm worm = mock(Worm.class);
    WormSprite wormSprite = mock(WormSprite.class);
    Jump jump = null;
    Jump jumpSpy = null;

    @Before
    public void createScene() throws Exception {
        jump = new Jump(facade, worm, playGameScreen);
        jumpSpy = spy(jump);
    }

    @Test
    public void testGetWorm() {
        assertEquals(jumpSpy.getWorm(), worm);
    }

    @Test
    public void testCanStartWithoutWorm() {
        doReturn(null).when(jumpSpy).getWorm();
        assertFalse(jumpSpy.canStart());
    }

    @Test
    public void testDoStartExecution() {
        jumpSpy.doStartExecution();
        verify(facade, times(1)).getJumpTime(eq(worm), anyDouble());
    }

    @Test
    public void testDoStartExecutionExceptionThrown() {
        doThrow(new ModelException(""))
                .when(facade)
                .getJumpTime(eq(worm), anyDouble());
        jumpSpy.doStartExecution();
        verify(jumpSpy, times(1)).cancelExecution();
    }

    @Test
    public void testAfterExecutionCancelled() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(null);
        jumpSpy.afterExecutionCancelled();
        verify(playGameScreen, times(1)).addMessage("This worm cannot jump :(", MessageType.ERROR);
    }

    @Test
    public void testAfterExecutionCancelledIfWorm() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(wormSprite);
        jumpSpy.afterExecutionCancelled();
        verify(wormSprite, times(1)).setIsJumping(anyBoolean());
        verify(playGameScreen, times(1)).addMessage("This worm cannot jump :(", MessageType.ERROR);
    }

    @Test
    public void testAfterExecutionCompleted() {
        when(playGameScreen.getWormSprite((Worm) any())).thenReturn(wormSprite);
        jumpSpy.afterExecutionCompleted();
        verify(wormSprite, times(1)).setIsJumping(false);
    }

    @Test
    public void testAfterExecutionCompletedNoWorm() {
        when(playGameScreen.getWormSprite((Worm) any())).thenReturn(null);
        jumpSpy.afterExecutionCompleted();
    }

    @Test
    public void testDoUpdateWhenIfNoSprite() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(null);
        jumpSpy.doUpdate(anyDouble());
        verify(jumpSpy, times(1)).cancelExecution();

    }
}
