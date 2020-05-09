package worms.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import worms.model.exceptions.IllegalNameException;

@RunWith(Parameterized.class)
public class PartialFacadeAddEmptyTeamExceptionTest {

    @Parameters(name = "Run {index}: wantedException={0}")
    public static Collection<Object[]> exceptionCase(){
        return Arrays.asList(new Object[][] {
                {"", new IllegalNameException("test")},
                {"Test1", new IllegalArgumentException()}
        });
    }

    private boolean[][] passableMap = new boolean[][] {
            { false, false, false, false }, { true, true, true, true },
            { true, true, true, true }, { false, false, false, false }
    };

    private Worm worm;
    private Facade facade;
    private World world;

    @Before
    public void createScene() {
        facade = new Facade();
        Random random = new Random(7357);
        world = new World(4.0, 4.0, passableMap, random);
        worm = new Worm(world, 1, 2, 0, 1, "Test");
    }

    @Parameter
    public String name;
    @Parameter(1)
    public Exception wantedException;
    @Test(expected=ModelException.class)
    public void testCreateWormException() throws Exception {
        World mockWorld = Mockito.spy(world);
        doThrow(wantedException).when(mockWorld).addEmptyTeam(anyString());
        facade.addEmptyTeam(mockWorld, name);
        verify(mockWorld, times(1)).addEmptyTeam(name);
    }
}