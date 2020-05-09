package worms.model;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
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

import worms.model.exceptions.IllegalActionPointException;
import worms.model.exceptions.IllegalPositionException;

@RunWith(Parameterized.class)
public class PartialFacadeMoveExceptionTest {

    @Parameters(name = "Run {index}: wantedException={0}")
    public static Collection<Object[]> exceptionCase(){
        return Arrays.asList(new Object[][] {
                {new IllegalPositionException(3.0, 'a')},
                {new IllegalActionPointException(3)},
                {new IllegalAccessException()}
        });
    }

    // X X X X
    // . . . .
    // . . . .
    // X X X X
    private boolean[][] passableMap = new boolean[][] {
            { false, false, false, false }, { true, true, true, true },
            { true, true, true, true }, { false, false, false, false } };

    private Worm worm;
    private Facade facade;

    @Before
    public void createScene() {
        facade = new Facade();
        Random random = new Random(7357);
        World world = new World(4.0, 4.0, passableMap, random);
        worm = new Worm(world, 1, 2, 0, 1, "Test");
    }

    @Parameter
    public Exception wantedException;
    @Test(expected=ModelException.class)
    public void testMoveException() throws IllegalActionPointException, IllegalPositionException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException{
        Worm mockWorm = Mockito.spy(worm);
        Facade spyFacade = Mockito.spy((Facade)facade);

        doThrow(wantedException).when(mockWorm).move();
        spyFacade.move(mockWorm);

        verify(mockWorm, times(1)).move();
    }
}