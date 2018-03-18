package metrics;

/* This code is used to calculate Attribute hiding factor which measures how attributes 
 * are encapsulated in the classes of a system.It has the same definition as MHF, but using
 * attributes rather than methods
 * 
 */

/* List of packages used inside the class*/
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import ast.ClassObject;
import ast.MethodObject;
import ast.SystemObject;


/* Class to implement Attribute Hiding Factor under MOOD Metrics Implementation */

public class AttHidingFactor {

	private SystemObject system;
	
/* Map to store import coupling metrics  */
	
	private Map<String, LinkedHashMap<String, Integer>> ICouplingMap;
	private Map<String, Integer> classFieldMap;

/* Class Constructor to initialize objects */
	
	public AttHidingFactor(SystemObject system) {

		this.system = system;
		this.ICouplingMap = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
		this.classFieldMap = new LinkedHashMap<String, Integer>();
		
		/* List containing all class names */
		List<String> classNames = system.getClassNames();
		for (String className : classNames) {
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
			for (String classNameX : classNames) {
				map.put(classNameX, 0);
			}
			ICouplingMap.put(className, map);
		}

		calculateCoupling();
		calculateAttHidingFactor();

	}

/* Function to calculate coupling */
	private void calculateCoupling()
	{
		/* List to iterate amongst all classes */
		ListIterator<ClassObject> classIterator = system.getClassListIterator();
		while (classIterator.hasNext()) {
			ClassObject classObject = classIterator.next();
			LinkedHashMap<String, Integer> map = ICouplingMap.get(classObject.getName());
			
			/* List to iterate amongst all methods */
			ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();

			if (!classFieldMap.containsKey(classObject.getName())) {

				if (classObject.getFieldList() != null) {
					classFieldMap.put(classObject.getName(), classObject.getFieldList().toArray().length);
				} else {
					classFieldMap.put(classObject.getName(), 0);
				}
			}

			while (methodIterator.hasNext()) {
				MethodObject method = methodIterator.next();
				if (method.getMethodBody() != null) {

					/* List for field instructions using AST from existing JD Code */
					List<ast.FieldInstructionObject> fieldInstructions = method.getFieldInstructions();
					for (ast.FieldInstructionObject fieldInstruction : fieldInstructions) {
						String fieldInstructionOrigin = fieldInstruction.getOwnerClass();
						if (map.keySet().contains(fieldInstructionOrigin) && !fieldInstruction.isStatic()) {
							map.put(fieldInstructionOrigin, map.get(fieldInstructionOrigin) + 1);
						}
					}
				}
			}
		}
	}

	/* Function to return average coupling for a class parsed */
	private double getAverageCoupling(String className) {
		LinkedHashMap<String, Integer> map = ICouplingMap.get(className);
		Set<String> keySet = map.keySet();
		int total = 0;
		for (String key : keySet) {
			if (!key.equals(className))
				total += map.get(key);
		}

		return (1 -(double) total / (double) (keySet.size() - 1));
	}

	/* Function to get coupling for system parsed */
	private double getSystemCoupling() {
		Set<String> keySet = ICouplingMap.keySet();
		double total = 0;
		for (String key : keySet) {
			total += getAverageCoupling(key);
		}
		return total;
	}

	/* Function to get field count from system parsed */
	private double getSystemFieldCount() {
		double total = 0;

		for (Map.Entry<String, Integer> entry : classFieldMap.entrySet()) {
			total += entry.getValue();
		}
		return total;
	}

	/* Function to calculate AHF for system parsed */
	private void calculateAttHidingFactor() {

		double atthidingfactor = ((getSystemCoupling()) / getSystemFieldCount());
		double AHF = atthidingfactor;
		System.out.println("*Attribute Hiding Factor*: " + AHF);
	}

}
