package metrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import ast.Access;
import ast.ClassObject;
import ast.FieldInstructionObject;
import ast.FieldObject;
import ast.MethodObject;
import ast.SystemObject;

public class Reusability {
	
	private double couplingCalculation(SystemObject system) {
		Map<String, Integer> couplingMap = new HashMap<String, Integer>();
		
		Set<ClassObject> classes = system.getClassObjects();
		List<String> classTypes  = system.getClassNames();
		
		for (ClassObject classObject : classes) {
			List<String> cls = new ArrayList<String>(classTypes);
			
			cls.remove(classObject.getName());
			int directCoupling = couplingCalculation(classObject, cls);
			couplingMap.put(classObject.getName(), directCoupling);
		}		
		
		double totalCoupling = 0.0;
		for(String key : couplingMap.keySet()) {
			totalCoupling += couplingMap.get(key);
        	System.out.println( key + "  " +  couplingMap.get(key));
        }
		return totalCoupling/classes.size();	
	}
	
	
	private int couplingCalculation(ClassObject classObject, List<String> otherClasses){
		Set<String> directClasses = new HashSet<String>();
		ListIterator<FieldObject> fieldIterator = classObject.getFieldIterator();
		while(fieldIterator.hasNext()) {
			FieldObject field = fieldIterator.next();
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
	
private double cohesionCalculation(SystemObject system) {		
		
		Map<String, Double> cohesionMap = new HashMap<String, Double>();
		Set<ClassObject> classes = system.getClassObjects();
		
		for (ClassObject classObject : classes) {
			double cntCohesion = cohesionCalculation(classObject);
			cohesionMap.put(classObject.getName(), cntCohesion);
		}
		
		double  totalCohesion = 0.0;
		for(String key : cohesionMap.keySet()) {
			totalCohesion += cohesionMap.get(key);
        	System.out.println( key + "  " +  cohesionMap.get(key));
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
	
	double sumIntersection = 0.0;
	
	for (int i = 0; i < lstMethods.size() - 1; i++) {
		List<FieldInstructionObject> lstFieldInstruction = lstMethods.get(i).getFieldInstructions();
		for (FieldInstructionObject param : lstFieldInstruction)
			if (allParameters.contains(param.getType().toString()))
					sumIntersection++;			
	}
	
	return sumIntersection/(lstMethods.size() * allParameters.size());
}

private double messagingCalculation(SystemObject system) {

	Map<String, Integer> messageMap = new HashMap<String, Integer>();

	Set<ClassObject> classes = system.getClassObjects();

	for (ClassObject classObject : classes) {
		int cntPublicMethod = countPublicMessages(classObject);
		messageMap.put(classObject.getName(), cntPublicMethod);

	}
    double noOfClasses = classes.size();
    double totalMessage = 0.0;
    for(String key : messageMap.keySet()) {
    	totalMessage += messageMap.get(key);
    	System.out.println( key + "  " +  messageMap.get(key));
    }
    
	return totalMessage/noOfClasses;
}

private  int countPublicMessages(ClassObject classObject) {
	int result = 0;
	List<MethodObject> methods = classObject.getMethodList();
	for (int i = 0; i < methods.size() - 1; i++) {
		MethodObject method = methods.get(i);
		if (method.getAccess() == Access.PUBLIC)
			result++;
	}
	return result;
}

	
	private double designSizeCalculation(SystemObject system) {
		return system.getClassObjects().size();
	}	
	
	public double calculate(SystemObject system) {
		double cntCoupling   = couplingCalculation(system);
		double cntCohesion   = cohesionCalculation(system);
		double cntMessaging  = messagingCalculation(system);
		double cntDesignSize = designSizeCalculation(system);
		
		System.out.println("\n Results: \n");
		System.out.println("--------------Reusability---------------------\n");
		System.out.println("coupling: " + cntCoupling);
		System.out.println("cohesion: " + cntCohesion);
		System.out.println("messaging: " + cntMessaging);
		System.out.println("designSize: " + cntDesignSize);
		
		double reusabilitycal =
		  -0.25*cntCoupling +
			     0.25*cntCohesion +
			     0.5*cntMessaging + 
			     0.5*cntDesignSize;
		
		System.out.println("*Reusability*:" + reusabilitycal);
		
		return reusabilitycal;
		
	}
}
