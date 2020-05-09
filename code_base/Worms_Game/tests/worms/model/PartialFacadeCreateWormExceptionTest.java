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
import worms.model.exceptions.IllegalNameException;
import worms.model.exceptions.IllegalPositionException;
import worms.model.exceptions.IllegalRadiusException;


@RunWith(Parameterized.class)
public class PartialFacadeCreateWormExceptionTest {

    @Parameters(name = "Run {index}: wantedException={0}")
    public static Collection<Object[]> exceptionCase(){
        return Arrays.asList(new Object[][] {
                {1.0, 1.0, 1, "", new IllegalNameException("test")},
                {1.0, 1.0, -1, "Test1", new IllegalRadiusException(3)},
                {Double.NaN, 1.0, 1, "Test2", new IllegalPositionException(3.0, 'a')},
                {1.0, Double.NaN, 1, "Test2", new IllegalPositionException(3.0, 'a')},
                {Double.POSITIVE_INFINITY, 1.0, 1, "Test2", new IllegalPositionException(3.0, 'a')}
        });
    }

    private boolean[][] passableMap = new boolean[][] {
            { false, false, false, false }, { true, true, true, true },
            { true, true, true, true }, { false, false, false, false } };

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
    public double positionX;
    @Parameter(1)
    public double positionY;
    @Parameter(2)
    public double radius;
    @Parameter(3)
    public String name;
    @Parameter(4)
    public Exception wantedException;
    @Test(expected=ModelException.class)
    public void testCreateWormException() throws Exception {
        World mockWorld = Mockito.spy(world);
        facade.createWorm(mockWorld, positionX, positionY, 1.0, radius, name);
    }
}