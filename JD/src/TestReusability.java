package metricTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ast.ASTReader;
import ast.ClassObject;
import ast.FieldInstructionObject;
import ast.FieldObject;
import ast.MethodObject;
import ast.SystemObject;

class TestReusability {

	/*@BeforeAll
	static void setUpBeforeClass() throws Exception {
		 Set<ClassObject> classes = system.getClassObjects(); 
	}*/

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}


	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void CouplingWrongCalculations() {		
		double totalSize = 0.0;
		Set<ClassObject> classes = new SystemObject().getClassObjects(); 
		double total = totalSize/classes.size();
		assertNotEquals(0, total , .001);

	}



	//	@Test
	//	private void couplingWrongCalculation(){
	//		List<String> classTypes  = new SystemObject().getClassNames();
	//		List<String> cls = new ArrayList<String>(classTypes);
	//		Set<String> directClasses = new HashSet<String>();
	//		ListIterator<FieldObject> fieldIterator = classObject.getFieldIterator();
	//		while(fieldIterator.hasNext()) {
	//			FieldObject field = fieldIterator.next();
	//			if (otherClasses.contains(field.getClassName()))
	//				directClasses.add(field.getClassName());
	//		}
	//			
	//		List<MethodObject> lstMethods = classObject.getMethodList();
	//		for (int i = 0; i < lstMethods.size() - 1; i++) {
	//			List<FieldInstructionObject> lstFieldInstruction = lstMethods.get(i).getFieldInstructions();
	//			for (FieldInstructionObject param : lstFieldInstruction)
	//				if(otherClasses.contains(param.getOwnerClass()))
	//					directClasses.add(param.getOwnerClass());
	//		}
	//		assertNotEquals(0, directClasses.size());
	//	}
	//	
	@Test
	public void  Reusabilitycalculations() {
		double cntCoupling   = 0.3131821998320739;
		double cntCohesion   = 0.478859292904389;
		double cntMessaging  = 5.0822837951301425;
		double cntDesignSize = 1191.0;

		double reusabilitycal =
				-0.25*cntCoupling +
				0.25*cntCohesion +
				0.5*cntMessaging + 
				0.5*cntDesignSize;

		assertEquals(598.0825611708332, reusabilitycal, .001);
	}

	@Test
	public void  ReusabilityWrongcalculations() {
		double cntCoupling   = 0.3131821998320739;
		double cntCohesion   = 0.478859292904389;
		double cntMessaging  = 5.0822837951301425;
		double cntDesignSize = 1191.0;

		double reusabilitycal =
				-0.25*cntCoupling +
				0.25*cntCohesion +
				0.5*cntMessaging + 
				0.5*cntDesignSize;

		assertNotEquals(10, reusabilitycal, .001);	
	}
}
