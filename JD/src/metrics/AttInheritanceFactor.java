package metrics;

/* This code is used to calculate Attribute inheritance factor, it has the same definition as MIF, 
 * but using attributes rather than methods.A low value indicates lack of inheritance or heavy use of 
 * shadowing/hiding (when a subclass declares an attribute despite being inherited from its superclass), 
 * which is a bad practice.
 * 
 */

/* List of packages used inside the class*/
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

import ast.Access;
import ast.ClassObject;
import ast.FieldObject;
import ast.SystemObject;
import ast.TypeObject;

/* Class to implement Attribute Inheritance Factor under MOOD Metrics Implementation */
public class AttInheritanceFactor {

	private SystemObject system;
	
	/* Map to store inherited fields */
	private Map<ClassObject, Integer> inheritFieldMap;
	
	/* Map to store particular class fields */
	private Map<String, Integer> classFieldMap;
	
	/* Map to store all class fields */
	private Map<String, Integer> allclassFieldMap;
	
	/* Class Constructor to initialize objects */

	public AttInheritanceFactor(SystemObject system) {

		this.system = system;
		this.inheritFieldMap = new LinkedHashMap<ClassObject, Integer>();
		this.classFieldMap = new LinkedHashMap<String, Integer>();
		this.allclassFieldMap = new LinkedHashMap<String, Integer>();

		/* Class iterator defined and initialized */
		ListIterator<ClassObject> classIterator = system.getClassListIterator();

		while (classIterator.hasNext()) {
			ClassObject classObject = classIterator.next();
			int inheritedFieldCount = 0;

			allclassFieldMap.put(classObject.getName(), classObject.getFieldList().toArray().length);
			ListIterator<TypeObject> interfaceIterator = classObject.getInterfaceIterator();

			if (interfaceIterator.hasNext()) {
				ListIterator<FieldObject> fieldIerator = classObject.getFieldIterator();

				while (fieldIerator.hasNext()) {
					FieldObject fieldObject = fieldIerator.next();
					Access access = fieldObject.getAccess();

					if (access == Access.PUBLIC) {
						inheritedFieldCount = inheritedFieldCount + 1;
					}

				}

				inheritFieldMap.put(classObject, inheritedFieldCount);

			}

		}

		CalculateAIF();

	}

	/* Function to compute AIF for a system parsed  */
	private void CalculateAIF() {

		double totalInheritancecount = 0;
		double classInheritanceFactor = 0;

		/* Loop to iterate amongst all inherited fields and calculate inheritance factor */
		for (Map.Entry<ClassObject, Integer> entry : inheritFieldMap.entrySet()) {
			
			double inheritanceFactor = entry.getValue();
			ClassObject classObj = entry.getKey();

			/* check if inheritance factor is not a number  */
			if (!Double.isNaN(inheritanceFactor))
				classInheritanceFactor = (inheritanceFactor / classObj.getFieldList().size());

			/* check if class inheritance factor is not a number  */
			if (!Double.isNaN(classInheritanceFactor))
				totalInheritancecount = totalInheritancecount + classInheritanceFactor;

		}

		System.out.println("*Attribute Inheritance Factor*: " + totalInheritancecount / allclassFieldMap.size());
	}
}
