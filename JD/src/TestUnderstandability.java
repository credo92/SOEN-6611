package metricTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import ast.ASTReader;
import ast.ClassObject;
import ast.FieldObject;
import ast.SystemObject;
import ast.TypeObject;
import metrics.Understandability;
@ExtendWith(SystemServiceParameterResolver.class)
class TestUnderstandability {
	private Understandability understandbility = null;
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		understandbility = new Understandability();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void AbstractionWrongCalculations() {		
		double totalDepth = 0.0;
		Set<ClassObject> classes = new SystemObject().getClassObjects(); 
		double noOfClasses = classes.size();
		double total = totalDepth/noOfClasses;
		assertNotEquals(0, total , .001);		
	}

	@Test
	public void AbstractionCalculations(SystemObject system) {		
		Set<ClassObject> classes = system.getClassObjects();
		double noOfClasses = 1191.0;
        double totalDepth = 863.0;
        double expected = totalDepth/noOfClasses;
		assertEquals(0.7246011754827876, expected , .001);		
	}
	
	@Test
	public void cohesionCalculation(SystemObject system) {		
		Set<ClassObject> classes = system.getClassObjects();
		double noOfClasses = 1191.0;
        double totalCohesion = 570.3214178491273;
        double expected = totalCohesion/noOfClasses;
		assertEquals(0.478859292904389, expected , .001);		
	}
	@Test
	public void encapsulationCalculation(SystemObject system) {		
		Set<ClassObject> classes = system.getClassObjects();
		double noOfClasses = 1191.0;
        double totalRatio = 851.1973354561201;
        double expected = totalRatio/noOfClasses;
		assertEquals(0.7146912976121915, expected , .001);		
	}
	
	@Test
	public void EncapsulationWrongCalculations() {
		double totalRatio = 0.0;
		Set<ClassObject> classes = new SystemObject().getClassObjects();
		double noOfClasses = classes.size(); 
		double total = totalRatio/noOfClasses;
		assertNotEquals(0, total , .001);		
	}
	

	@Test
	public void Understandibilitycalculations() {

		double abstraction   = 0.7246011754827876;
		double encapsulation   = 0.7146912976121915;
		double coupling   = 0.3131821998320739;
		double cohesion   = 0.478859292904389;
		double polymorphism  = 33.0;
		double complexity  = 7.712846347607053;
		double designsize = 1191.0;


		double understandabilitycal =
				-0.33*abstraction +
				0.33*encapsulation -
				0.33*coupling +
				0.33*cohesion -
				0.33*polymorphism -
				0.5*complexity + 
				0.5*designsize;		

		assertEquals(580.8049800072131, understandabilitycal, .001);
	}

	@Test
	public void UnderstandibilityWrongCalculate() {

		double abstraction   = 0.7246011754827876;
		double encapsulation   = 0.7146912976121915;
		double coupling   = 0.3131821998320739;
		double cohesion   = 0.478859292904389;
		double polymorphism  = 33.0;
		double complexity  = 7.712846347607053;
		double designsize = 1191.0;


		double understandabilitycal =
				-0.33*abstraction +
				0.33*encapsulation -
				0.33*coupling +
				0.33*cohesion -
				0.33*polymorphism -
				0.5*complexity + 
				0.5*designsize;		

		assertNotEquals(580.80, understandabilitycal, .001);
	}
}


