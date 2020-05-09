
package worms.gui.game.commands;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;


import static org.mockito.Mockito.*;

import worms.gui.game.PlayGameScreen;
import worms.gui.game.sprites.WormSprite;
import worms.gui.messages.MessageType;
import worms.model.IFacade;
import worms.model.Worm;
import worms.model.*;

public class MoveTest {
    IFacade facade = mock(IFacade.class);
    PlayGameScreen playGameScreen = mock(PlayGameScreen.class);
    WormSprite wormSprite = mock(WormSprite.class);
    Move moveClass = null;
    Move move = null;
    Worm worm = null;

    @Before
    public void createScene() throws Exception {
        worm = mock(Worm.class);
        moveClass = new Move(facade, worm, playGameScreen);
        move = spy(moveClass);
    }

    @Test
    public void testGetWorm() {
        assertEquals(move.getWorm(), worm);
    }


    @Test
    public void testCanStartIfThereIsNoWorm() {
        doReturn(null).when(move).getWorm();
        assertFalse(move.canStart());
    }

    @Test
    public void testCanStartIfThereIsAWorm() {
        doReturn(worm).when(move).getWorm();
        when(facade.canMove(worm)).thenReturn(false);
        assertFalse(move.canStart());

    }


    @Test
    public void testCanStartIfThereIsAWormAndItCanMove() {
        doReturn(worm).when(move).getWorm();
        when(facade.canMove(worm)).thenReturn(true);
        assertTrue(move.canStart());

    }

    @Test
    public void testCanFallFalse() {
        when(move.getFacade()).thenReturn(facade);
        when(facade.canFall(worm)).thenReturn(false);
        assertFalse(move.canFall());
    }

    @Test
    public void testCanFallTrue() {
        when(move.getFacade()).thenReturn(facade);
        when(facade.canFall(worm)).thenReturn(true);
        assertTrue(move.canFall());
    }

    @Test
    public void testDoUpdateWhenThereIsNoSprite() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(null);
        move.doUpdate(anyDouble());
        verify(move, times(1)).cancelExecution();
    }

    @Test
    public void testUpdateFallingWhenTimeElapsedFallingIsLessThanDuration() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(wormSprite);
        when(playGameScreen.screenToWorldDistance(anyDouble())).thenReturn(300.0);
        doReturn(20.0).when(move).getElapsedTime();
        doReturn(10.0).when(move).getFallingStartTime();
        move.updateFalling();
        verify(wormSprite, times(1)).setCenterLocation(anyDouble(), anyDouble());
    }

    @Test
    public void testUpdateFallingWhenTimeElapsedFallingIsGreaterThanDuration() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(wormSprite);
        when(playGameScreen.screenToWorldDistance(anyDouble())).thenReturn(5.0);
        doReturn(20.0).when(move).getElapsedTime();
        doReturn(10.0).when(move).getFallingStartTime();
        move.updateFalling();
        verify(wormSprite, times(1)).setCenterLocation(anyDouble(), anyDouble());
        verify(move, times(1)).completeExecution();
    }


    @Test
    public void testStartFallingIfItCanFallAndObjectIsAlive() {
        doReturn(true).when(move).canFall();
        doReturn(true).when(move).isObjectStillActive();
        move.startFalling();
        verify(move, times(1)).ensureFalling();
        verify(playGameScreen, times(2)).getScreenX(anyDouble());
    }

    @Test
    public void testStartFallingIfItCanFallAndNoObjectIsAlive() {
        doReturn(true).when(move).canFall();
        doReturn(false).when(move).isObjectStillActive();
        move.startFalling();
        verify(move, times(1)).ensureFalling();
        verify(playGameScreen, times(2)).getScreenY(anyDouble());
    }

    @Test
    public void testStartFallingIfItCanFallAndNoObjectIsAliveWhenExceptionIsThrown() {
        doReturn(true).when(move).canFall();
        doReturn(false).when(move).isObjectStillActive();
        doThrow(new ModelException(""))
                .when(playGameScreen)
                .getScreenY(100);
        move.startFalling();
        verify(move, times(1)).ensureFalling();
        verify(playGameScreen, times(2)).getScreenY(0);
    }

    @Test
    public void testDoStartExecution() {
        doThrow(new ModelException(""))
                .when(playGameScreen)
                .getScreenY(anyDouble());
        move.doStartExecution();
        verify(move, times(1)).cancelExecution();
    }

    @Test
    public void testFallForFalse() {
        when(move.isFalling()).thenReturn(false);
        move.fall(anyDouble());
        verify(move, times(1)).startFalling();
    }

    @Test
    public void testAfterExecutionCompleted() {
        when(playGameScreen.getWormSprite((Worm) any())).thenReturn(wormSprite);
        move.afterExecutionCompleted();
        verify(wormSprite, times(1)).setIsMoving(false);
    }

    @Test
    public void testAfterExecutionCancelled() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(null);
        move.afterExecutionCancelled();
        verify(playGameScreen, times(1)).addMessage("This worm cannot move like that :(", MessageType.ERROR);
    }

    @Test
    public void testAfterExecutionCancelledIfThereIsAWorm() {
        when(playGameScreen.getWormSprite(worm)).thenReturn(wormSprite);
        move.afterExecutionCancelled();
        verify(wormSprite, times(1)).setIsMoving(anyBoolean());
        verify(playGameScreen, times(1)).addMessage("This worm cannot move like that :(", MessageType.ERROR);
    }


    @Test
    public void testEnsureFalling() {
        doReturn(10.0).when(move).getElapsedTime();
        move.ensureFalling();
        assertEquals(10.0, move.getFallingStartTime(), 0.1);
        assertTrue(move.isFalling());
    }


    @Test
    public void testEnsureFallingWhenFallingStartTimeIsNotMinusOne() {
        move.setFallingStartTime(1);
        move.ensureFalling();
        assertTrue(move.isFalling());
    }
}