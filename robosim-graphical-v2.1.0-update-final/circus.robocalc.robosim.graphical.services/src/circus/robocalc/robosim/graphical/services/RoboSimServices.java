package circus.robocalc.robosim.graphical.services;

import static org.eclipse.xtext.xbase.lib.IterableExtensions.filter;
import static org.eclipse.xtext.xbase.lib.IterableExtensions.map;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeContainerSpec;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeListSpec;
import org.eclipse.sirius.diagram.business.internal.metamodel.spec.DNodeSpec;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.xtext.EcoreUtil2;

import circus.robocalc.robochart.Action;
import circus.robocalc.robochart.Assignable;
import circus.robocalc.robochart.BasicContext;
import circus.robocalc.robochart.Call;
import circus.robocalc.robochart.CallExp;
import circus.robocalc.robochart.Clock;
import circus.robocalc.robochart.ClockReset;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.DuringAction;
import circus.robocalc.robochart.EntryAction;
import circus.robocalc.robochart.Enumeration;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.ExitAction;
import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Interface;
import circus.robocalc.robochart.Literal;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.Node;
import circus.robocalc.robochart.NodeContainer;
import circus.robocalc.robochart.Operation;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RCPackage;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.RoboticPlatformRef;
import circus.robocalc.robochart.Skip;
import circus.robocalc.robochart.State;
import circus.robocalc.robochart.StateMachine;
import circus.robocalc.robochart.StateMachineDef;
import circus.robocalc.robochart.StateMachineRef;
import circus.robocalc.robochart.Statement;
import circus.robocalc.robochart.StringExp;
import circus.robocalc.robochart.Transition;
import circus.robocalc.robochart.TypeDecl;
import circus.robocalc.robochart.TypeRef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableList;
import circus.robocalc.robochart.graphical.services.ParseResult;
import circus.robocalc.robochart.graphical.services.RoboChartServices;
import circus.robocalc.robosim.CycleExp;
import circus.robocalc.robosim.ExecStatement;
import circus.robocalc.robosim.InputContext;
import circus.robocalc.robosim.OutputCommunication;
import circus.robocalc.robosim.OutputContext;
import circus.robocalc.robosim.RoboSimFactory;
import circus.robocalc.robosim.SimCall;
import circus.robocalc.robosim.SimControllerDef;
import circus.robocalc.robosim.SimMachineDef;
import circus.robocalc.robosim.SimModule;
import circus.robocalc.robosim.SimOperationDef;
import circus.robocalc.robosim.SimRefExp;
import circus.robocalc.robosim.SimVarRef;
import circus.robocalc.robosim.graphical.label.roboSimLabel.ActionInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.EventInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.ExpressionInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.Label;
import circus.robocalc.robosim.graphical.label.roboSimLabel.LabelModel;
import circus.robocalc.robosim.graphical.label.roboSimLabel.OperationInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.SimControllerDefInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.SimMachineDefInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.SimModuleInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.SimTransitionInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.StatementInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.VariableInContext;

 
//import circus.robocalc.graphical.robosim.ReadingServices;
/**
 * The services class used by VSM.
 */
public class RoboSimServices extends RoboChartServices {
	
	public boolean isRoboSimModel(EObject self) {
		Resource r = self.eResource();
		return "rst".equals(r.getURI().fileExtension().toLowerCase());
	}
	
	public EObject deleteSimNode(Node e) {
		return super.deleteNode(e);
	}

	public EList<EObject> test(EObject o) {
		System.out.println("Param: " + o);
		return new BasicEList<EObject>();
	}


	private Label getLabel(Resource r) {
		if (r.getContents().size() == 0) {
			System.out.println("Label produced empty resource");
			throw new RuntimeException("Label produced empty resource");
		}
			EObject o = r.getContents().get(0);
		if (!(o instanceof LabelModel)) 
			throw new RuntimeException("Root of label resource is not a LabelModel");
		LabelModel lm = (LabelModel) o;
		if (lm.getLabel() != null) 
			return lm.getLabel();
		else 
			throw new RuntimeException("LabelModel does not contain a label");
	}


	public void setExpression(String value, Consumer<Expression> f) {
		f.accept(null);
	}
	//new
	public void renameSimModule(SimModule o, String name, String cycle) {
		o.setName(name);
		setExpression(cycle, o::setCycleDef);
	}
	
	
	public void renameSimMachine(SimMachineDef o, String name, String cycle) {
		o.setName(name);
		setExpression(cycle, o::setCycleDef);
	}
	//new
	public void renameSimController(SimControllerDef o, String name, String cycle) {
		o.setName(name);
		setExpression(cycle, o::setCycleDef);
	}

	public String newSimRoboticPlatformDefName(EObject o) {
		return super.newRoboticPlatformDefName(o);
	}

	//new
	public EList<SimMachineDef> getSimMachineDefs(EObject o) {
		return dispatchOnModuleAndPackage(o, this::getSimMachineDefs, this::getSimMachineDefs);
	}
	
	public EList<SimControllerDef> getSimControllerDefs2(EObject o) {
		return dispatchOnModuleAndPackage(o, this::getSimControllerDefs2, this::getSimControllerDefs2);
	}

	public EList<SimMachineDef> getSimMachineDefs(RCModule o) {
		EList<SimMachineDef> defs = new BasicEList<SimMachineDef>();
		for (SimMachineDef m : filter(o.getNodes(), SimMachineDef.class)) {
			defs.add(m);
		}
		return defs;
	}
	
	//new
	public EList<SimMachineDef> getSimMachineDefs(SimModule o) {
		EList<SimMachineDef> defs = new BasicEList<SimMachineDef>();
		for (SimMachineDef m : filter(o.getNodes(), SimMachineDef.class)) {
			defs.add(m);
		}
		return defs;
	}


	public EList<SimMachineDef> getRCPackageSimMachineDefs(EObject o) {
		if (o == null) {
			return null;
		}
		if (o instanceof RCPackage) {
			return getSimMachineDefs((RCPackage) o);
		} else {
			return getRCPackageSimMachineDefs(o.eContainer());
		}
	}
	
	//new
	public EList<SimControllerDef> getRCPackageSimControllerDefs(EObject o) {
		if (o == null) {
			return null;
		}
		if (o instanceof RCPackage) {
			return getSimControllerDefs2((RCPackage) o);
		} else {
			return getRCPackageSimControllerDefs(o.eContainer());
		}
	}

	public EList<RoboticPlatformDef> getSimRoboticPlatformDefs(RCPackage o) {
		EList<RoboticPlatformDef> list = new BasicEList<RoboticPlatformDef>();
		for (RoboticPlatformDef rpd : filter(o.getRobots(), RoboticPlatformDef.class)) {
			list.add(rpd);
		}
		return list;
	}

	public EList<RoboticPlatformDef> getSimRoboticPlatformDefs(RCModule o) {
		return super.getRoboticPlatformDefs(o);
	}
	
	//new
	public EList<RoboticPlatformDef> getSimRoboticPlatformDefs(SimModule o) {
		return super.getRoboticPlatformDefs(o);
	}

//	public boolean simCanConnect(Connection c, DSemanticDecorator source, DSemanticDecorator target) {
//		ConnectionNode sobj = c.getFrom();
//		ConnectionNode tobj = c.getTo();
//
//		EObject selem = simAssociatedElement(source.eContainer());
//		EObject telem = simAssociatedElement(target.eContainer());
//
//		boolean b = sobj != null && sobj == selem & tobj != null && tobj == telem;
//		return b;
//	}

	//new version SimController and SimMachine
	private EObject obtainTarget(EObject target) {
		if (target instanceof NamedElement)
			return ((NamedElement) target);
		else if (target instanceof SimControllerDef)
			return ((SimControllerDef) target);
		else if (target instanceof SimModule)
			return ((SimModule) target);
		else if (target instanceof ControllerRef)
			return ((ControllerRef) target);
		else if (target instanceof SimMachineDef)
			return ((SimMachineDef) target);  
		else if (target instanceof StateMachineRef)
			return ((StateMachineRef) target);
		else if(target instanceof InputContext)
			 return ((InputContext)target);
		else if(target instanceof OutputContext)
			 return ((OutputContext)target);
		else	return null;
	}

	public EObject simAssociatedElement(EObject o) {
		if (o instanceof DNodeListSpec) {
			DNodeListSpec node = (DNodeListSpec) o;
			EObject target = node.basicGetTarget();
			return obtainTarget(target);

		} else if (o instanceof DNodeContainerSpec) {
			DNodeContainerSpec node = (DNodeContainerSpec) o;
			EObject target = node.basicGetTarget();
			return obtainTarget(target);
		} else if (o instanceof DNodeSpec) {
			DNodeSpec node = (DNodeSpec) o;
			EObject target = node.basicGetTarget();
			return obtainTarget(target);
		} else {
			return null;
		}
	}

	public EList<Event> allSimEvents(ControllerDef context) {
		EList<Event> events = new BasicEList<Event>();
		events.addAll(context.getEvents());
		for (Interface i : context.getInterfaces())
			events.addAll(i.getEvents());
		return events;
	}
	
	public EList<Event> allSimEvents(SimControllerDef context) {
		EList<Event> events = new BasicEList<Event>();
		events.addAll(context.getEvents());
		for (Interface i : context.getInterfaces())
			events.addAll(i.getEvents());
		return events;
	}

	public EList<Event> allSimEvents(SimMachineDef context) {
		EList<Event> events = new BasicEList<Event>();
		 events.addAll(context.getEvents());
		for (Interface i : context.getInterfaces())
			events.addAll(i.getEvents());
		for (Interface i : context.getInputContext().getInterfaces())
			events.addAll(i.getEvents());
		for (Event ev : context.getInputContext().getEvents())
			events.add(ev);
		for (Interface i : context.getOutputContext().getInterfaces())
			events.addAll(i.getEvents());
		for (Event ev : context.getOutputContext().getEvents())
			events.add(ev);
		
		return events;
	}
	
	public EList<Event> allSimEvents(SimOperationDef context) {
		EList<Event> events = new BasicEList<Event>();
		 events.addAll(context.getEvents());
		for (Interface i : context.getInterfaces())
			events.addAll(i.getEvents());
		for (Interface i : context.getInputContext().getInterfaces())
			events.addAll(i.getEvents());
		for (Event ev : context.getInputContext().getEvents())
			events.add(ev);
		for (Interface i : context.getOutputContext().getInterfaces())
			events.addAll(i.getEvents());
		for (Event ev : context.getOutputContext().getEvents())
			events.add(ev);
		
		return events;
	}

	public EList<Event> allSimEvents(EObject context) {
		EList<Event> events = new BasicEList<Event>();
		if (context instanceof Context) {
			
			events.addAll(((Context) context).getEvents());
			
			
			for (Interface i : ((Context) context).getInterfaces())
				events.addAll(i.getEvents());
			/*
			 * for (Interface i : ((Context) s).getRInterfaces())
			 * events.addAll(i.getEvents());
			 */
			return events;
		}
			else if (context instanceof SimControllerDef) {
			return allSimEvents((SimControllerDef) context);
		} else if (context instanceof ControllerRef) {
			return allSimEvents(((ControllerRef) context).getRef());
		} else if (context instanceof SimMachineDef) {

			for (Interface i : ((SimMachineDef) context).getInterfaces())
				events.addAll(i.getEvents());
			for (Interface i : ((SimMachineDef) context).getInputContext().getInterfaces())
				events.addAll(i.getEvents());
			for (Event ev : ((SimMachineDef) context).getInputContext().getEvents())
				events.add(ev);
			for (Interface i : ((SimMachineDef) context).getOutputContext().getInterfaces())
				events.addAll(i.getEvents());
			for (Event ev : ((SimMachineDef) context).getOutputContext().getEvents())
				events.add(ev);
			
			return events;
			//return allSimEvents((SimMachineDef) context);
		} else if (context instanceof StateMachineRef) {
			StateMachineDef stm = ((StateMachineRef) context).getRef();
			return allSimEvents((SimMachineDef) stm);		
			//return allSimEvents(((StateMachineRef) context).getRef());
		}
		else if(context instanceof SimModule){
			return allSimEvents(getRoboticPlatform((SimModule)context));
		}
		else if (context instanceof SimOperationDef) {
			return allSimEvents((SimOperationDef)context);
		}
		return allEvents(context);
	}
	
	@Override
	public RoboticPlatformDef getRoboticPlatform(RCModule m) { 
		for (ConnectionNode n: m.getNodes()) {
			if (n instanceof RoboticPlatformDef) {
				return ((RoboticPlatformDef)n);
			} else if (n instanceof RoboticPlatformRef) {
				return ((RoboticPlatformRef)n).getRef();
			}
		}
		return null;
	}

	private <T> EList<T> iterableToEList(Iterable<T> iter) {
		EList<T> r = new BasicEList<T>();
		iter.forEach((sm) -> r.add(sm));
		return r;
	}

	private <T> EList<T> dispatchOnModuleAndPackage(EObject context, Function<RCModule, EList<T>> m,
			Function<RCPackage, EList<T>> p) {
		if (context instanceof RCModule) {
			return m.apply((RCModule) context);
		} else if (context instanceof RCPackage) {
			return p.apply((RCPackage) context);
		} else {
			return null;
		}
	}

	//new
	public EList<SimModule> getSimModule(EObject context) {
		return dispatchOnModuleAndPackage(context, this::getSimModule, this::getSimModule);
	}

	public EList<ControllerDef> getControllerDefs(RCModule context) {
		Iterable<ControllerDef> c = map(filter(context.getNodes(), (n) -> n instanceof ControllerDef),
				(n) -> (ControllerDef) n);
		return iterableToEList(c);
	}

	public EList<ControllerDef> getControllerDefs(RCPackage context) {
		return iterableToEList(filter(context.getControllers(), ControllerDef.class));
	}

	public EList<SimMachineDef> getSimMachineDefs(ControllerDef context) {
		Iterable<SimMachineDef> c = map(filter(context.getMachines(), (n) -> n instanceof SimMachineDef),
				(n) -> (SimMachineDef) n);
		return iterableToEList(c);
	}

	public EList<SimMachineDef> getSimMachineDefs(RCPackage context) {
		EList<SimMachineDef> machines = iterableToEList(filter(context.getMachines(), SimMachineDef.class));
		return machines;
	}

	public EList<StateMachineRef> getSimMachineRefs(ControllerDef context) {
		return iterableToEList(
				map(filter(context.getMachines(), (m) -> m instanceof StateMachineRef), (m) -> (StateMachineRef) m));
	}

	public EList<StateMachineRef> getSimMachineRefs(RCPackage context) {
		return new BasicEList<StateMachineRef>();
	}

	public String printSimMachine(SimMachineDef o) {
		
		//return String.format("%s [cycleDef = (%s)]", o.getName(), print(o.getCycleDef()));
		return String.format("%s [cycleDef = %s]", o.getName(), print(o.getCycleDef()));
		//return "";
	}
	
	public String printSimModule(SimModule o) {
		return String.format("%s [cycleDef = %s]", o.getName(), print(o.getCycleDef()));
	}
	
	public String printSimController(SimControllerDef o) {
		return String.format("%s [cycleDef = %s]", o.getName(), print(o.getCycleDef()));
	}

	
//	public String printExp(Expression e) {
//		try {
//			if (e instanceof ResultExp) {
//				return "result";
//			}
//			if (e instanceof CycleExp) {
//				return "cycle";
//			}
//			if (e instanceof Forall) {
//				Forall i = (Forall) e;
//				String s = "forall ";
//				Iterator<Variable> it = i.getVariables().iterator();
//				if (it.hasNext()) {
//					Variable v = it.next();
//					s += v.getName() + ": " + printType(v.getType());
//				}
//				while (it.hasNext()) {
//					Variable v = it.next();
//					s += ", " + v.getName() + ": " + printType(v.getType());
//				}
//				if (i.getSuchthat() != null)
//					s += " | " + print(i.getSuchthat());
//				if (i.getPredicate() != null)
//					s += " @ " + print(i.getPredicate());
//				return s;
//			} else if (e instanceof Exists) {
//				Exists i = (Exists) e;
//				String s = "";
//				if (i.isUnique())
//					s = "exists1 ";
//				else
//					s = "exists ";
//				Iterator<Variable> it = i.getVariables().iterator();
//				if (it.hasNext()) {
//					Variable v = it.next();
//					s += v.getName() + ": " + printType(v.getType());
//				}
//				while (it.hasNext()) {
//					Variable v = it.next();
//					s += ", " + v.getName() + ": " + printType(v.getType());
//				}
//				if (i.getSuchthat() != null)
//					s += " | " + print(i.getSuchthat());
//				if (i.getPredicate() != null)
//					s += " @ " + print(i.getPredicate());
//				return s;
//			} else if (e instanceof LambdaExp) {
//				LambdaExp i = (LambdaExp) e;
//				String s = "lambda ";
//				Iterator<Variable> it = i.getVariables().iterator();
//				if (it.hasNext()) {
//					Variable v = it.next();
//					s += v.getName() + ": " + printType(v.getType());
//				}
//				while (it.hasNext()) {
//					Variable v = it.next();
//					s += ", " + v.getName() + ": " + printType(v.getType());
//				}
//				if (i.getSuchthat() != null)
//					s += " | " + print(i.getSuchthat());
//				if (i.getExpression() != null)
//					s += " @ " + print(i.getExpression());
//				return s;
//			} else if (e instanceof DefiniteDescription) {
//				DefiniteDescription i = (DefiniteDescription) e;
//				String s = "the ";
//				Iterator<Variable> it = i.getVariables().iterator();
//				if (it.hasNext()) {
//					Variable v = it.next();
//					s += v.getName() + ": " + printType(v.getType());
//				}
//				while (it.hasNext()) {
//					Variable v = it.next();
//					s += ", " + v.getName() + ": " + printType(v.getType());
//				}
//				if (i.getSuchthat() != null)
//					s += " | " + print(i.getSuchthat());
//				if (i.getExpression() != null)
//					s += " @ " + print(i.getExpression());
//				return s;
//			} else if (e instanceof LetExpression) {
//				LetExpression i = (LetExpression) e;
//				String s = "let ";
//				Iterator<Declaration> it = i.getDeclarations().iterator();
//				if (it.hasNext()) {
//					Declaration d = it.next();
//					s += d.getName() + " == " + print(d.getValue());
//				}
//				while (it.hasNext()) {
//					Declaration d = it.next();
//					s += ", " + d.getName() + " == " + print(d.getValue());
//				}
//				if (i.getExpression() != null)
//					s += " @ " + print(i.getExpression());
//				return s;
//			} else if (e instanceof IfExpression) {
//				// don't need to check for existence of else because it is mandatory for expressions
//				IfExpression i = (IfExpression) e;
//				return "if " + print(i.getCondition()) + " then " + print(i.getIfexp()) + " else "
//						+ print(i.getElseexp()) + " end";
//			} else if (e instanceof InExp) {
//				InExp i = (InExp) e;
//				return print(i.getMember()) + " in " + print(i.getSet());
//			} else if (e instanceof SetComp) {
//				SetComp i = (SetComp) e;
//				String s = "{ ";
//				Iterator<Variable> it = i.getVariables().iterator();
//				if (it.hasNext()) {
//					Variable v = it.next();
//					s += v.getName() + ": " + printType(v.getType());
//				}
//				while (it.hasNext()) {
//					Variable v = it.next();
//					s += ", " + v.getName() + ": " + printType(v.getType());
//				}
//				if (i.getPredicate() != null)
//					s += " | " + print(i.getPredicate());
//				if (i.getExpression() != null)
//					s += " @ " + print(i.getExpression());
//				s += "}";
//				return s;
//			}
//
//			else if (e instanceof Iff) {
//				Iff i = (Iff) e;
//				return print(i.getLeft()) + "<=>" + print(i.getRight());
//			} else if (e instanceof Implies) {
//				Implies i = (Implies) e;
//				return print(i.getLeft()) + "=>" + print(i.getRight());
//			} else if (e instanceof Or) {
//				Or i = (Or) e;
//				return print(i.getLeft()) + "\\/" + print(i.getRight());
//			} else if (e instanceof And) {
//				And i = (And) e;
//				return print(i.getLeft()) + "/\\" + print(i.getRight());
//			} else if (e instanceof Not) {
//				Not i = (Not) e;
//				return "not " + print(i.getExp());
//			} else if (e instanceof Equals) {
//				Equals i = (Equals) e;
//				return print(i.getLeft()) + "==" + print(i.getRight());
//			} else if (e instanceof Different) {
//				Different i = (Different) e;
//				return print(i.getLeft()) + "!=" + print(i.getRight());
//			} else if (e instanceof GreaterThan) {
//				GreaterThan i = (GreaterThan) e;
//				return print(i.getLeft()) + ">" + print(i.getRight());
//			} else if (e instanceof GreaterOrEqual) {
//				GreaterOrEqual i = (GreaterOrEqual) e;
//				return print(i.getLeft()) + ">=" + print(i.getRight());
//			} else if (e instanceof LessThan) {
//				LessThan i = (LessThan) e;
//				return print(i.getLeft()) + "<" + print(i.getRight());
//			} else if (e instanceof LessOrEqual) {
//				LessOrEqual i = (LessOrEqual) e;
//				return print(i.getLeft()) + "<=" + print(i.getRight());
//			} else if (e instanceof Plus) {
//				Plus i = (Plus) e;
//				return print(i.getLeft()) + "+" + print(i.getRight());
//			} else if (e instanceof Minus) {
//				Minus i = (Minus) e;
//				return print(i.getLeft()) + "-" + print(i.getRight());
//			} else if (e instanceof Mult) {
//				Mult i = (Mult) e;
//				return print(i.getLeft()) + "*" + print(i.getRight());
//			} else if (e instanceof Div) {
//				Div i = (Div) e;
//				return print(i.getLeft()) + "/" + print(i.getRight());
//			} else if (e instanceof Cat) {
//				Cat i = (Cat) e;
//				return print(i.getLeft()) + " cat " + print(i.getRight());
//			} else if (e instanceof Neg) {
//				Neg i = (Neg) e;
//				return "-" + print(i.getExp());
//			} else if (e instanceof Selection) {
//				Selection i = (Selection) e;
//				String v = print(i.getReceiver());
//				v += ".";
//				v += i.getMember().getName();
//				return v;
//			} else if (e instanceof BooleanExp) {
//				return ((BooleanExp) e).getValue();
//			} else if (e instanceof IntegerExp) {
//				IntegerExp i = (IntegerExp) e;
//				return "" + i.getValue();
//			} else if (e instanceof FloatExp) {
//				FloatExp i = (FloatExp) e;
//				return "" + i.getValue();
//			} else if (e instanceof StringExp) {
//				StringExp i = (StringExp) e;
//				return "\""+i.getValue()+"\"";
//			} else if (e instanceof EnumExp) {
//				EnumExp i = (EnumExp) e;
//				String v = i.getType().getName();
//				v += "::";
//				v += i.getLiteral().getName();
//				return v;
//			} else if (e instanceof VarExp) {
//				VarExp i = (VarExp) e;
//				return i.getValue().getName();
//			} 
//			//	else if (e instanceof CallExp) {
////				if (((CallExp) e).getFunction() instanceof RefExp && ((RefExp)((CallExp) e).getFunction()).getRef() instanceof Function) {
////					CallExp i = (CallExp) e;
////					Function f = (Function)((RefExp)((CallExp) e).getFunction()).getRef();
////					String v = f.getName();
////					v += "(";
////					int n = 0;
////					for (Expression ex : i.getArgs()) {
////						n++;
////						if (n == i.getArgs().size()) {
////							v += print(ex);
////						} else {
////							v += print(ex) + ", ";
////						}
////					}
////					v += ")";
////					return v;
////				} else throw new RuntimeException("Printing services do not yet support other types of callees");
////			} 
//			else if (e instanceof ParExp) {
//				ParExp i = (ParExp) e;
//				return "(" + print(i.getExp()) + ")";
//			} else if (e instanceof SeqExp) {
//				SeqExp i = (SeqExp) e;
//				String s = "<";
//				Iterator<Expression> it = i.getValues().iterator();
//				if (it.hasNext()) {
//					Expression o = it.next();
//					s += print(o);
//				}
//				while (it.hasNext()) {
//					Expression o = it.next();
//					s += ", " + print(o);
//				}
//				s += ">";
//				return s;
//			} else if (e instanceof SetExp) {
//				SetExp i = (SetExp) e;
//				String s = "{";
//				Iterator<Expression> it = i.getValues().iterator();
//				if (it.hasNext()) {
//					Expression o = it.next();
//					s += print(o);
//				}
//				while (it.hasNext()) {
//					Expression o = it.next();
//					s += ", " + print(o);
//				}
//				s += "}";
//				return s;
//			} else if (e instanceof SetRange) {
//				SetRange i = (SetRange) e;
//				String s = "{";
//				s += print(i.getStart());
//				s += " to ";
//				s += print(i.getEnd());
//				s += "}";
//				return s;
//			} else if (e instanceof TupleExp) {
//				TupleExp i = (TupleExp) e;
//				String s = "(|";
//				Iterator<Expression> it = i.getValues().iterator();
//				if (it.hasNext()) {
//					Expression o = it.next();
//					s += print(o);
//				}
//				while (it.hasNext()) {
//					Expression o = it.next();
//					s += ", " + print(o);
//				}
//				s += "|)";
//				return s;
//			} else if (e instanceof ClockExp) {
//				Clock instant = ((ClockExp) e).getClock();
//				if (instant != null) {
//					return "since(" + instant.getName() + ")";
//				} else {
//					return null;
//				}
//			} else if (e instanceof RangeExp) {
//				RangeExp i = (RangeExp) e;
//				return i.getLinterval() + print(i.getLrange()) + "," + print(i.getRrange()) + i.getRinterval();
//			} else if (e instanceof StateClockExp) {
//				State state = ((StateClockExp) e).getState();
//				StateMachineDef stm = getStateMachineDef(state);
//				if (stm == null) return null;
//				String name = state.getName();
//				int n = numberOfStates(stm, state.getName());
//				if (n > 1) {
//					name = fullName(state);
//				}
//				if (state != null) {
//					return "sinceEntry(" + name + ")";
//				} else {
//					return null;
//				}
//			} else if (e instanceof RefExp) {
//				NamedExpression n = ((RefExp) e).getRef();
//				if (n instanceof Literal) {
//					Enumeration en = (Enumeration) ((Literal) n).eContainer();
//					return en.getName() + "::" + ((Literal) n).getName();
//				} else if (n instanceof Variable) {
//					return ((Variable) n).getName();
//				} else
//					return null;
//			} else if (e instanceof ArrayExp) {
//				ArrayExp i = (ArrayExp) e;
//				String s = print(i.getValue()) + "[";
//				s += print(i.getParameters().get(0));
//				int j = 1;
//				while (j < i.getParameters().size()) {
//					s += "," + print(i.getParameters().get(j));
//					j++;
//				}
//				s += "]";
//				return s;
//			} else if (e instanceof ElseExp) {
//				return "else";
//			} else if (e instanceof Modulus) {
//				Modulus i = (Modulus) e;
//				return print(i.getLeft()) + "%" + print(i.getRight());
//			}
//
//			else {
//				return null;
//			}
//		} catch (Exception ex) {
//			return null;
//		}
//	}
   
	
//MSCF	
	@Override
	public String print(Expression exp) {

			if (exp instanceof CycleExp) {
			return "cycle";
		    }
			else if(exp instanceof SimRefExp){
				return print((SimRefExp) exp);
			}
			
//			else if (exp instanceof VarRef){
//				return printVarRef(exp);
//			}
		return super.print(exp);
	}

	public String printSimMachineRef(StateMachineRef e) {
		if (e.getRef().getName()!=null)
			return "ref " + e.getRef().getName();
		return "null";

	}


	//Check the following methods in the new version of the tool
	@Override
	public String printStateMachineRef(StateMachineRef e) {
		if (e.getRef().getName()!=null)
			return "ref " + e.getRef().getName();
		return "null";


	}
//	
	@Override
	public String printControllerRef(ControllerRef r) {
		if (r.getRef().getName()!=null)
			return "ref " + r.getRef().getName();
		return "null";
}
	
//	@Override
//	public String printStateMachineRef(StateMachineRef e) {
//		NamedElement o1 = e;
//		NamedElement o2 = e.getRef();
//		EObject l = lca(o1,o2);
//		String s = e.getRef().getName();
//		EObject o = e.getRef();
//		while (o.eContainer() != null && o.eContainer() != l) {
//			o = o.eContainer();
//			if (o instanceof RCPackage && ((RCPackage) o).getName() != null) {
//				s = ((RCPackage) o).getName() + "::" + s;
//			}
//			if (o instanceof NamedElement) {
//				s = ((NamedElement) o).getName() + "::" + s;
//			}
//		}
//		return "ref "+s;
//	}
	
//	@Override
//	public String printControllerRef(ControllerRef e) {
//		NamedElement o1 = e;
//		NamedElement o2 = e.getRef();
//		EObject l = lca(o1,o2);
//		String s = e.getRef().getName();
//		EObject o = e.getRef();
//		while (o.eContainer() != null && o.eContainer() != l) {
//			o = o.eContainer();
//			if (o instanceof RCPackage && ((RCPackage) o).getName() != null) {
//				s = ((RCPackage) o).getName() + "::" + s;
//			}
//			if (o instanceof NamedElement) {
//				s = ((NamedElement) o).getName() + "::" + s;
//			}
//		}
//		return "ref "+s;
//	}

	
	public EList<Clock> allSimClocks(EObject o) {
		if (o instanceof SimMachineDef) {
			return ((SimMachineDef) o).getClocks();
		} else if (o instanceof OperationDef) {
			return ((OperationDef) o).getClocks();
		} else
			return null;
	}
	
	
	
	
//	@Override
//	public String printTransition(Transition t) {
//		if (t != null) {
//			System.out.println("test transition");
//		}
//		
//		return "";
//	}
	  

	public String printSimTransition(Transition t) {
		if (t != null) {
			String value = "";
			
			EList<ClockReset> resets = t.getReset();
			Expression cond = t.getCondition();
			Statement a = t.getAction();
			
			Expression probability = t.getProbability();

			

			if (probability != null) {
				value += "p{" + print(probability) + "}";
			}
			
			if (t.getTrigger()!=null) {
				value += "exec";
			}
			
			
			if (resets != null) {
				for (ClockReset c : resets) {
					Clock timer = c.getClock();
					if (timer != null) {
						value += " #" + timer.getName();
					}
				}
			}

			if (cond != null) {
				try {
					String v = print(cond);
					value += " [" + v + "]";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (a != null) {
				value += "/" + print(a);
			}
			return value;
		}
		return null;

	}

	public String printSimAction(Action a) {
		if (a !=  null) {

			if (a instanceof EntryAction) {
				return "entry " + print(a.getAction());
			} else if (a instanceof DuringAction) {
				return "during " + print(a.getAction());
			} else if (a instanceof ExitAction) {
				return "exit " + print(a.getAction());
			}
				
		}
		return null;
			
		}

//	public String print(CommandOperationCall e) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("$");
//		sb.append(e.getOperation().getName());
//		sb.append("(");
//		String separator = "";
//		for (Expression exp : e.getArgs()) {
//			sb.append(separator);
//			sb.append(print(exp));
//			separator = ", ";
//		}
//		sb.append(")");
//		return sb.toString();
//	}
	
//OperationCall => SimCall
	public String print(SimCall e) {
		StringBuilder sb = new StringBuilder();
		sb.append("$");
		sb.append(e.getOperation().getName());
		sb.append("(");
		String separator = "";
		for (Expression exp : e.getArgs()) {
			sb.append(separator);
			sb.append(print(exp));
			separator = ", ";
		}
		sb.append(")");
		return sb.toString();
	}
	
	//public String print(String t){
	//	return t;
	//}

	//OutputCommunication => SimRef
//	public String print(SimRefExp e) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("$");
//		sb.append(e.getElement().getName());
//		if (e.getValue() != null) {
//			sb.append("!");
//			//String st = e.getEvent().getName();
//			sb.append(print(e.getValue()));
//			//sb.append(print(st));
//		}
//		return sb.toString();
//	}
//	
	//OutputCommunication 
	public String print(OutputCommunication e) {
		StringBuilder sb = new StringBuilder();
		sb.append("$");
		sb.append(e.getEvent().getName());
		if (e.getValue() != null) {
			sb.append("!");
			//String st = e.getEvent().getName();
			sb.append(print(e.getValue()));
			//sb.append(print(st));
		}
		return sb.toString();
	}


	@Override
	public String print(Statement e) {
		if (e instanceof SimCall) {
			return print((SimCall) e);
		} else
			
		if (e instanceof OutputCommunication) {
			return print((OutputCommunication) e);
		}
		if (e instanceof ExecStatement) {
			return print((ExecStatement)e );
		}
		return super.print(e);
	}
	
	@Override
	public String print(Assignable e){
		if (e instanceof SimVarRef){
			return print((SimVarRef) e);
			
		}
		
//		if (e instanceof VarRef){
//			return print((VarRef) e);
//			
//		}
		
		return super.print(e);
	}
	
	
public String print(ExecStatement e) {
		
	StringBuilder sb = new StringBuilder();
    sb.append("exec");
	
    return sb.toString();
	}


//InputCommunication => SimRefExp
	public String print(SimRefExp e) {
		
		//SimRefExp e = (SimRefExp) exp;
		StringBuilder sb = new StringBuilder();

		Expression pred = e.getPredicate();
		sb.append("$");
		sb.append(e.getElement().getName());
		
		if (e.getVariable() != null) {
			sb.append("?");
			sb.append(e.getVariable().getName());
		} else if (e.getExp() != null) {
			sb.append(".");
			sb.append(print(e.getExp()));
		}
		//System.out.println(e.getPredicate().toString());
		
		
		if (pred != null){
			sb.append(print(pred));
		}


		return sb.toString();
	}
	

	
public String print(SimVarRef e) {
		
	StringBuilder sb = new StringBuilder();

       Variable v = (Variable) e.getName();
		sb.append("$");
		//System.out.println(v.getName());
		sb.append(v.getName());

		return sb.toString();
	}




	
	/*
	 * Creation methods for RoboSim elements
	 */
	
	private void alert(String title, String message) {
		Shell shell = Display.getCurrent().getActiveShell();
		MessageDialog dlg = new MessageDialog(shell, title, null, message, MessageDialog.WARNING, 0, "OK");
	}
	
	@Override
	public String getContext(EObject d) {
		if (d == null)
			return null;
		if (d instanceof Context) {
			return getFullName(d);
		}
		if (d instanceof RCPackage) {
			for (EObject c : d.eContents()) {
				if (c instanceof NamedElement) {
					return getFullName(c);
				}
			}
			return null;
		}
		if (d.eContainer() != null) {
			return getContext(d.eContainer());
		}
		return null;
	}
	
	
	
	public InputContext inputContext(EObject d) {
		String ctx = getContext(d);
		InputContext inp = RoboSimFactory.eINSTANCE.createInputContext();
		return inp;
		
	}
	
	
	public OutputContext outputContext(EObject d) {
		String ctx = getContext(d);
		OutputContext out = RoboSimFactory.eINSTANCE.createOutputContext();
		return out;
		
	}
	
	//inputContext
	public InputContext newStateMachineInputContext(EObject d) {
		String ctx = getContext(d);
		InputContext inp = RoboSimFactory.eINSTANCE.createInputContext();
		return inp;
		
	}
	//outputContext
	public OutputContext newStateMachineOutputContext(EObject d) {
		String ctx = getContext(d);
		OutputContext out = RoboSimFactory.eINSTANCE.createOutputContext();
		return out;
		
	}
	
//public void newStateMachineDefCycle2(EObject o){
//	
//	String ctx = getContext(o);
//	String s = readInput("", "Define a boolean expression for the cycle in the format cycle == value", new IInputValidator() {
//		@Override
//		public String isValid(String s) {
//			Expression f = readExpression(o, s, ctx);
//			ParseResult<SimMachineDefInContext> f2 = readSimMachine(o, "stm " + s, ctx);
//			if (r == null)
//				return "Cannot parse";
//			return null;
//		}
//
//	});
//	if (s == null)
//		return;
//	StateMachineRef r = readStateMachineRef(d, s, ctx);
//	if (r != null && d instanceof ControllerDef) {
//		r.setName(newStateMachineRefName(d));
//		((ControllerDef) d).getMachines().add(r);
//	}
//		
//		
//		
//		
//		//Expression f = readExpression(o, s, ctx);
//		SimMachineDef sm = readSimMachine(o, s, ctx);
//		sm.setName(nameRes);
//		sm.setCycleDef(f);
//		if (sm != null && sm instanceof SimMachineDef ) {
//			RCPackage rs = (RCPackage) o.eContainer();
//			for (StateMachine stm : rs.getMachines()) {
//				stm.toString();
//			}
//			((RCPackage) o.eContainer()).getMachines().add(sm);
//			
//		}
//		//return f;
//	}
	
//	public Expression newStateMachineDefCycle(EObject o){
//		
//		
//		SimMachineDef d = (SimMachineDef) o;
//		String ctx = getContext(o);
//		String cycle= "";
//		
//		//String s = readInput("", "Define a boolean expresion for the cycle", new IInputValidator() {
//		
//		String s = readInput("", "Define a boolean expression for the cycle in the format cycle == value", new IInputValidator() {
//				@Override
//				public String isValid(String s) {
//					
//					Expression f = readExpression(o, s, ctx);
//					ParseResult<SimMachineDefInContext> f2 = readSimMachine(d, "stm " + s, ctx);
//					if (f == null)
//						return "Cannot parse";
//				
//					if  (f2 == null || f2.errors.size() > 0 || f2.object == null) {
//						String err = "Cannot parse:\n";
//						for (String x: f2.errors) {
//							err += x + "\n";
//						}
//						return err;
//					}
//					return null;
//				}
//
//			});
//			
////			@Override
////			public String isValid(String s) {
////				ParseResult<SimMachineDefInContext> f = readSimMachine(d, "stm " + s, ctx);
////				if (f == null || f.errors.size() > 0 || f.object == null) {
////					String err = "Cannot parse:\n";
////					for (String x: f.errors) {
////						err += x + "\n";
////					}
////					return err;
////				}
////				return null;
////			}
////			
//		
////			@Override
////			public String isValid(String s) {
////				
////				Expression f = readExpression(o, s, ctx);
////				ParseResult<SimMachineDefInContext> f = readSimMachine(d, "stm " + s, ctx);
////				if (f == null || f.errors.size() > 0 || f.object == null) {
////					String err = "Cannot parse:\n";
////					for (String x: f.errors) {
////						err += x + "\n";
////					}
////					return err;
////				}
//			//	return null;
//				
////				if (f == null || f.errors.size() > 0 || f.object == null) {
////					String err = "Cannot parse:\n";
////					for (String x: f.errors) {
////						err += x + "\n";
////					}
////					return err;
////				
////				if (f == null)
////					return "Cannot parse";
////				return null;
////			}
//
//	//	});
//		
//		if (s == null) {
//			//String err = "Cannot parse:\n";
//			ParseResult<SimMachineDefInContext> f2 = readSimMachine(d, "stm " + s, ctx);
//			return null;//err;
//		}
//		return null;
//			
//		
//		//Expression f = readExpression(o, s, ctx);
////		SimMachineDef sm = readSimMachine(o, s, ctx);
////		sm.setName(nameRes);
////		sm.setCycleDef(f);
////		if (sm != null && sm instanceof SimMachineDef ) {
////			RCPackage rs = (RCPackage) o.eContainer();
////			for (StateMachine stm : rs.getMachines()) {
////				stm.toString();
////			}
////			((RCPackage) o.eContainer()).getMachines().add(sm);
//			
////		}
//		//return f;
//	}
//	
	public Expression newStateMachineDefCycle(EObject o){
		String ctx = getContext(o);
		String cycle= "";
		//String s = readInput("", "Define a boolean expresion for the cycle", new IInputValidator() {
		
		String s = readInput("", "Define a boolean expression for the cycle in the format cycle == value", new IInputValidator() {
			@Override
			public String isValid(String s) {
				
				Expression f = readExpression(o, s, ctx);
				
				if (f == null)
					return "Cannot parse";
				return null;
			}

		});
		
//		if (s == null)
//			return;
		
		Expression f = readExpression(o, s, ctx);
//		SimMachineDef sm = readSimMachine(o, s, ctx);
//		sm.setName(nameRes);
//		sm.setCycleDef(f);
//		if (sm != null && sm instanceof SimMachineDef ) {
//			RCPackage rs = (RCPackage) o.eContainer();
//			for (StateMachine stm : rs.getMachines()) {
//				stm.toString();
//			}
//			((RCPackage) o.eContainer()).getMachines().add(sm);
			
//		}
		return f;
	}
	
	
	public Expression newDefCycle(EObject o){
		String ctx = getContext(o);
		String cycle= "";
		String s = readInput("", "Define a boolean expression for the cycle in the format cycle == value", new IInputValidator() {
			@Override
			public String isValid(String s) {
				
				Expression f = readExpression(o, s, ctx);
				
				if (f == null)
					return "Cannot parse";
				return null;
			}

		});
		Expression f = readExpression(o, s, ctx);
//		SimMachineDef sm = readSimMachine(o, s, ctx);
//		sm.setName(nameRes);
//		sm.setCycleDef(f);
//		if (sm != null && sm instanceof SimMachineDef ) {
//			RCPackage rs = (RCPackage) o.eContainer();
//			for (StateMachine stm : rs.getMachines()) {
//				stm.toString();
//			}
//			((RCPackage) o.eContainer()).getMachines().add(sm);
			
//		}
		return f;
	}
	
	public Expression newControllerDefCycle(EObject o){
		String ctx = getContext(o);
		String cycle= "";
		String s = readInput("", "Define a boolean expression for the cycle in the format cycle == value", new IInputValidator() {
			@Override
			public String isValid(String s) {
				
				Expression f = readExpression(o, s, ctx);
				
				if (f == null)
					return "Cannot parse";
				return null;
			}

		});
		Expression f = readExpression(o, s, ctx);
//		SimMachineDef sm = readSimMachine(o, s, ctx);
//		sm.setName(nameRes);
//		sm.setCycleDef(f);
//		if (sm != null && sm instanceof SimMachineDef ) {
//			RCPackage rs = (RCPackage) o.eContainer();
//			for (StateMachine stm : rs.getMachines()) {
//				stm.toString();
//			}
//			((RCPackage) o.eContainer()).getMachines().add(sm);
			
//		}
		return f;
	}
	
	//@Override
	public String newSimMachineDefName(EObject o) {
		

		String ctx = getContext(o);	
		String name = "stm";
		List<String> list = new LinkedList<String>();
		List<StateMachineDef> cs = getStateMachineDefs(o.eContainer());
	
		if (cs == null)
			return name + "0";
		
		for (StateMachineDef t : cs) {
			list.add(t.getName());
			
		}
		
		String nameRes;
		
		int i = 0;
		while (true) {
			if (!list.contains(name + i)) {
				nameRes = name + i ;
				break;
			} else {
				i++;
			}
		}
		
//		Expression cycleDef = readExpression(o, s, ctx);
//		SimMachineDef sm = readSimMachine(o, nameRes, s, ctx);
//		sm.setName(nameRes);
//		sm.setCycleDef(cycleDef);
//		if (sm != null && sm instanceof SimMachineDef ) {
//			RCPackage rs = (RCPackage) o.eContainer();
//			for (StateMachine stm : rs.getMachines()) {
//				stm.toString();
//			}
		//	((RCPackage) o.eContainer()).getMachines().add(sm);
			
//		}
		
		return nameRes ;
	}
	
	
//	@Override
	public String newSimControllerDefName(EObject o) {
		String name = "ctrl";
		List<String> list = new LinkedList<String>();
		List<ControllerDef> cs = getSimControllerDefs(o.eContainer());
		if (cs == null)
			return name + "0";
		for (ControllerDef t : cs) {
			list.add(t.getName());
		}
		int i = 0;
		while (true) {
			if (!list.contains(name + i)) {
				return name + i;
			} else {
				i++;
			}
		}
	}
	
	//@Override
	public String newSimModuleName(EObject o) {
		String name = "mod";
		List<String> list = new LinkedList<String>();
		List<RCModule> cs = getModules(o.eContainer());
		if (cs == null)
			return name + "0";
		for (RCModule t : cs) {
			list.add(t.getName());
		}
		int i = 0;
		while (true) {
			if (!list.contains(name + i)) {
				return name + i;
			} else {
				i++;
			}
		}
	}
	
	public String newRoboticPlatformDefName(EObject o) {
		String name = "rp";
		List<String> list = new LinkedList<String>();
		List<RoboticPlatformDef> cs = getRoboticPlatformDefs(o.eContainer());
		if (cs == null)
			return name + "0";
		for (RoboticPlatformDef t : cs) {
			list.add(t.getName());
		}
		int i = 0;
		while (true) {
			if (!list.contains(name + i)) {
				return name + i;
			} else {
				i++;
			}
		}
	}
	
	//@Override
	public String newSimTransitionName(EObject o) {
		String name = "t";
		List<String> transitions = new LinkedList<String>();
		List<Transition> ts = getTransitions(o.eContainer());
		if (ts == null)
			return name + "0";
		for (Transition t : ts) {
			transitions.add(t.getName());
		}
		int i = 0;
		while (true) {
			if (!transitions.contains(name + i)) {
				return name + i;
			} else {
				i++;
			}
		}
	}
	
	//@Override
	public EList<ControllerDef> getSimControllerDefs(EObject o) {
		if (o instanceof RCModule) {
			EList<ControllerDef> list = new BasicEList<ControllerDef>();
			for (ConnectionNode m : ((RCModule) o).getNodes()) {
				if (m instanceof ControllerDef) {
					list.add((ControllerDef) m);
				}
			}
			return list;
		} else if (o instanceof RCPackage) {
			EList<ControllerDef> list = new BasicEList<ControllerDef>();
			for (Controller m : ((RCPackage) o).getControllers()) {
				if (m instanceof ControllerDef) {
					list.add((ControllerDef) m);
				}
			}
			return list;
		} else
			return null;
	}
	
	@Override
	public EList<RoboticPlatformDef> getRoboticPlatformDefs(EObject o) {
		if (o instanceof RCModule) {
			EList<RoboticPlatformDef> list = new BasicEList<RoboticPlatformDef>();
			for (ConnectionNode m : ((RCModule) o).getNodes()) {
				if (m instanceof RoboticPlatformDef) {
					list.add((RoboticPlatformDef) m);
				}
			}
			return list;
		} else if (o instanceof RCPackage) {
			EList<RoboticPlatformDef> list = new BasicEList<RoboticPlatformDef>();
			for (RoboticPlatform m : ((RCPackage) o).getRobots()) {
				if (m instanceof RoboticPlatformDef) {
					list.add((RoboticPlatformDef) m);
				}
			}
			return list;
		} else
			return null;
	}

	
	
	
	
	
	
	/*
	 * getterServices
	 */
	@Override
	public EList<OperationDef> getOperationDefs(EObject o) {
		if (o instanceof SimControllerDef) {
			EList<OperationDef> list = new BasicEList<OperationDef>();
			for (Operation m : ((SimControllerDef) o).getLOperations()) {
				if (m instanceof SimOperationDef) {
					list.add((OperationDef) m);
				}
			}
			return list;
		} else if (o instanceof RCPackage) {
			EList<OperationDef> list = new BasicEList<OperationDef>();
			for (Operation m : ((RCPackage) o).getOperations()) {
				if (m instanceof SimOperationDef) {
					list.add((OperationDef) m);
				}
			}
			return list;
		} else if (o instanceof SimModule) {
			EList<OperationDef> list = new BasicEList<OperationDef>();
			for (ConnectionNode m : ((SimModule) o).getNodes()) {
				if (m instanceof SimOperationDef) {
					list.add((OperationDef) m);
				}
			}
			return list;
		} else
			return null;
	}
	
//	//@Override
//	public EList<SimControllerRef> getSimControllerRefs(EObject o) {
//		if (o instanceof SimModule) {
//			EList<SimControllerRef> list = new BasicEList<SimControllerRef>();
//			for (ConnectionNode m : ((SimModule) o).getNodes()) {
//				if (m instanceof SimControllerRef) {
//					list.add((SimControllerRef) m);
//				}
//			}
//			return list;
//		} else
//			return null;
//	}
	
	@Override
	public EList<StateMachineDef> getStateMachineDefs(EObject o) {
		if (o instanceof ControllerDef) {
			EList<StateMachineDef> list = new BasicEList<StateMachineDef>();
			for (StateMachine m : ((ControllerDef) o).getMachines()) {
				if (m instanceof StateMachineDef) {
					list.add((StateMachineDef) m);
				}
			}
			return list;
		} else if (o instanceof RCModule) {
			EList<StateMachineDef> list = new BasicEList<StateMachineDef>();
			for (ConnectionNode m : ((RCModule) o).getNodes()) {
				if (m instanceof StateMachineDef) {
					list.add((StateMachineDef) m);
				}
			}
			return list;
		} else if (o instanceof RCPackage) {
			EList<StateMachineDef> list = new BasicEList<StateMachineDef>();
			for (StateMachine m : ((RCPackage) o).getMachines()) {
				if (m instanceof StateMachineDef) {
					list.add((StateMachineDef) m);
				}
			}
			return list;
		} else
			return null;
	}
	
	
	public EList<StateMachineDef> getSimControlerDefs(EObject o) {
		if (o instanceof SimControllerDef) {
			EList<StateMachineDef> list = new BasicEList<StateMachineDef>();
			for (StateMachine m : ((SimControllerDef) o).getMachines()) {
				if (m instanceof StateMachineDef) {
					list.add((StateMachineDef) m);
				}
			}
			return list;
		} else if (o instanceof RCModule) {
			EList<StateMachineDef> list = new BasicEList<StateMachineDef>();
			for (ConnectionNode m : ((RCModule) o).getNodes()) {
				if (m instanceof StateMachineDef) {
					list.add((StateMachineDef) m);
				}
			}
			return list;
		} else if (o instanceof RCPackage) {
			EList<StateMachineDef> list = new BasicEList<StateMachineDef>();
			for (StateMachine m : ((RCPackage) o).getMachines()) {
				if (m instanceof StateMachineDef) {
					list.add((StateMachineDef) m);
				}
			}
			return list;
		} else
			return null;
	}
	
	public EList<RCModule> getModules(EObject o) {
		if (o instanceof RCPackage) {
			return ((RCPackage) o).getModules();
		} else
			return null;
	}
	
//	@Override
//	public EList<Event> allEvents(EObject s) {
//		if (s instanceof InputContext){
//			EList<Event> events = new BasicEList<Event>();
//			events.addAll(((InputContext) s).getEvents());
//			return events;
//		}else if (s instanceof OutputContext){
//			EList<Event> events = new BasicEList<Event>();
//			events.addAll(((OutputContext) s).getEvents());
//			return events;
//		}
//		
//		else if (s instanceof Context) {
//			EList<Event> events = new BasicEList<Event>();
//			events.addAll(((Context) s).getEvents());
//			
//			events.addAll(((InputContext) s).getEvents());
//			// changed this because events are only in interfaces used, not
//			// provided or required
//			for (Interface i : ((Context) s).getInterfaces())
//				events.addAll(i.getEvents());
//			/*
//			 * for (Interface i : ((Context) s).getRInterfaces())
//			 * events.addAll(i.getEvents());
//			 */
//			return events;
//		} else if (s instanceof StateMachineRef)
//			return allEvents(((StateMachineRef) s).getRef());
//		else if (s instanceof ControllerRef)
//			return allEvents(((ControllerRef) s).getRef());
//		else if (s instanceof RoboticPlatformRef)
//			return allEvents(((RoboticPlatformRef) s).getRef());
//		else if (s instanceof OperationRef)
//			return allEvents(((OperationRef) s).getRef());
//		else if (s instanceof ModuleRef)
//			return allEvents(((ModuleRef) s).getRef());
//		else if (s instanceof RCModule)
//			return allEvents(getRoboticPlatform((RCModule)s));
//		else
//			return null;
//	}

	
	/*
	 * creating services
	 */
	static int i = 0;
	
	private Resource createTempResource(EObject o) {
		// JPanel parent = new JPanel();
		ResourceSet rs = o.eResource().getResourceSet();
		String platformString = o.eResource().getURI().toPlatformString(true);
		/*
		 * JFrame parent = new JFrame(); JOptionPane.showMessageDialog(parent,
		 * "Creating resource: "+platformString+".rct");
		 */
		URI uri = URI.createPlatformResourceURI(platformString + i + ".rslabel", true);
		try {
			Resource r = rs.createResource(uri);
			return r;
		} catch (Exception e) {
			i++;
			// JOptionPane.showMessageDialog(parent, e.getMessage());
			return createTempResource(o);
		}
	}
	
	
	
	private void destroyTempResource(Resource r) {
		log("Destroying resource " + r.getURI());
		r.getResourceSet().getResources().remove(r);
		try {
			r.delete(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createSimVariable(EObject d) {
		if (d instanceof Interface && (((Interface) d).getEvents().size() > 0)) {
			alert("Failed to create variable", "Cannot create a variable in an inteface containing events");
			return;
		}
		String ctx = getContext(d);
		String s = readInput("", "Enter a variable list in the format [VName: Type (',' Vname: Type)*]", new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<VariableList> f = readSimVariable(d, "var " + s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		ParseResult<VariableList> f = readSimVariable(d, "var " + s, ctx);
		if (f != null && f.object != null && d instanceof BasicContext) {
			((BasicContext) d).getVariableList().add(f.object);
		}
	}
	
	
	public void createOperation(EObject d) {
		if (d instanceof Interface && (((Interface) d).getEvents().size() > 0)) {
			alert("Failed to create operation", "Cannot create an operation in an inteface containing events");
			return;
		}
		String ctx = getContext(d);
		String s = readInput("", "Define an operation as [Name]([Parameter]*)", new IInputValidator() {
			@Override
			public String isValid(String s) {
				OperationSig f = readOperation(d, s, ctx);
				//Operation f = readOperation(d,s,ctx);
				if (f == null)
					return "Cannot parse";
				return null;
			}

		});
		if (s == null)
			return;
		OperationSig f = readOperation(d, s, ctx);
		if (f != null && d instanceof BasicContext) {
			((BasicContext) d).getOperations().add(f);
			
		}
	}
	
	//@Override
	public void createSimOperationDef(EObject d) {
		// Because operations can be declared at the level of RCPackage, we
		// might have the context as a RCPackage, and
		// since a RCPackage might not have a name, we need an alternative
		// element to ground the label and give access to
		// the objects declared, for instance, for the method typesDeclared.
		String ctx = getContext(d);
		String s = readInput("", "Define an operation as [Name]([Parameter]*)", new IInputValidator() {
			@Override
			public String isValid(String s) {
				OperationDef f = readOperationDef(d, s, ctx);
				if (f == null)
					return "Cannot parse";
				return null;
			}

		});
		if (s == null)
			return;
		OperationDef f = readOperationDef(d, s, ctx);
		if (f != null) {
			if (d instanceof RCPackage) {
				((RCPackage) d).getOperations().add(f);				
			} else if (d instanceof SimControllerDef) {
				 ((SimControllerDef) d).getOperations().add(f);
	
			}
		}
	}
	
	public void createSimAction(EObject d) {
		String ctx = getContext(d);// getStateMachineName(d);
		String s = readInput("", "Enter an action in the format [(entry|exit|during) Action]", new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<Action> f = readSimAction(d, "action " + s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		ParseResult<Action> f = readSimAction(d, "action " + s, ctx);
		if (f != null && f.object != null && d instanceof State) {
			((State) d).getActions().add(f.object);
		}
	}

//	@Override
//	public void createUsedInterface(EObject d) {
//		String ctx = getContext(d);
//		String s = readInput("", "Enter an interface name", new IInputValidator() {
//			@Override
//			public String isValid(String s) {
//				Interface f = readUsedInterface(d, s, ctx);
//				if (f == null)
//					return "Cannot parse";
//				else if (d instanceof RoboticPlatformDef && (f.getOperations().size() != 0 || f.getVariableList().size() != 0)) {
//					return "Cannot use an interface containing operations or variables";
//				}
//				else if (d instanceof ControllerDef && f.getOperations().size() != 0) {
//					return "Cannot use an interface containing operations";
//				}
//				return null;
//			}
//
//		});
//		if (s == null)
//			return;
//		Interface f = readUsedInterface(d, s, ctx);
//		if (f != null && d instanceof InputContext){
//			((InputContext) d).getInterfaces().add(f);
//		}
//		else if (f != null && d instanceof OutputContext){
//			((OutputContext) d).getInterfaces().add(f);
//		}
//		if (f != null && d instanceof BasicContext) {
//			((Context) d).getInterfaces().add(f);
//		}
//		
//		
//		
//	}
//	
//	@Override
//	public void createRequiredInterface(EObject d) {
//		String ctx = getContext(d);
//		String s = readInput("", "Enter an interface name", new IInputValidator() {
//			@Override
//			public String isValid(String s) {
//				Interface f = readRequiredInterface(d, s, ctx);
//				if (f == null)
//					return "Cannot parse";
//				else if (f.getEvents().size() != 0)
//					return "Cannot require an interface containing events";
//				return null;
//			}
//
//		});
//		if (s == null)
//			return;
//		Interface f = readRequiredInterface(d, s, ctx);
//		
//		
//		if (f != null && d instanceof InputContext){
//			((InputContext) d).getRInterfaces().add(f);
//		}
//		else if (f != null && d instanceof OutputContext){
//			((OutputContext) d).getRInterfaces().add(f);
//		}
//		else if(f != null && d instanceof BasicContext) {
//			((Context) d).getRInterfaces().add(f);
//		}
//		
//	}
	
	
	/*
	 * reading services
	 */
	
	// Fields, variables, Literals, clocks and events
	
//	@Override
//	public Field readField(EObject o, String s, String context) {
//		try {
//			Resource r = createTempResource(o);
//			String str = includeImports(o) + " label " + s + ((context != null) ? (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			FieldInContext fic = (FieldInContext) p;
//			Field f = EcoreUtil2.copy(fic.getField());
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			return f;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	//@Override
	public ParseResult<VariableList> readSimVariable(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			Resource original = o.eResource();
			/*
			 * String str = ""; RCPackage spec = (RCPackage)
			 * original.getContents().get(0); for (Import i : spec.getImports())
			 * { str += "import " + i.getImportedNamespace() + "\n"; }
			 */
			String str = includeImports(o) +  s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			VariableInContext fic = (VariableInContext) p;
			VariableList f = EcoreUtil2.copy(fic.getVars());
			List<String> errors = new LinkedList<String>();
			for (Diagnostic d : r.getErrors()) {
				errors.add(d.getMessage().replace("incontext", "end of line"));
			}
			// System.out.println("REPLACING "+str +" Errors:
			// "+r.getErrors().size());
			destroyTempResource(r);
			return new ParseResult<VariableList>(f, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
//	@Override
//	public Literal readLiteral(EObject o, String s, String context) {
//		try {
//			Resource r = createTempResource(o);
//			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			LiteralInContext fic = (LiteralInContext) p;
//			Literal f = EcoreUtil2.copy(fic.getLiteral());
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			return f;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
//	@Override
//	public Clock readClock(EObject o, String s, String context) {
//		try {
//			Resource r = createTempResource(o);
//			String str = s + ((context != null) ? (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			ClockInContext fic = (ClockInContext) p;
//			Clock f = EcoreUtil2.copy(fic.getClock());
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			return f;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
	@Override
	public Event readEvent(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + "event " + s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			EventInContext fic = (EventInContext) p;
			Event f = EcoreUtil2.copy(fic.getEv());
			boolean b = r.getErrors().size() > 0;
			destroyTempResource(r);
			if (b) {
				return null;
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// TODO: double-check the need fo rsimoperationdef as the syntax in the original plugin did not add a cycle
	public SimOperationDef readOperationDef(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + "signature " + s
					+ ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			OperationInContext fic = (OperationInContext) p;
			OperationSig op = (OperationSig) EcoreUtil2.copy(fic.getOp());
			//OperationDef od = RoboChartFactory.eINSTANCE.createOperationDef();
			SimOperationDef od = RoboSimFactory.eINSTANCE.createSimOperationDef(); 
			InputContext inpC = RoboSimFactory.eINSTANCE.createInputContext();//inputContext(od);
			OutputContext outC = RoboSimFactory.eINSTANCE.createOutputContext(); //outputContext(od);
			od.getParameters().addAll(op.getParameters());
			od.setName(op.getName());
			od.setInputContext(inpC);
			od.setOutputContext(outC);
			boolean b = r.getErrors().size() > 0;
			destroyTempResource(r);
			if (b) {
				return null;
			}
			return od;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	    
	/*
	 * interfaces
	 */
	
//	public Interface readUsedInterface(EObject o, String s, String context) {
//		try {
//			Resource r = createTempResource(o);
//			Resource original = o.eResource();
//			/*
//			 * RCPackage spec = (RCPackage) original.getContents().get(0); for
//			 * (Import i : spec.getImports()) { str += "import " +
//			 * i.getImportedNamespace() + "\n"; }
//			 */
//
//			String str = includeImports(o) + "used " + s + ((context != null) ? (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			UsedInterfaceInContext fic = (UsedInterfaceInContext) p;
//			Interface f = fic.getInterface();
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			return f;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
//	@Override
//	public Interface readRequiredInterface(EObject o, String s, String context) {
//		try {
//			Resource r = createTempResource(o);
//			Resource original = o.eResource();
//			/*
//			 * RCPackage spec = (RCPackage) original.getContents().get(0); for
//			 * (Import i : spec.getImports()) { str += "import " +
//			 * i.getImportedNamespace() + "\n"; }
//			 */
//			String str = includeImports(o) + "required " + s + ((context != null) ? (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			RequiredInterfaceInContext fic = (RequiredInterfaceInContext) p;
//			Interface f = fic.getInterface();
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			return f;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	
//	@Override
//	public Interface readProvidedInterface(EObject o, String s, String context) {
//		try {
//			Resource r = createTempResource(o);
//			/*
//			 * String str = ""; RCPackage spec = (RCPackage)
//			 * original.getContents().get(0); for (Import i : spec.getImports())
//			 * { str += "import " + i.getImportedNamespace() + "\n"; }
//			 */
//			//"provided " +
//			String str = includeImports(o) +  s + ((context != null) ? (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			ProvidedInterfaceInContext fic = (ProvidedInterfaceInContext) p;
//			Interface f = fic.getInterface();
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			return f;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	

	//Realmente precisa?
//	public SimMachineDef readSimMachine(EObject o, String name, String nmCycleDef, String context) {
//		try {
//			Resource r = createTempResource(o);
//			String str = includeImports(o) + "label stm " + name + " cycleDef " + nmCycleDef + 
//			((context != null) ?  (" incontext " + context) : "");
//			InputStream in2 = new ByteArrayInputStream(str.getBytes());
//			r.load(in2, null);
//			r.getContents();
//			EcoreUtil2.resolveAll(r.getResourceSet());
//			Label p = getLabel(r);
//			SimMachineDefInContext fic = (SimMachineDefInContext) p;
//			SimMachineDef stm = RoboSimFactory.eINSTANCE.createSimMachineDef();
//			stm.setName(fic.getName());
//			stm.setCycleDef(fic.getExp());
//			boolean b = r.getErrors().size() > 0;
//			destroyTempResource(r);
//			if (b) {
//				return null;
//			}
//			
//			return stm;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//		
//		public SimMachineDef readSimMachine(EObject o,  String nmCycleDef, String context) {
//			try {
//				Resource r = createTempResource(o);
//				String str = includeImports(o) + "label stm stm1 cycleDef " + nmCycleDef + 
//				((context != null) ?  (" incontext " + context) : "");
//				InputStream in2 = new ByteArrayInputStream(str.getBytes());
//				r.load(in2, null);
//				r.getContents();
//				EcoreUtil2.resolveAll(r.getResourceSet());
//				Label p = getLabel(r);
//				SimMachineDefInContext fic = (SimMachineDefInContext) p;
//				SimMachineDef stm = RoboSimFactory.eINSTANCE.createSimMachineDef();
//				stm.setName(fic.getName());
//				stm.setCycleDef(fic.getExp());
//				boolean b = r.getErrors().size() > 0;
//				destroyTempResource(r);
//				if (b) {
//					return null;
//				}
//				
//				return stm;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//	}
	
	// Expressions, statements and actions 
	@Override
	public Expression readExpression(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + "expression " + s
					+ ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			ExpressionInContext fic = (ExpressionInContext) p;
			Expression f = EcoreUtil2.copy(fic.getExp());
			boolean b = r.getErrors().size() > 0;
			destroyTempResource(r);
			if (b) {
				return null;
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	@Override
	public Statement readStatement(EObject o, String s) {
		try {

			Resource r = createTempResource(o);
			String str = includeImports(o) + s;
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			cleanResources(r, r.getResourceSet());
			// EcoreUtil2.resolveAll(r);

			EcoreUtil2.resolveAll(r.getResourceSet());

			Label p = getLabel(r);
			StatementInContext fic = (StatementInContext) p;
			Statement f = EcoreUtil2.copy(fic.getStm());
			boolean b = r.getErrors().size() > 0;
			// System.out.println("REPLACING "+str +" Errors:
			// "+r.getErrors().size());
			destroyTempResource(r);
			if (b) {
				return null;
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	*/

	@Override
	public Statement readStatement(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + "statement " + s
					+ ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			StatementInContext fic = (StatementInContext) p;
			Statement f = EcoreUtil2.copy(fic.getStm());
			boolean b = r.getErrors().size() > 0;
			destroyTempResource(r);
			if (b) {
				return null;
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ParseResult<Action> readSimAction(EObject o, String s, String context) {
		try {

			Resource r = createTempResource(o);
			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
			//String str2 = includeImports(o) + "action " + s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			cleanResources(r, r.getResourceSet());
			// EcoreUtil2.resolveAll(r);

			EcoreUtil2.resolveAll(r.getResourceSet());

			Label p = getLabel(r);
			ActionInContext fic = (ActionInContext) p;
			Action f = EcoreUtil2.copy(fic.getAction());
			List<String> errors = new LinkedList<String>();
			for (Diagnostic d : r.getErrors()) {
				errors.add(d.getMessage().replace("incontext", "end of line"));
			}
			// System.out.println("REPLACING "+str +" Errors:
			// "+r.getErrors().size());
			destroyTempResource(r);
			return new ParseResult<Action>(f, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//Operation
	
	public OperationSig readOperation(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + "signature " + s
					+ ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			OperationInContext fic = (OperationInContext) p;
			OperationSig f = EcoreUtil2.copy(fic.getOp());
			boolean b = r.getErrors().size() > 0;
			destroyTempResource(r);
			if (b) {
				return null;
			}
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void log(String s) {
		try {
			Bundle b = Platform.getBundle("circus.robocalc.robosim.graphical");
			Platform.getLog(b).log(new Status(Status.INFO, "circus.robocalc.robosim.graphical", Status.OK, s, null));
		} catch (Exception e) {
			System.out.println(s);
		}
	}
	
	public void cleanResources(Resource cr, ResourceSet rs) {
		log("cleaning resources");
		for (Iterator<Resource> it = rs.getResources().iterator(); it.hasNext();) {
			Resource r = it.next();
			String uri = r.getURI().toString();
			log(uri);
			if (uri.indexOf(".rslabel") >= 0 && cr != r) {
				log("removing " + uri);
				it.remove();
			}

		}
		log("done cleaning resources");
	}

	//Transitions
	public ParseResult<SimTransitionInContext> readSimTransition(EObject o, String s, String context) {
		try {

			Resource r = createTempResource(o);
			String str = includeImports(o) + "transition " + s + ((context != null) ? (" incontext " + context) : "");
			//String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			cleanResources(r, r.getResourceSet());
			// EcoreUtil2.resolveAll(r);

			EcoreUtil2.resolveAll(r.getResourceSet());

			Label p = getLabel(r);
			SimTransitionInContext tic = (SimTransitionInContext) p;
			tic.getCond();
			// JPanel panel = new JPanel();
			// JOptionPane.showMessageDialog(panel, r.getErrors().size());
			// System.out.println("REPLACING "+str +" Errors:
			// "+r.getErrors().size());
			List<String> errors = new LinkedList<String>();
			
			for (Diagnostic d : r.getErrors()) {
				errors.add(d.getMessage().replace("incontext", "end of line"));
			}
			destroyTempResource(r);
			return new ParseResult<SimTransitionInContext>(tic, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	//SimMachineDef
	
	public ParseResult<SimMachineDefInContext> readSimMachine(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			cleanResources(r, r.getResourceSet());
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			SimMachineDefInContext fic = (SimMachineDefInContext) p;
			System.out.println(fic.getInputContext());
			System.out.println(fic.getOutputContext());
			List<String> errors = new LinkedList<String>();
			for (Diagnostic d : r.getErrors()) {
				errors.add(d.getMessage().replace("incontext", "end of line"));
			}
			destroyTempResource(r);
			return new ParseResult<SimMachineDefInContext>(fic, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//SimControllerDef
	
	public ParseResult<SimControllerDefInContext> readSimController(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			cleanResources(r, r.getResourceSet());
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			SimControllerDefInContext fic = (SimControllerDefInContext) p;
			List<String> errors = new LinkedList<String>();
			for (Diagnostic d : r.getErrors()) {
				errors.add(d.getMessage().replace("incontext", "end of line"));
			}
			destroyTempResource(r);
			return new ParseResult<SimControllerDefInContext>(fic, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//SimModule
	
		public ParseResult<SimModuleInContext> readSimModule(EObject o, String s, String context) {
			try {
				Resource r = createTempResource(o);
				String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
				InputStream in2 = new ByteArrayInputStream(str.getBytes());
				r.load(in2, null);
				r.getContents();
				cleanResources(r, r.getResourceSet());
				EcoreUtil2.resolveAll(r.getResourceSet());
				Label p = getLabel(r);
				SimModuleInContext fic = (SimModuleInContext) p;
				List<String> errors = new LinkedList<String>();
				for (Diagnostic d : r.getErrors()) {
					errors.add(d.getMessage().replace("incontext", "end of line"));
				}
				destroyTempResource(r);
				return new ParseResult<SimModuleInContext>(fic, errors);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
		//StateMachineRef
		//MSCF 14/10
//		@Override
//		public StateMachineRef readStateMachineRef(EObject o, String s, String context) {
//			try {
//				Resource r = createTempResource(o);
//				String str = includeImports(o) + "sref " + s.replace("::", "_") + "_REF = " + s
//						+ ((context != null) ? (" incontext " + context) : "");
//				InputStream in2 = new ByteArrayInputStream(str.getBytes());
//				r.load(in2, null);
//				r.getContents();
//				EcoreUtil2.resolveAll(r.getResourceSet());
//				Label p = getLabel(r);
//				StateMachineRefInContext fic = (StateMachineRefInContext) p;
//				// TODO: check change. I changed from simstatemachinref to statemachineref as there is no apparent difference between them
//				StateMachineRef f = RoboChartFactory.eINSTANCE.createStateMachineRef();			
//				f.setName(fic.getName());
//				f.setRef(fic.getRef());
//				boolean b = r.getErrors().size() > 0;
//				destroyTempResource(r);
//				if (b) {
//					return null;
//				}
//				return f;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
//		
//		//ControllerRef
//		public ControllerRef readControllerRef(EObject o, String s, String context) {
//			try {
//				Resource r = createTempResource(o);
//				String str = includeImports(o) + "cref " + s.replace("::", "_") + "_REF = " + s
//						+ ((context != null) ? (" incontext " + context) : "");
//				InputStream in2 = new ByteArrayInputStream(str.getBytes());
//				r.load(in2, null);
//				r.getContents();
//				EcoreUtil2.resolveAll(r.getResourceSet());
//				Label p = getLabel(r);
//				ControllerRefInContext fic = (ControllerRefInContext) p;
//				// TODO: check change. I changed from simcontrollerref to controllerref as there is no apparent difference between them				
//				ControllerRef f = RoboChartFactory.eINSTANCE.createControllerRef();
//				f.setName(fic.getName());
//				f.setRef(fic.getRef());
//				boolean b = r.getErrors().size() > 0;
//				destroyTempResource(r);
//				if (b) {
//					return null;
//				}
//				return f;
//			} catch (Exception e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
//	
	
	public EList<Transition> getSimTransitions(EObject o) {
		if (o instanceof NodeContainer) {
			return ((NodeContainer) o).getTransitions();
		} else
			return null;
	}
	
	//Edition services
	
	public void editSimAction(EObject d) {
		if (!(d instanceof Action)) {
			return;
		}
		Action a = (Action) d;
		String ctx = getContext(d);
		String value = printSimAction(a);
		String s = readInput("", "Enter an action in the format [(entry|exit|during) Action]", value, new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<Action> f = readSimAction(d, "action " + s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		
		
		ParseResult<Action> f = readSimAction(d, "action " + s, ctx);
		if (f != null && f.object != null) {
			EcoreUtil2.replace(a, f.object);
		}
	}
	
	
	public void editSimTransition(EObject d) {
		if (!(d instanceof Transition)) {
			return;
		}
		Transition t = (Transition) d;
		String ctx = getContext(d);
		String value = printSimTransition(t);
		String s = readInput("", "Enter a transition label in the format ('exec')? ([guard])? / action?", value, new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<SimTransitionInContext> f = readSimTransition(d, s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		
		
		ParseResult<SimTransitionInContext> tic = readSimTransition(d, s, ctx);
		if (tic != null && tic.object != null) {
			t.setTrigger(tic.object.getTrigger());
			t.getReset().clear();
			t.getReset().addAll(tic.object.getReset());
			t.setCondition(tic.object.getCond());
			t.setAction(tic.object.getStm());
		}
	}
	
	public void editSimMachineDef(EObject d) {
		if (!(d instanceof SimMachineDef)) {
			return;
		}
		SimMachineDef sm = (SimMachineDef) d;
		String ctx = getContext(d);
		String value = printSimMachine(sm);
		String s = readInput("", "Enter a state machine label in the format stmName [cycleDef = cycle==exp]", value, new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<SimMachineDefInContext> f = readSimMachine(d, "stm " + s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		
		
		ParseResult<SimMachineDefInContext> tic = readSimMachine(d, "stm " + s, ctx);
		if (tic != null && tic.object != null) {
			sm.setName(tic.object.getName());
			sm.setCycleDef(tic.object.getExp());
			
		}
	}
	
	public void editSimControllerDef(EObject d) {
		if (!(d instanceof SimControllerDef)) {
			return;
		}
		SimControllerDef sc = (SimControllerDef) d;
		String ctx = getContext(d);
		String value = printSimController(sc);
		String s = readInput("", "Enter a controller label in the format controllerName [cycleDef = cycle==exp]", value, new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<SimControllerDefInContext> f = readSimController(d, "controller " + s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		
		
		ParseResult<SimControllerDefInContext> tic = readSimController(d, "controller " + s, ctx);
		if (tic != null && tic.object != null) {
			sc.setName(tic.object.getName());
			sc.setCycleDef(tic.object.getExp());
			
		}
	}
	
	public void editSimModule(EObject d) {
		if (!(d instanceof SimModule)) {
			return;
		}
		SimModule sm = (SimModule) d;
		String ctx = getContext(d);
		String value = printSimModule(sm);
		String s = readInput("", "Enter a module label in the format moduleName [cycleDef = cycle==exp]", value, new IInputValidator() {
			@Override
			public String isValid(String s) {
				ParseResult<SimModuleInContext> f = readSimModule(d, "module " + s, ctx);
				if (f == null || f.errors.size() > 0 || f.object == null) {
					String err = "Cannot parse:\n";
					for (String x: f.errors) {
						err += x + "\n";
					}
					return err;
				}
				return null;
			}
		});
		if (s == null)
			return;
		
		
		ParseResult<SimModuleInContext> tic = readSimModule(d, "module " + s, ctx);
		if (tic != null && tic.object != null) {
			sm.setName(tic.object.getName());
			sm.setCycleDef(tic.object.getExp());
			
		}
	}

	/*
	 * deletion
	 */
	// TODO: double check this case. I have changed from override because the robochart editor has no such method.
	// this use case is treated by the method deleteContext. I kept the method here because it is probably used
	// in the odesign file.
//	@Override
	public EObject deleteStateMachineDef(StateMachineDef m) {
		Collection<Setting> usage = EcoreUtil.UsageCrossReferencer.find(m, m.eResource().getResourceSet());
		for (Setting s : usage) {
			// these should only be references
			EObject o = s.getEObject();
			if (m instanceof StateMachineRef) {
				deleteReferences(o);
			} else {
				EcoreUtil2.remove(o);
			}
		}

		EcoreUtil2.remove(m);
		return null;
	}
	
	public EObject deleteSimControllerDef(Controller m) {
		Collection<Setting> usage = EcoreUtil.UsageCrossReferencer.find(m, m.eResource().getResourceSet());
		for (Setting s : usage) {
			// these should only be references
			EObject o = s.getEObject();
			if (m instanceof ControllerRef) {
				deleteReferences(o);
			} else {
				EcoreUtil2.remove(o);
			}
		}

		EcoreUtil2.remove(m);
		return null;
	}
	
	
	// Types
		public EObject deleteType(EObject t) {
			Resource res = t.eResource();

			ResourceSet rs = t.eResource().getResourceSet();
			List<TypeDecl> types = new ArrayList<TypeDecl>();
			for (Resource r : rs.getResources()) {
				if (r.getContents().size() > 0 && r.getContents().get(0) instanceof RCPackage) {
					types.addAll(((RCPackage) r.getContents().get(0)).getTypes());
				}
			}

			types.remove(t);
			Shell shell = Display.getCurrent().getActiveShell();
			ElementListSelectionDialog dlg = new ElementListSelectionDialog(shell, new TypeLabelProvider());
			dlg.setElements(types.toArray());
			if (dlg.open() == Window.OK) {
				Object type = dlg.getFirstResult();
				// deal with enumerations
				if (t instanceof Enumeration) {
					// if the newly selected type is also an enumeration and they
					// have the same Literals
					// replace the references to the Literals same for same
					if (type instanceof Enumeration && EqualEnums((Enumeration) t, (Enumeration) type)) {
						for (Literal c : ((Enumeration) t).getLiterals()) {
							Literal x = findAlternative(c, (Enumeration) type);
							ResourceSet rset = c.eResource().getResourceSet();
							List<RefExp> list = new ArrayList<RefExp>();
							for (Iterator<Notifier> it = rset.getAllContents(); it.hasNext();) {
								Notifier n = it.next();
								if (n instanceof RefExp && ((RefExp) n).getRef() == c) {
									list.add((RefExp) n);
								}
							}

							for (RefExp u : list) {
								u.setRef(x);
							}
						}
					} else {
						// otherwise, replace the reference to the Literals by a
						// string indicating the need
						// to change it
						for (Literal c : ((Enumeration) t).getLiterals()) {
							ResourceSet rset = c.eResource().getResourceSet();
							List<RefExp> list = new ArrayList<RefExp>();
							for (Iterator<Notifier> it = rset.getAllContents(); it.hasNext();) {
								Notifier n = it.next();
								if (n instanceof RefExp && ((RefExp) n).getRef() == c) {
									list.add((RefExp) n);
								}
							}

							for (RefExp u : list) {
								StringExp value = RoboChartFactory.eINSTANCE.createStringExp();
								value.setValue("REPLACE");
								EcoreUtil2.replace(u, value);
							}
						}
					}
				}

				Collection<Setting> list = EcoreUtil.UsageCrossReferencer.find(t, res.getResourceSet());
				if (list.isEmpty()) {
					EcoreUtil2.delete(t);
					return null;
				}

				for (Setting s : list) {

					if (!(s.getEObject() instanceof TypeRef)) {
						// if the reference is not an operation call, continues
						// to next
						continue;
					}
					// calculate the scope for an operation call
					TypeRef tr = (TypeRef) s.getEObject();
					tr.setRef((TypeDecl) type);
				}
				EcoreUtil2.delete(t);
				return null;
			} else
				return null;
		}
		
		//Interfaces
		
		@Override
		public void deleteUsedInterface(Interface f, EObject o) {
			EObject ae = simAssociatedElement(o);
			if (ae instanceof Context) {
				Context c = (Context) ae;
				Map<EObject, String> map = new HashMap<EObject, String>();
				// may need to change this so that it replaces events in triggers
				// and send actions
				for (OperationSig op : f.getOperations()) {
					// finding all objects that refer to op defined in f
					Collection<Setting> list = EcoreUtil.UsageCrossReferencer.find(op, f.eResource().getResourceSet());
					// for every cross reference, replace it with an
					// alternative (if
					// none exists, replace the call with skip
					for (Setting s : list) {
						if (s.getEObject() instanceof Call) {
							Call call = (Call) s.getEObject();
							String node = getConnectionNodeName(call);
							String label = "statement " + print(call);
							if (node != null)
								label += " incontext " + node;
							map.put(call, label);
						} else if (s.getEObject() instanceof CallExp) {
							CallExp call = (CallExp) s.getEObject();
							String node = getConnectionNodeName(call);
							String label = "expression " + print(call);
							if (node != null)
								label += " incontext " + node;
							map.put(call, label);
						}
					}
				}
				c.getInterfaces().remove(f);
				for (EObject i : map.keySet()) {
					String label = map.get(i);
					if (i instanceof Call) {
						Statement stm = readStatement(i, label);
						if (stm == null) {
							stm = RoboChartFactory.eINSTANCE.createSkip();
						}
						EcoreUtil2.replace(i, stm);
					} else if (i instanceof CallExp) {
						Expression exp = readExpression(i, label);
						if (exp == null) {
							Skip skip = RoboChartFactory.eINSTANCE.createSkip();
							EObject container = getCallExpContainer(i);
							if (container instanceof Statement) {
								EcoreUtil2.replace(container, skip);
							} else if (container instanceof Variable) {
								((Variable) container).setInitial(null);
							}
						} else {
							EcoreUtil2.replace(i, exp);
						}
					}
				}
			} else {
				JPanel p = new JPanel();
				JOptionPane.showMessageDialog(p, "Failed to delete used interface: " + f.getName());
			}

		}
		
		@Override
		public void deleteRequiredInterface(Interface f, EObject o) {
			EObject ae = simAssociatedElement(o);
			if (ae instanceof Context) {
				Context c = (Context) ae;
				Map<EObject, String> map = new HashMap<EObject, String>();
				for (OperationSig op : f.getOperations()) {
					// finding all objects that refer to op defined in f
					Collection<Setting> list = EcoreUtil.UsageCrossReferencer.find(op, f.eResource().getResourceSet());
					// for every cross reference, replace it with an
					// alternative (if
					// none exists, replace the call with skip
					for (Setting s : list) {
						if (s.getEObject() instanceof Call) {
							Call call = (Call) s.getEObject();
							String node = getConnectionNodeName(call);
							String label = "statement " + print(call);
							if (node != null)
								label += " incontext " + node;
							map.put(call, label);
						} else if (s.getEObject() instanceof CallExp) {
							CallExp call = (CallExp) s.getEObject();
							String node = getConnectionNodeName(call);
							String label = "expression " + print(call);
							if (node != null)
								label += " incontext " + node;
							map.put(call, label);
						}
					}
				}
				c.getRInterfaces().remove(f);
				for (EObject i : map.keySet()) {
					String label = map.get(i);
					if (i instanceof Call) {
						Statement stm = readStatement(i, label);
						if (stm == null) {
							stm = RoboChartFactory.eINSTANCE.createSkip();
						}
						EcoreUtil2.replace(i, stm);
					} else if (i instanceof CallExp) {
						Expression exp = readExpression(i, label);
						if (exp == null) {
							Skip skip = RoboChartFactory.eINSTANCE.createSkip();
							EObject container = getCallExpContainer(i);
							if (container instanceof Statement) {
								EcoreUtil2.replace(container, skip);
							} else if (container instanceof Variable) {
								((Variable) container).setInitial(null);
							}
						} else {
							EcoreUtil2.replace(i, exp);
						}
					}
				}
			} else {
				JPanel p = new JPanel();
				JOptionPane.showMessageDialog(p, "Failed to delete required interface: " + f.getName());
			}

		}
		
		public void deleteProvidedInterface(Interface f, EObject o) {
			EObject ae = simAssociatedElement(o);
			Map<EObject, String> map = new HashMap<EObject, String>();
			if (ae instanceof Context) {
				Context c = (Context) ae;
				for (OperationSig op : f.getOperations()) {
					// finding all objects that refer to op defined in f
					Collection<Setting> list = EcoreUtil.UsageCrossReferencer.find(op, f.eResource().getResourceSet());
					// for every cross reference, replace it with an
					// alternative (if
					// none exists, replace the call with skip
					for (Setting s : list) {
						if (s.getEObject() instanceof Call) {
							Call call = (Call) s.getEObject();
							String node = getConnectionNodeName(call);
							String label = "statement " + print(call);
							if (node != null)
								label += " incontext " + node;
							map.put(call, label);
						} else if (s.getEObject() instanceof CallExp) {
							CallExp call = (CallExp) s.getEObject();
							String node = getConnectionNodeName(call);
							String label = "expression " + print(call);
							if (node != null)
								label += " incontext " + node;
							map.put(call, label);
						}
					}
				}
				c.getPInterfaces().remove(f);
				for (EObject i : map.keySet()) {
					String label = map.get(i);
					if (i instanceof Call) {
						Statement stm = readStatement(i, label);
						if (stm == null) {
							stm = RoboChartFactory.eINSTANCE.createSkip();
						}
						EcoreUtil2.replace(i, stm);
					} else if (i instanceof CallExp) {
						Expression exp = readExpression(i, label);
						if (exp == null) {
							Skip skip = RoboChartFactory.eINSTANCE.createSkip();
							EObject container = getCallExpContainer(i);
							if (container instanceof Statement) {
								EcoreUtil2.replace(container, skip);
							} else if (container instanceof Variable) {
								((Variable) container).setInitial(null);
							}
						} else {
							EcoreUtil2.replace(i, exp);
						}
					}
				}
			} else {
				JPanel p = new JPanel();
				JOptionPane.showMessageDialog(p, "Failed to delete provided interface: " + f.getName());
			}

		}
		
		
		/*
		 * CLASSES
		 */
		class TypeLabelProvider extends LabelProvider {
			@Override
			public String getText(Object element) {
				if (element instanceof TypeDecl) {
					TypeDecl t = ((TypeDecl) element);
					RCPackage s = (RCPackage) t.eContainer();
					String name = "";
					if (s.getName() != null)
						name += s.getName() + "::";
					name += t.getName();
					return name;

				} else {
					return super.getText(element);
				}
			}
		}
		
		
		//Interface
		
		public EList<Interface> getInterface(EObject o) {
			if (o instanceof RCPackage) {
				return ((RCPackage) o).getInterfaces();
			} else
				return null;
		}
		
		@Override
		public NamedElement associatedElement(EObject o) {
			if (o instanceof DNodeListSpec) {
				DNodeListSpec node = (DNodeListSpec) o;
				// JOptionPane.showMessageDialog(parent, "2
				// "+node.basicGetTarget());
				EObject target = node.basicGetTarget();
				if (target instanceof NamedElement)
					return ((NamedElement) target);
				else
					return null;

			} else if (o instanceof DNodeContainerSpec) {
				DNodeContainerSpec node = (DNodeContainerSpec) o;
				// JOptionPane.showMessageDialog(parent, "2
				// "+node.basicGetTarget());
				EObject target = node.basicGetTarget();
				if (target instanceof NamedElement)
					return ((NamedElement) target);
				else
					return null;
			} else if (o instanceof DNodeSpec) {
				DNodeSpec node = (DNodeSpec) o;
				EObject target = node.basicGetTarget();
				if (target instanceof NamedElement)
					return ((NamedElement) target);
				else
					return null;
			} else {
				return null;
			}
		}
		
		
		
}
