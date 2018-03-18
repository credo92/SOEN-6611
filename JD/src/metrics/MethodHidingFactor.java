package metrics;

/* This code is used to calculate Method hiding factor which measures how methods 
 * are encapsulated in the classes of a system.It has the same definition as AHF, but using
 * methods rather than attributes
 * 
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
import ast.ClassObject;
import ast.FieldInstructionObject;
import ast.MethodInvocationObject;
import ast.MethodObject;
import ast.SystemObject;


/* Class to implement Method Hiding Factor under MOOD Metrics Implementation */
public class MethodHidingFactor {

	private SystemObject system;
	
	/* Map to store import coupling metrics  */
	private Map<String, LinkedHashMap<String, Integer>> ICouplingMap;
	
	/* Map to store class methods */
	private Map<String, Integer> classMethodMap;

	/* Class Constructor to initialize objects */
	public MethodHidingFactor(SystemObject system) {

		this.system = system;
		this.ICouplingMap = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
		this.classMethodMap = new LinkedHashMap<String, Integer>();

		List<String> classNames = system.getClassNames();
		for (String className : classNames) {
			LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
			for (String classNamex : classNames) {
				map.put(classNamex, 0);
			}
			ICouplingMap.put(className, map);
		}

		calculateCoupling();
		calculateMHF();
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

	/* Function to return average coupling for a class parsed */
	private double getAverageCouplingClass(String className) {
		LinkedHashMap<String, Integer> map = ICouplingMap.get(className);
		int total = 0;
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			if (!key.equals(className))
				total += map.get(key);
		}

		return (1 -(double) total / (double) (keySet.size() - 1));
	}

	/* Function to return coupling for the system parsed */
	private double getSystemCoupling() {
		Set<String> keySet = ICouplingMap.keySet();
		double total = 0;
		for (String key : keySet) {
			total += getAverageCouplingClass(key);
		}
		return total;
	}

	/* Function to get method counts for the system parsed */
	private double getSystemMethodCount() {
		double sum = 0;

		for (Map.Entry<String, Integer> entry : classMethodMap.entrySet()) {
			sum += entry.getValue();
		}
		return sum;
	}

	/* Function to calculate Method Hiding factor and printing it */
	void calculateMHF() {

		double MHF = ((getSystemCoupling()) / getSystemMethodCount());
		System.out.println("*Method Hiding Factor*: " + MHF);
	}

}
