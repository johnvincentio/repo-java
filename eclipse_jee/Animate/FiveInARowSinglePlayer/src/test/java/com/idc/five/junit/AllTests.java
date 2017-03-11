package com.idc.five.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.idc.five.junit.board.Board1Test;
import com.idc.five.junit.board.Board2Test;
import com.idc.five.junit.board.BoardATest;
import com.idc.five.junit.coordinate.CoordinateTest;
import com.idc.five.junit.counting.Counting1Test;
import com.idc.five.junit.counting.Counting2Test;
import com.idc.five.junit.counting.CountingATest;
import com.idc.five.junit.counting.CountingBTest;
import com.idc.five.junit.counting.CountingCTest;
import com.idc.five.junit.counting.CountingDTest;
import com.idc.five.junit.counting.CountingETest;
import com.idc.five.junit.counting.CountingFTest;
import com.idc.five.junit.counting.CountingGTest;
import com.idc.five.junit.counting.CountingHTest;
import com.idc.five.junit.game.Game1Test;
import com.idc.five.junit.game.Game2Test;
import com.idc.five.junit.game.Game3Test;
import com.idc.five.junit.game.Game4Test;
import com.idc.five.junit.game.Sizing1Test;
import com.idc.five.junit.game.Sizing2Test;
import com.idc.five.junit.game.Sizing3Test;
import com.idc.five.junit.game.Sizing4Test;
import com.idc.five.junit.game.Sizing5Test;
import com.idc.five.junit.moves.Moves1Test;
import com.idc.five.junit.scoring.Scoring1Test;
import com.idc.five.junit.scoring.Scoring2Test;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	Board1Test.class, Board2Test.class, BoardATest.class,

	CoordinateTest.class,

	Counting1Test.class, Counting2Test.class,
	CountingATest.class, CountingBTest.class, CountingCTest.class, CountingDTest.class, CountingETest.class, 
	CountingFTest.class, CountingGTest.class, CountingHTest.class, 

	Game1Test.class, Game2Test.class, Game3Test.class, Game4Test.class,

	Moves1Test.class,

	Sizing1Test.class, Sizing2Test.class, Sizing3Test.class, Sizing4Test.class, Sizing5Test.class,

	Scoring1Test.class, Scoring2Test.class
})

public class AllTests {

}
