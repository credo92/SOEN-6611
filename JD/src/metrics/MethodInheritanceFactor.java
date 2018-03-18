package metrics;

/* This code is used to calculate Method inheritance factor, it has the same definition as AIF, 
 * but using Methods rather than attributes.A low MIF value indicates lack of inheritance or heavy use of 
 * overriding, which essentially minimizes the benefit of reuse through inheritance. If all inherited 
 * methods are overridden, then MIF=0
 * 
 */

/* List of packages used inside the class*/
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import ast.Access;
import ast.ClassObject;
import ast.MethodInvocationObject;
import ast.MethodObject;
import ast.SystemObject;
import ast.TypeObject;

/* Class to implement Method Inheritance Factor under MOOD Metrics Implementation */
public class MethodInheritanceFactor {

	private SystemObject system;
	
	/* Map to store inherited methods */
	private Map<ClassObject, Integer> inheritedMethodMap;
	
	/* Map to store all class methods */
	private Map<String, Integer> allclassMethodMap;

	/* Class Constructor to initialize objects */
	public MethodInheritanceFactor(SystemObject system) {

		this.system = system;
		
		this.inheritedMethodMap = new LinkedHashMap<ClassObject, Integer>();

		this.allclassMethodMap = new LinkedHashMap<String, Integer>();

		/* Class iterator defined and initialized */
		ListIterator<ClassObject> classIterator = system.getClassListIterator();

		while (classIterator.hasNext()) {
			ClassObject classObject = classIterator.next();
			int inheritedMethodCount = 0;

			allclassMethodMap.put(classObject.getName(), classObject.getNumberOfMethods());
			ListIterator<TypeObject> interfaceIterator = classObject.getInterfaceIterator();

			if (interfaceIterator.hasNext()) {
				ListIterator<MethodObject> methodIterator = classObject.getMethodIterator();

				while (methodIterator.hasNext()) {
					MethodObject methodObject = methodIterator.next();
					Access access = methodObject.getAccess();

					if (access == Access.PUBLIC) {
						inheritedMethodCount = inheritedMethodCount + 1;
					}

				}
				inheritedMethodMap.put(classObject, inheritedMethodCount);
			}

		}

		CalculateMIF();

	}

	/* Function to compute MIF for a system parsed  */
	private void CalculateMIF() {

		double classInheritanceFactor = 0;
		double total = 0;

		for (Map.Entry<ClassObject, Integer> entry : inheritedMethodMap.entrySet()) {

			double inheritanceMethodCount = entry.getValue();
			ClassObject classObj = entry.getKey();

			classInheritanceFactor = (inheritanceMethodCount / classObj.getMethodList().size());
			total = total + classInheritanceFactor;
	

		}

		System.out.println("*Method Inheritance Factor*: " + classInheritanceFactor / allclassMethodMap.size());
	}
}