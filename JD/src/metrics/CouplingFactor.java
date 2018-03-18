package metrics;

/* This code is used to calculate Coupling factor which measures the coupling between 
 * the classes of a system. If no classes are coupled, CF = 0%. If all classes are coupled 
 * to all other classes, CF=100%.
*/

/* List of packages used inside the class*/
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import ast.Access;
import ast.ClassObject;
import ast.FieldInstructionObject;
import ast.MethodInvocationObject;
import ast.MethodObject;
import ast.SystemObject;

/* Class to implement Coupling Factor under MOOD Metrics Implementation */

public class CouplingFactor {

	private SystemObject system;
	
	/* Map to store import coupling metrics  */
	private Map<String, LinkedHashMap<String, Integer>> ICouplingMap;
	
	/* Map to store classes methods  */
	private Map<String, Integer> classMethodMap;;
	int totalNoOfClass = 0;

	/* Class Constructor to initialize objects */
	public CouplingFactor(SystemObject system) {

		this.system = system;
		this.ICouplingMap = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
		this.classMethodMap = new LinkedHashMap<String, Integer>();

		/* List containing all class names */
		List<String> classNames = system.getClassNames();
		for (String className : classNames) {
			totalNoOfClass += 1;
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
			for (String classNamex : classNames) {
				map.put(classNamex, 0);
			}
			ICouplingMap.put(className, map);
		}

		calculateCoupling();
		calculateCF();
	}

	/* Function to calculate coupling */
	private void calculateCoupling() {
		
		/* List to iterate amongst all classes */
		ListIterator<ClassObject> classIterator = system.getClassListIterator();
		while (classIterator.hasNext()) {
			ClassObject classObject = classIterator.next();

			LinkedHashMap<String, Integer> map = ICouplingMap.get(classObject.getName());
			
			/* List to iterate amongst all methods */
			ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();

			if (!classMethodMap.containsKey(classObject.getName())) {

				if (classObject.getMethodList() != null) {
					classMethodMap.put(classObject.getName(), classObject.getMethodList().toArray().length);
				} else {
					classMethodMap.put(classObject.getName(), 0);
				}
			}

			while (methodIterator.hasNext()) {
				MethodObject method = methodIterator.next();
				if (method.getMethodBody() != null) {

					int accessMethodCount = 0;
					Access accessMethod = method.getAccess();
					if (accessMethod.PUBLIC == Access.PUBLIC) {
						accessMethodCount = accessMethodCount + 1;
					}

					/* List for method invocations using AST from existing JD Code */
					List<MethodInvocationObject> methodInvocations = method.getMethodInvocations();
					for (MethodInvocationObject methodInvocation : methodInvocations) {
						String methodInvocationOrigin = methodInvocation.getOriginClassName();
						if (map.keySet().contains(methodInvocationOrigin)) {

							ClassObject originClass = system.getClassObject(methodInvocationOrigin);
							MethodObject originMethod = originClass.getMethod(methodInvocation);

							if (originMethod != null && !originMethod.isStatic())
								map.put(methodInvocationOrigin, map.get(methodInvocationOrigin) + 1);
						}
					}
				}
			}
		}
	}
	
	/* Function to get average coupling for a class */

	private double getAverageClassCoupling(String className) {
		LinkedHashMap<String, Integer> map = ICouplingMap.get(className);
		int total = 0;
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			if (!key.equals(className))
				total += map.get(key);
		}

		return (double) total;
	}

	/* Function to get  coupling for the whole system parsed */
	
	private double getSystemCoupling() {
		Set<String> keySet = ICouplingMap.keySet();
		double sum = 0;
		for (String key : keySet) {
			sum += getAverageClassCoupling(key);
		}
		return sum;
	}

	/* Function to print and calculate coupling factor */
	private void calculateCF() {


		System.out.println("*Coupling Factor*: " + getSystemCoupling() / ((totalNoOfClass * totalNoOfClass) - totalNoOfClass));
	}

}
