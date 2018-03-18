package metrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ast.ASTReader;
import ast.Access;
import ast.ClassObject;
import ast.FieldInstructionObject;
import ast.FieldObject;
import ast.MethodObject;
import ast.SystemObject;
import ast.TypeObject;

public class Understandability {
	
	// Calcualte Abstraction 
	private double abstractionCalculation(SystemObject system) {		
		Map<String, Integer> allFieldMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject classObject : classes) {

			int lengthParent = depthCalculation(classObject, 0);
			
			allFieldMap.put(classObject.getName(), lengthParent);
		}
		
		double noOfClasses = classes.size();
        double totalDepth = 0.0;
        for(String key : allFieldMap.keySet()) {
        	totalDepth += allFieldMap.get(key);
        }
		return totalDepth/noOfClasses;
	}
	
	private int depthCalculation(ClassObject classObject, int depth) {
		TypeObject parentclass = classObject.getSuperclass();
		if(parentclass == null) {
				return depth;
		} else {			
			ClassObject objParent = ASTReader.getSystemObject().getClassObject(parentclass.getClassType());
			if(objParent != null) {				
				return depthCalculation(objParent, depth+1);
			} else {
				return depth;
			}
		}
		
	}
	
	// Calcualte Encapsulation
	private double encapsulationCalculation(SystemObject system) {
		Set<ClassObject> classes = system.getClassObjects();
		double totalRatio = 0.0;
		for (ClassObject objClass : classes) {
			List<FieldObject> lstFields = objClass.getFieldList();
			int fieldSize = lstFields.size();			
			int sizeOfPrivateProtected = countPrivateProtected(objClass);			
			if(fieldSize != 0) {
				double ratio = ((double) sizeOfPrivateProtected)/fieldSize;
				totalRatio += ratio;
			}			
		}
		
        double noOfClasses = classes.size();        
        
		return totalRatio/noOfClasses;
	}
	
	private int countPrivateProtected(ClassObject classObject) {
		int result = 0;
		List<FieldObject> lstFields = classObject.getFieldList();
		for (int i = 0; i < lstFields.size(); i++) {
			FieldObject field = lstFields.get(i);
			if (field.getAccess() == Access.PRIVATE || field.getAccess() == Access.PROTECTED) {
				result++;
			}				
		}
		return result;
	}
	
	private double couplingCalculation(SystemObject system) {
		Map<String, Integer> couplingMap = new HashMap<String, Integer>();
		
		Set<ClassObject> classes = system.getClassObjects();
		List<String> classTypes  = system.getClassNames();
		
		for (ClassObject classObject : classes) {
			List<String> lstCls = new ArrayList<String>(classTypes);			
			lstCls.remove(classObject.getName());
			
			int cntDirectCoupling = couplingCalculation(classObject, lstCls);
			couplingMap.put(classObject.getName(), cntDirectCoupling);
		}		
		
		double totalCoupling = 0.0;
		for(String key : couplingMap.keySet()) {
			totalCoupling += couplingMap.get(key);
        }
		return totalCoupling/classes.size();	
	}	
	
	private int couplingCalculation(ClassObject classObject, List<String> otherClasses){
		Set<String> directClasses = new HashSet<String>();
		ListIterator<FieldObject> lstFieldIterator = classObject.getFieldIterator();
		while(lstFieldIterator.hasNext()) {
			FieldObject field = lstFieldIterator.next();
			if (otherClasses.contains(field.getClassName()))
				directClasses.add(field.getClassName());
		}
			
		List<MethodObject> lstMethods = classObject.getMethodList();
		for (int i = 0; i < lstMethods.size() - 1; i++) {
			List<FieldInstructionObject> lstFieldInstruction = lstMethods.get(i).getFieldInstructions();
			for (FieldInstructionObject param : lstFieldInstruction)
				if(otherClasses.contains(param.getOwnerClass()))
					directClasses.add(param.getOwnerClass());
		}
		return directClasses.size();
	}

	// Calculate Cohesion 
	private double cohesionCalculation(SystemObject system) {
			
			Map<String, Double> cohesionMap = new HashMap<String, Double>();
			Set<ClassObject> classes = system.getClassObjects();
			
			for (ClassObject classObject : classes) {
				double cntCohesion = cohesionCalculation(classObject);
				cohesionMap.put(classObject.getName(), cntCohesion);
			}
			
			double totalCohesion = 0.0;
			for(String key : cohesionMap.keySet()) {
				totalCohesion += cohesionMap.get(key);
	        }
			
			return totalCohesion/classes.size();
		}
			
	private double cohesionCalculation(ClassObject classObject){
			
			List<MethodObject> lstMethods = classObject.getMethodList();
			Set<String> allParameters = new HashSet<String>();
			
			for (int i = 0; i < lstMethods.size() - 1; i++) {
				List<FieldInstructionObject> lstFieldInstruction = lstMethods.get(i).getFieldInstructions();
				for (FieldInstructionObject param : lstFieldInstruction)
					allParameters.add(param.getType().toString());			
			}
			
			if (allParameters.size() == 0)
				return 0;		
			
			double totalIntersection = 0.0;
			
			for (int i = 0; i < lstMethods.size() - 1; i++) {
				List<FieldInstructionObject> lstFieldInstruction = lstMethods.get(i).getFieldInstructions();
				for (FieldInstructionObject param : lstFieldInstruction)
					if (allParameters.contains(param.getType().toString()))
						totalIntersection++;		
			}
			
			return totalIntersection/(lstMethods.size() * allParameters.size());
		}
	
	// Calculate Polymorphism
	private double polymorphismCalculation(SystemObject system) {
		Map<String, Integer> allMethodMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject objClass : classes) {
			int cntAbstractMethod = abstractMethodCalculation(objClass);
			allMethodMap.put(objClass.getName(), cntAbstractMethod);
		}
		
        int totalAbstractMethods = 0;
        for(String key : allMethodMap.keySet()) {
        	totalAbstractMethods += allMethodMap.get(key);
        }        
        
		return totalAbstractMethods;
	}
	
	private int abstractMethodCalculation(ClassObject classObject) {
		List<MethodObject> lstMethods = classObject.getMethodList();
		int cntAbstractMethods = 0;
		for (int i = 0; i < lstMethods.size() - 1; i++) {
			if(lstMethods.get(i).isAbstract()) {
				cntAbstractMethods++;
			}		
		}
		return cntAbstractMethods;
	}
	
	// Calculate Complexity
	private double complexityCalculation(SystemObject system) {
		Map<String, Integer> allMethodMap = new HashMap<String, Integer>();

		Set<ClassObject> classes = system.getClassObjects();

		for (ClassObject classObject : classes) {
			List<MethodObject> lstMethods = classObject.getMethodList();
			int cntMethod = lstMethods.size();
			allMethodMap.put(classObject.getName(), cntMethod);
		}
		
        double noOfClasses = classes.size();
        double totalMethods = 0.0;
        for(String key : allMethodMap.keySet()) {
        	totalMethods += allMethodMap.get(key);   
        }
        
		return totalMethods/noOfClasses;
	}
	
	// Calculate Design Size
	private double designSizeCalculation(SystemObject system) {
		return system.getClassObjects().size();
	}

		
	public double calculate(SystemObject system) {
		
		double abstraction   = abstractionCalculation(system);
		double encapsulation   = encapsulationCalculation(system);
		double coupling   = couplingCalculation(system);
		double cohesion   = cohesionCalculation(system);
		double polymorphism  = polymorphismCalculation(system);
		double complexity  = complexityCalculation(system);
		double designsize = designSizeCalculation(system);
			
			System.out.println("\n Abstraction: " + abstraction);
			System.out.println("Encapsulation: " + encapsulation);
			System.out.println("Polymorphism: " + polymorphism);
			System.out.println("Complexity: " + complexity);

			double understandabilitycal =
					-0.33*abstraction +
				     0.33*encapsulation -
				     0.33*coupling +
				     0.33*cohesion -
				     0.33*polymorphism -
				     0.5*complexity + 
				     0.5*designsize;		
		
					System.out.println("*Understandability*: " + understandabilitycal);	
					
					return understandabilitycal;		
		}

}
