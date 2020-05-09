package worms.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import worms.gui.game.IActionHandler;
import worms.model.exceptions.IllegalActionPointException;
import worms.model.exceptions.IllegalNameException;
import worms.model.exceptions.IllegalPositionException;
import worms.model.exceptions.IllegalRadiusException;
import worms.model.part3.Action;
import worms.model.part3.Type;
import worms.model.programs.ParseOutcome;
import worms.model.programs.ParseOutcome.Success;
import worms.util.Util;

public class PartialFacadeTest {

	private static final double EPS = Util.DEFAULT_EPSILON;

	private IFacade facade;
	private Random random;
	private World world;
	private Worm worm;
	private Program program;
	private Food food;
	private Projectile projectile;

	// X X X X
	// . . . .
	// . . . .
	// X X X X
	private boolean[][] passableMap = new boolean[][] {
			{ false, false, false, false }, { true, true, true, true },
			{ true, true, true, true }, { false, false, false, false } };


	@Before
	public void setup() {
		facade = new Facade();
		random = new Random(7357);
		world = new World(4.0, 4.0, passableMap, random);
		worm = new Worm(world, 1, 2, 0, 1, "Test");
		program = new Program(new HashMap<String,Type>(), new Action(), new SimpleActionHandler(facade));
		food = new Food(world, 1.0, 1.0);
		projectile = new Projectile(world, 1.0, 1.0, 1.0, 1, Guns.Bazooka);
	}

	@Test
	public void testCreateWormWithProgram(){
		Facade anotherObjSpy = Mockito.spy((Facade)facade);
		World mockWorld = Mockito.spy(world);
		Worm mockWorm = Mockito.spy(worm);
		Program mockProgram = mock(Program.class);

		doReturn(mockWorm).when(mockWorld).createWorm(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString(), any(Program.class));
		doReturn(mockWorm).when(anotherObjSpy).createWorm(any(World.class), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString());

		anotherObjSpy.createWorm(mockWorld, 1, 2, 0, 1, "Test", mockProgram);
		verify(mockWorld, times(1)).createWorm(1, 2, 0, 1, "Test", mockProgram);
		verify(anotherObjSpy, times(0)).createWorm(mockWorld, 1, 2, 0, 1, "Test");
	}

	@Test
	public void testCreateWormWithoutProgram() {
		Facade anotherObjSpy = Mockito.spy((Facade)facade);
		World mockWorld = Mockito.spy(world);
		Worm mockWorm = Mockito.spy(worm);
		Program mockProgram = mock(Program.class);

		doReturn(mockWorm).when(mockWorld).createWorm(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString(), any(Program.class));
		doReturn(mockWorm).when(anotherObjSpy).createWorm(any(World.class), anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyString());

		anotherObjSpy.createWorm(mockWorld, 1, 2, 0, 1, "Test", null);
		verify(anotherObjSpy, times(1)).createWorm(mockWorld, 1, 2, 0, 1, "Test");
		verify(mockWorld, times(0)).createWorm(1, 2, 0, 1, "Test", mockProgram);

	}

	@Test
	public void testMaximumActionPoints() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5).when(mockWorm).getMaxActionPoints();

		int result = facade.getMaxActionPoints(mockWorm);

		verify(mockWorm, times(1)).getMaxActionPoints();
		assertEquals(5, result);
	}

	@Test
	public void testMove() {
		Worm mockWorm = Mockito.spy(worm);

		try {
			doNothing().when(mockWorm).move();
			facade.move(mockWorm);

			verify(mockWorm, times(1)).move();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTurn() {
		Worm mockWorm = Mockito.spy(worm);
		doNothing().when(mockWorm).turn(anyDouble());
		facade.turn(mockWorm, 5.0);

		verify(mockWorm, times(1)).turn(5.0);
	}

	@Test(expected=ModelException.class)
	public void testTurnException() {
		Worm mockWorm = Mockito.spy(worm);
		Facade spyFacade = Mockito.spy((Facade)facade);

		doThrow(new AssertionError()).when(mockWorm).turn(anyDouble());
		spyFacade.turn(mockWorm, 5.0);

		verify(mockWorm, times(1)).turn(5.0);
	}

	@Test
	public void testGetJumpTime() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(6.0).when(mockWorm).getJumpTime(anyDouble());

		double result = facade.getJumpTime(mockWorm, 5.0);

		verify(mockWorm, times(1)).getJumpTime(5.0);
		assertEquals(6.0, result, EPS);
	}

	@Test(expected=ModelException.class)
	public void testGetJumpTimeException() {
		Worm mockWorm = Mockito.spy(worm);
		Facade spyFacade = Mockito.spy((Facade)facade);

		doThrow(new IllegalPositionException(5.0, 'a')).when(mockWorm).getJumpTime(anyDouble());
		spyFacade.getJumpTime(mockWorm, 5.0);

		verify(mockWorm, times(1)).getJumpTime(5.0);
	}

	@Test(expected=ModelException.class)
	public void testJumpAbleException() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(false).when(mockWorm).isAbleToJump();

		facade.jump(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToJump();
	}

	@Test
	public void testJump() {
		Worm mockWorm = Mockito.spy(worm);
		doNothing().when(mockWorm).jump(anyDouble());
		doReturn(true).when(mockWorm).isAbleToJump();

		facade.jump(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToJump();
		verify(mockWorm, times(1)).jump(5.0);
	}

	@Test(expected=ModelException.class)
	public void testJumpException() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(true).when(mockWorm).isAbleToJump();
		doThrow(new IllegalPositionException(5.0,'a')).when(mockWorm).jump(anyDouble());

		facade.jump(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToJump();
		verify(mockWorm, times(1)).jump(5.0);
	}

	@Test
	public void testJumpProjectile() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doNothing().when(mockProjectile).jump(anyDouble());

		facade.jump(mockProjectile, 5.0);

		verify(mockProjectile, times(1)).jump(5.0);


		World mockWorld = Mockito.spy(world);
	}

	@Test(expected=ModelException.class)
	public void testJumpExceptionProjectile() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doThrow(new IllegalPositionException(5.0,'a')).when(mockProjectile).jump(anyDouble());

		facade.jump(mockProjectile, 5.0);

		verify(mockProjectile, times(1)).jump(5.0);
	}

	@Test(expected=ModelException.class)
	public void testGetJumpStepAbleException() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(false).when(mockWorm).isAbleToJump();

		facade.getJumpStep(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToJump();
	}

	@Test
	public void testGetJumpStep() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(new double[] {1.1,2.2}).when(mockWorm).getJumpStep(anyDouble());
		doReturn(true).when(mockWorm).isAbleToJump();

		double[] result = facade.getJumpStep(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToJump();
		verify(mockWorm, times(1)).getJumpStep(5.0);
		assertEquals(1.1, result[0], EPS);
		assertEquals(2.2, result[1], EPS);
	}

	@Test(expected=ModelException.class)
	public void testGetJumpStepException() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(true).when(mockWorm).isAbleToJump();
		doThrow(new IllegalPositionException(5.0,'a')).when(mockWorm).getJumpStep(anyDouble());

		facade.getJumpStep(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToJump();
		verify(mockWorm, times(1)).getJumpStep(5.0);
	}

	@Test
	public void testGetJumpStepProjectile() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doReturn(new double[] {1.1,2.2}).when(mockProjectile).getJumpStep(anyDouble());

		double[] result = facade.getJumpStep(mockProjectile, 5.0);

		verify(mockProjectile, times(1)).getJumpStep(5.0);
		assertEquals(1.1, result[0], EPS);
		assertEquals(2.2, result[1], EPS);
	}

	@Test(expected=ModelException.class)
	public void testGetJumpStepExceptionProjectile() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doThrow(new IllegalPositionException(5.0,'a')).when(mockProjectile).getJumpStep(anyDouble());

		facade.getJumpStep(mockProjectile, 5.0);

		verify(mockProjectile, times(1)).getJumpStep(5.0);
	}

	@Test
	public void testGetJumpTimeProjectile() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doReturn(5.0).when(mockProjectile).getJumpTime(anyDouble());

		double result = facade.getJumpTime(mockProjectile, 5.0);

		verify(mockProjectile, times(1)).getJumpTime(5.0);
		assertEquals(5.0, result, EPS);
	}

	@Test(expected=ModelException.class)
	public void testGetJumpTimeExceptionProjectile() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doThrow(new IllegalPositionException(5.0,'a')).when(mockProjectile).getJumpTime(anyDouble());

		facade.getJumpTime(mockProjectile, 5.0);

		verify(mockProjectile, times(1)).getJumpTime(5.0);
	}

	@Test
	public void testSetRadius() {
		Worm mockWorm = Mockito.spy(worm);
		doNothing().when(mockWorm).setRadius(anyDouble());

		facade.setRadius(mockWorm, 5.0);

		verify(mockWorm, times(1)).setRadius(5.0);
	}

	@Test(expected=ModelException.class)
	public void testSetRadiusException() {
		Worm mockWorm = Mockito.spy(worm);
		doThrow(new IllegalRadiusException(5.0)).when(mockWorm).setRadius(anyDouble());

		facade.setRadius(mockWorm, 5.0);

		verify(mockWorm, times(1)).setRadius(5.0);
	}

	@Test
	public void testRename() {
		Worm mockWorm = Mockito.spy(worm);
		doNothing().when(mockWorm).setName(anyString());

		facade.rename(mockWorm, "Test");

		verify(mockWorm, times(1)).setName("Test");
	}

	@Test(expected=ModelException.class)
	public void testRenameException() {
		Worm mockWorm = Mockito.spy(worm);
		doThrow(new IllegalNameException("Test")).when(mockWorm).setName(anyString());

		facade.rename(mockWorm, "Test");

		verify(mockWorm, times(1)).setName("Test");
	}

	@Test
	public void testShoot() {
		Worm mockWorm = Mockito.spy(worm);
		try {
			doNothing().when(mockWorm).shoot(anyInt());

			facade.shoot(mockWorm, 5);

			verify(mockWorm, times(1)).shoot(5);
		} catch (IllegalActionPointException e) {
			e.printStackTrace();
		}
	}

	@Test(expected=ModelException.class)
	public void testShootException() throws IllegalActionPointException {
		Worm mockWorm = Mockito.spy(worm);
		doThrow(new IllegalActionPointException(5)).when(mockWorm).shoot(anyInt());

		facade.shoot(mockWorm, 5);

		verify(mockWorm, times(1)).shoot(5);
	}

	@Test
	public void testCanFall() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(true).when(mockWorm).isAbleToFall();

		boolean result = facade.canFall(mockWorm);

		verify(mockWorm, times(1)).isAbleToFall();
		assertEquals(true, result);
	}

	@Test
	public void testCanTurn() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(true).when(mockWorm).isAbleToTurn(anyDouble());

		boolean result = facade.canTurn(mockWorm, 5.0);

		verify(mockWorm, times(1)).isAbleToTurn(anyDouble());
		assertEquals(true, result);
	}

	@Test
	public void testCanMove() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(true).when(mockWorm).isAbleToMove();

		boolean result = facade.canMove(mockWorm);

		verify(mockWorm, times(1)).isAbleToMove();
		assertEquals(true, result);
	}

	@Test
	public void testFall() {
		Worm mockWorm = Mockito.spy(worm);
		doNothing().when(mockWorm).fall();

		facade.fall(mockWorm);

		verify(mockWorm, times(1)).fall();
	};


	@Test
	public void testGetX() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5.0).when(mockWorm).getXPosition();

		double result = facade.getX(mockWorm);

		verify(mockWorm, times(1)).getXPosition();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetY() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5.0).when(mockWorm).getYPosition();

		double result = facade.getY(mockWorm);

		verify(mockWorm, times(1)).getYPosition();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetRadius() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5.0).when(mockWorm).getRadius();

		double result = facade.getRadius(mockWorm);

		verify(mockWorm, times(1)).getRadius();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetMinimalRadius() {
		Worm mockWorm = Mockito.spy(worm);

		double result = facade.getMinimalRadius(mockWorm);

		assertEquals(0.25, result, EPS);
	};

	@Test
	public void testGetActionPoints() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5).when(mockWorm).getRemainingActionPoints();

		int result = facade.getActionPoints(mockWorm);

		verify(mockWorm, times(1)).getRemainingActionPoints();
		assertEquals(5, result);
	};

	@Test
	public void testGetName() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn("Test").when(mockWorm).getName();

		String result = facade.getName(mockWorm);

		verify(mockWorm, times(1)).getName();
		assertEquals("Test", result);
	};

	@Test
	public void testGetMass() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5.0).when(mockWorm).getMass();

		double result = facade.getMass(mockWorm);

		verify(mockWorm, times(1)).getMass();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetNullTeamName() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(null).when(mockWorm).getTeam();

		String result = facade.getTeamName(mockWorm);

		verify(mockWorm, times(1)).getTeam();
		assertEquals("", result);
	};

	@Test
	public void testGetTeamName() {
		Worm mockWorm = Mockito.spy(worm);
		Team mockTeam = mock(Team.class);
		doReturn(mockTeam).when(mockWorm).getTeam();
		doReturn("Test").when(mockTeam).getName();

		String result = facade.getTeamName(mockWorm);

		verify(mockWorm, times(2)).getTeam();
		assertEquals("Test", result);
	};

	@Test
	public void testGetHitPoints() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5).when(mockWorm).getRemainingHitPoints();

		int result = facade.getHitPoints(mockWorm);

		verify(mockWorm, times(1)).getRemainingHitPoints();
		assertEquals(5, result);
	};

	@Test
	public void testGetMaxHitPoints() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(5).when(mockWorm).getMaxHitPoints();

		int result = facade.getMaxHitPoints(mockWorm);

		verify(mockWorm, times(1)).getMaxHitPoints();
		assertEquals(5, result);
	};

	@Test
	public void testSelectNextWeapon() {
		Worm mockWorm = Mockito.spy(worm);
		doNothing().when(mockWorm).selectNextWeapon();

		facade.selectNextWeapon(mockWorm);

		verify(mockWorm, times(1)).selectNextWeapon();
	}

	@Test
	public void testGetSelectedWeapon() {
		Worm mockWorm = Mockito.spy(worm);
		Guns mockEnum = Guns.Bazooka;
		doReturn(mockEnum).when(mockWorm).getCurrentWeapon();

		String result = facade.getSelectedWeapon(mockWorm);

		verify(mockWorm, times(1)).getCurrentWeapon();
		assertEquals("Bazooka", result);
	};

	@Test
	public void testGetNullProgram() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(null).when(mockWorm).getProgram();

		boolean result = facade.hasProgram(mockWorm);

		verify(mockWorm, times(1)).getProgram();
		assertEquals(false, result);
	};

	@Test
	public void testGetProgram() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(mock(Program.class)).when(mockWorm).getProgram();

		boolean result = facade.hasProgram(mockWorm);

		verify(mockWorm, times(1)).getProgram();
		assertEquals(true, result);
	};

	@Test
	public void testIsAliveFalse() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(false).when(mockWorm).isTerminated();

		boolean result = facade.isAlive(mockWorm);

		verify(mockWorm, times(1)).isTerminated();
		assertEquals(true, result);
	};

	@Test
	public void testIsAliveTrue() {
		Worm mockWorm = Mockito.spy(worm);
		doReturn(true).when(mockWorm).isTerminated();

		boolean result = facade.isAlive(mockWorm);

		verify(mockWorm, times(1)).isTerminated();
		assertEquals(false, result);
	};

	@Test
	public void testGetRadiusFood() {
		Food mockFood = Mockito.spy(food);
		doReturn(5.0).when(mockFood).getRadius();

		double result = facade.getRadius(mockFood);

		verify(mockFood, times(1)).getRadius();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetXProjectile() {
		Projectile mockFood = Mockito.spy(projectile);
		doReturn(5.0).when(mockFood).getXPosition();

		double result = facade.getX(mockFood);

		verify(mockFood, times(1)).getXPosition();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetYProjectile() {
		Projectile mockFood = Mockito.spy(projectile);
		doReturn(5.0).when(mockFood).getYPosition();

		double result = facade.getY(mockFood);

		verify(mockFood, times(1)).getYPosition();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetRadiusProjectile() {
		Projectile mockFood = Mockito.spy(projectile);
		doReturn(5.0).when(mockFood).getRadius();

		double result = facade.getRadius(mockFood);

		verify(mockFood, times(1)).getRadius();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetXFood() {
		Food mockFood = Mockito.spy(food);
		doReturn(5.0).when(mockFood).getXPosition();

		double result = facade.getX(mockFood);

		verify(mockFood, times(1)).getXPosition();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetYFood() {
		Food mockFood = Mockito.spy(food);
		doReturn(5.0).when(mockFood).getYPosition();

		double result = facade.getY(mockFood);

		verify(mockFood, times(1)).getYPosition();
		assertEquals(5.0, result, EPS);
	};

	@Test
	public void testGetFood() {
		World mockWorld = Mockito.spy(world);
		doReturn(new LinkedList<Food>()).when(mockWorld).getAllFood();

		Collection<Food> result = facade.getFood(mockWorld);

		verify(mockWorld, times(1)).getAllFood();
		assertEquals(new LinkedList<Food>(), result);
	};

	@Test
	public void testGetWorms() {
		World mockWorld = Mockito.spy(world);
		doReturn(new LinkedList<Worm>()).when(mockWorld).getAllWorms();

		Collection<Worm> result = facade.getWorms(mockWorld);

		verify(mockWorld, times(1)).getAllWorms();
		assertEquals(new LinkedList<Worm>(), result);
	};

	@Test
	public void testIsAdjacent() {
		World mockWorld = Mockito.spy(world);
		doReturn(true).when(mockWorld).isAdjacentPosition(anyDouble(), anyDouble(), anyDouble());

		boolean result = facade.isAdjacent(mockWorld, 2.0, 3.0, 4.0);

		verify(mockWorld, times(1)).isAdjacentPosition(2.0, 3.0, 4.0);
		assertEquals(true, result);
	};

	@Test
	public void testIsGameFinished() {
		World mockWorld = Mockito.spy(world);
		doReturn(true).when(mockWorld).isGameFinished();

		boolean result = facade.isGameFinished(mockWorld);

		verify(mockWorld, times(1)).isGameFinished();
		assertEquals(true, result);
	};

	@Test
	public void testIsImpassable() {
		World mockWorld = Mockito.spy(world);
		doReturn(true).when(mockWorld).isImpassablePosition(anyDouble(), anyDouble(), anyDouble());

		boolean result = facade.isImpassable(mockWorld, 2.0, 3.0, 4.0);

		verify(mockWorld, times(1)).isImpassablePosition(2.0, 3.0, 4.0);
		assertEquals(true, result);
	};

	@Test
	public void testIsWellFormed() {
		Program mockProgram = Mockito.spy(program);
		doReturn(true).when(mockProgram).isValidProgram();

		boolean result = facade.isWellFormed(mockProgram);

		verify(mockProgram, times(1)).isValidProgram();
		assertEquals(true, result);
	};

	@Test
	public void testIsActiveTrue() {
		Food mockFood = Mockito.spy(food);
		doReturn(true).when(mockFood).isTerminated();

		boolean result = facade.isActive(mockFood);

		verify(mockFood, times(1)).isTerminated();
		assertEquals(false, result);
	};

	@Test
	public void testIsActiveFalse() {
		Food mockFood = Mockito.spy(food);
		doReturn(false).when(mockFood).isTerminated();

		boolean result = facade.isActive(mockFood);

		verify(mockFood, times(1)).isTerminated();
		assertEquals(true, result);
	};

	@Test
	public void testIsActiveFalseNullProjectile() {
		Projectile mockProjectile = null;

		boolean result = facade.isActive(mockProjectile);

		assertEquals(false, result);
	};

	@Test
	public void testIsActiveFalseProjectileNullWorld() {
		Projectile mockProjectile = Mockito.spy(projectile);
		doReturn(null).when(mockProjectile).getWorld();

		boolean result = facade.isActive(mockProjectile);

		verify(mockProjectile, times(1)).getWorld();
		assertEquals(false, result);
	};

	@Test
	public void testIsActiveFalseEqualProjectile() {
		World mockWorld = Mockito.spy(world);
		Projectile mockProjectile = Mockito.spy(projectile);
		doReturn(mockWorld).when(mockProjectile).getWorld();
		doReturn(mockProjectile).when(mockWorld).getActiveProjectile();

		boolean result = facade.isActive(mockProjectile);

		verify(mockProjectile, times(2)).getWorld();
		assertEquals(false, result);
	};

	@Test
	public void testIsActiveTrueEqualProjectile() {
		World mockWorld = Mockito.spy(world);
		Projectile mockProjectile = Mockito.spy(projectile);
		Projectile mockProjectile2 = mock(Projectile.class);
		doReturn(mockWorld).when(mockProjectile).getWorld();
		doReturn(mockProjectile2).when(mockWorld).getActiveProjectile();

		boolean result = facade.isActive(mockProjectile);

		verify(mockProjectile, times(2)).getWorld();
		assertEquals(true, result);
	};

	@Test
	public void testGetWinnerWithNoTeam() {
		World mockWorld = Mockito.spy(world);
		Worm mockWorm = Mockito.spy(worm);

		doReturn(null).when(mockWorld).getWinningTeam();
		doReturn(mockWorm).when(mockWorld).getWinningWorm();
		doReturn("Test").when(mockWorm).getName();

		String result = facade.getWinner(mockWorld);

		verify(mockWorld, times(1)).getWinningTeam();
		verify(mockWorld, times(1)).getWinningWorm();
		verify(mockWorm, times(1)).getName();
		assertEquals("Test", result);
	};

	@Test
	public void testGetWinnerWithTeam() {
		World mockWorld = Mockito.spy(world);
		Team mockTeam = mock(Team.class);

		doReturn(mockTeam).when(mockWorld).getWinningTeam();
		doReturn("Test").when(mockTeam).getName();

		String result = facade.getWinner(mockWorld);

		verify(mockTeam, times(1)).getName();
		verify(mockWorld, times(1)).getWinningTeam();
		assertEquals("Test", result);
	};

	@Test
	public void testGetWinnerWithNullWorm() {
		World mockWorld = Mockito.spy(world);

		doReturn(null).when(mockWorld).getWinningTeam();
		doReturn(null).when(mockWorld).getWinningWorm();

		String result = facade.getWinner(mockWorld);

		verify(mockWorld, times(1)).getWinningTeam();
		verify(mockWorld, times(1)).getWinningWorm();
		assertEquals("Bob & Matthijs", result);
	};

	@Test
	public void testGetActiveProjectile() {
		World mockWorld = Mockito.spy(world);
		doReturn(mock(Projectile.class)).when(mockWorld).getActiveProjectile();

		facade.getActiveProjectile(mockWorld);

		verify(mockWorld, times(1)).getActiveProjectile();
	};

	@Test
	public void testAddNewFood() {
		World mockWorld = Mockito.spy(world);
		doNothing().when(mockWorld).addNewFood();

		facade.addNewFood(mockWorld);

		verify(mockWorld, times(1)).addNewFood();
	};

	@Test
	public void testAddEmptyTeam() {
		World mockWorld = Mockito.spy(world);
		doReturn(mock(Team.class)).when(mockWorld).addEmptyTeam(anyString());

		facade.addEmptyTeam(mockWorld, "Test");

		verify(mockWorld, times(1)).addEmptyTeam("Test");
	};

	@Test
	public void testCreateWorm() throws Exception {
		World mockWorld = Mockito.spy(world);
		Facade spyFacade = Mockito.spy((Facade)facade);

		Worm worm = spyFacade.createWorm(mockWorld, 1.0,1.0,-4*Math.PI, 1.0, "Test");

		assertEquals(0.0, worm.getDirection(), EPS);
	};

	@Test
	public void testStartGame() {
		World mockWorld = Mockito.spy(world);
		doNothing().when(mockWorld).startGame();

		facade.startGame(mockWorld);

		verify(mockWorld, times(1)).startGame();
	};

	@Test
	public void testStartNextTurn() {
		World mockWorld = Mockito.spy(world);
		doNothing().when(mockWorld).startNextTurn();

		facade.startNextTurn(mockWorld);

		verify(mockWorld, times(1)).startNextTurn();
	};

	@Test
	public void testCreateFood() {
		World mockWorld = Mockito.spy(world);
		doReturn(mock(Food.class)).when(mockWorld).createFood(anyDouble(), anyDouble());

		facade.createFood(mockWorld, 5.0, 6.0);

		verify(mockWorld, times(1)).createFood(5.0, 6.0);
	};

	@Test
	public void testProgram() {
		IActionHandler handler = new SimpleActionHandler(facade);
		World world = facade.createWorld(100.0, 100.0, new boolean[][] { {true}, {false} }, random);
		ParseOutcome<?> outcome = facade.parseProgram(""
				+ "double x;\n"
				+ "while (x < 1.5) {\n"
				+ "x := x + 0.1;\n"
				+ "}\n "
				+ "turn x;\n", handler);
		assertTrue(outcome.isSuccess());
		Program program = ((Success)outcome).getResult();
		Worm worm = facade.createWorm(world, 50.0, 50.51, 0, 0.5, "Test", program);
		facade.addNewWorm(world, null); // add another worm
		double oldOrientation = facade.getOrientation(worm);
		//facade.startGame(world); // this will run the program
		worm.getProgram().nextExec(); // run program only once!
		double newOrientation = facade.getOrientation(worm);
		assertEquals(oldOrientation + 1.5, newOrientation, EPS);
		assertNotEquals(worm, facade.getCurrentWorm(world)); // turn must end after executing program
	}

}