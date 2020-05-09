package worms.gui.game.commands;

import org.junit.AfterClass;
import org.junit.Test;
import worms.gui.game.PlayGameScreen;
import worms.gui.messages.MessageType;
import worms.model.IFacade;
import worms.model.World;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AddNewTeamTest {
    IFacade facade = mock(IFacade.class);
    PlayGameScreen playGameScreen = mock(PlayGameScreen.class);
    AddNewTeam addNewTeam = new AddNewTeam(facade, "", playGameScreen);
    AddNewTeam addNewTeamSpy = spy(addNewTeam);
    World world = mock(World.class);

    @Test
    public void testCanStart() {
        assertTrue(addNewTeamSpy.canStart());
    }

    @Test
    public void testDoStartExecution() {
        doReturn(world).when(addNewTeamSpy).getWorld();
        addNewTeamSpy.doStartExecution();
        verify(facade, times(1)).addEmptyTeam(world, "");
        verify(playGameScreen, times(1)).addMessage(anyString(), eq(MessageType.NORMAL));
    }
}
