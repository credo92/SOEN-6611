package view;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import metrics.AttHidingFactor;
import metrics.AttInheritanceFactor;
import metrics.CAMC;
import metrics.CouplingFactor;
import metrics.LCOM;
import metrics.MethodHidingFactor;
import metrics.MethodInheritanceFactor;
import metrics.WMC;
import metrics.Reusability;
import metrics.Understandability;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ast.ASTReader;
import ast.CompilationUnitCache;
import ast.SystemObject;

public class MetricsAction  implements IObjectActionDelegate {
	
	private IWorkbenchPart part;
	private ISelection selection;
	
	private IJavaProject selectedProject;
	private IPackageFragmentRoot selectedPackageFragmentRoot;
	private IPackageFragment selectedPackageFragment;
	private ICompilationUnit selectedCompilationUnit;
	private IType selectedType;
	private IMethod selectedMethod;
	
	public void run(IAction arg0) {
		try {
			CompilationUnitCache.getInstance().clearCache();
			if(selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object element = structuredSelection.getFirstElement();
				if(element instanceof IJavaProject) {
					selectedProject = (IJavaProject)element;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)element;
					selectedProject = packageFragmentRoot.getJavaProject();
					selectedPackageFragmentRoot = packageFragmentRoot;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					selectedProject = packageFragment.getJavaProject();
					selectedPackageFragment = packageFragment;
					selectedPackageFragmentRoot = null;
					selectedCompilationUnit = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit)element;
					selectedProject = compilationUnit.getJavaProject();
					selectedCompilationUnit = compilationUnit;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedType = null;
					selectedMethod = null;
				}
				else if(element instanceof IType) {
					IType type = (IType)element;
					selectedProject = type.getJavaProject();
					selectedType = type;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedMethod = null;
				}
				else if(element instanceof IMethod) {
					IMethod method = (IMethod)element;
					selectedProject = method.getJavaProject();
					selectedMethod = method;
					selectedPackageFragmentRoot = null;
					selectedPackageFragment = null;
					selectedCompilationUnit = null;
					selectedType = null;
				}
				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						if(ASTReader.getSystemObject() != null && selectedProject.equals(ASTReader.getExaminedProject())) {
							new ASTReader(selectedProject, ASTReader.getSystemObject(), monitor);
						}
						else {
							new ASTReader(selectedProject, monitor);
						}
						
						SystemObject system = ASTReader.getSystemObject();
						CAMC camc = new CAMC(system);
						Reusability reusabilityQMOOD = new Reusability();
						Understandability understandabilityCalculator = new Understandability();
						
						System.out.print(camc.toString());
						System.out.println("--------------Reusability---------------------\n");
						reusabilityQMOOD.calculate(system);
						System.out.println("\n -------------Understandability----------------\n");
						understandabilityCalculator.calculate(system);
						System.out.println("\n -------------AHF----------------\n");
						AttHidingFactor ahf = new AttHidingFactor(system);
						System.out.println("\n -------------AIF----------------\n");
						AttInheritanceFactor aif = new AttInheritanceFactor(system);
						System.out.println("\n -------------CF----------------\n");
						CouplingFactor cf =new CouplingFactor(system);
						System.out.println("\n -------------MHF----------------\n");
						MethodHidingFactor mhf = new MethodHidingFactor(system);
						System.out.println("\n -------------MIF----------------\n");
						MethodInheritanceFactor mif = new MethodInheritanceFactor(system);
						
						if(selectedPackageFragmentRoot != null) {
							// package fragment root selected
						}
						else if(selectedPackageFragment != null) {
							// package fragment selected
						}
						else if(selectedCompilationUnit != null) {
							// compilation unit selected
						}
						else if(selectedType != null) {
							// type selected
						}
						else if(selectedMethod != null) {
							// method selected
						}
						else {
							// java project selected
						}
					}
				});
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
	}
}
