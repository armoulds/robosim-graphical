/********************************************************************************
 * Copyright (c) 2019 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Madiel Conserva Filho - initial definition
 *   Alvaro Miyazawa - initial definition
 *   
 ********************************************************************************/

package circus.robocalc.robosim.graphical.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.osgi.framework.Bundle;

import circus.robocalc.robochart.Action;
import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Import;
import circus.robocalc.robochart.RCPackage;
import circus.robocalc.robochart.VariableList;
import circus.robocalc.robochart.graphical.label.roboChartLabel.Label;
import circus.robocalc.robochart.graphical.label.roboChartLabel.LabelModel;
import circus.robocalc.robochart.graphical.label.roboChartLabel.TransitionInContext;
import circus.robocalc.robochart.graphical.services.ParseResult;
import circus.robocalc.robosim.RoboSimFactory;
import circus.robocalc.robosim.SimMachineDef;
import circus.robocalc.robosim.graphical.label.roboSimLabel.ActionInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.ExpressionInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.SimMachineDefInContext;
import circus.robocalc.robosim.graphical.label.roboSimLabel.VariableInContext;

public class ReadingServices {
	
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
	
	public Label getLabel(Resource r) {
		if (r.getContents().size() == 0) throw new RuntimeException("Label produced empty resource");
		EObject o = r.getContents().get(0);
		if (!(o instanceof LabelModel)) throw new RuntimeException("Root of label resource is not a LabelModel");
		LabelModel lm = (LabelModel) o;
		if (lm.getLabel() != null) return lm.getLabel();
		else throw new RuntimeException("LabelModel does not contain a label");
	}
	
	public String includeImports(EObject o) {
		EObject obj = o.eResource().getContents().get(0);
		if (obj instanceof RCPackage) {
			String s = "";
			if (((RCPackage) obj).getName() != null)
				s += "import "+ ((RCPackage)obj).getName()+ "::*\n";
			/*
			 * if (((RCPackage) obj).getName() != null) { s += "import " +
			 * ((RCPackage) obj).getName() + "::*\n"; }
			 */
			for (Import i : ((RCPackage) obj).getImports()) {
				s += "import " + i.getImportedNamespace() + "\n";
			}
			return s;
		} else
			return "";
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

	
	protected void log(String s) {
		try {
			Bundle b = Platform.getBundle("circus.robocalc.robosim.graphical");
			Platform.getLog(b).log(new Status(Status.INFO, "circus.robocalc.robosim.graphical", Status.OK, s, null));
		} catch (Exception e) {
			System.out.println(s);
		}
	}
	
	public ParseResult<VariableList> readVariable(EObject o, String s, String context) {
		try {
			Resource r = createTempResource(o);
			Resource original = o.eResource();
			/*
			 * String str = ""; RCPackage spec = (RCPackage)
			 * original.getContents().get(0); for (Import i : spec.getImports())
			 * { str += "import " + i.getImportedNamespace() + "\n"; }
			 */
			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
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
	
	public ParseResult<Action> readAction(EObject o, String s, String context) {
		try {

			Resource r = createTempResource(o);
			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
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
			return new ParseResult(f, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ParseResult<TransitionInContext> readSimTransition(EObject o, String s, String context) {
		try {

			Resource r = createTempResource(o);
			String str = includeImports(o) + s + ((context != null) ? (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			cleanResources(r, r.getResourceSet());
			// EcoreUtil2.resolveAll(r);

			EcoreUtil2.resolveAll(r.getResourceSet());

			Label p = getLabel(r);
			TransitionInContext tic = (TransitionInContext) p;
			// JPanel panel = new JPanel();
			// JOptionPane.showMessageDialog(panel, r.getErrors().size());
			// System.out.println("REPLACING "+str +" Errors:
			// "+r.getErrors().size());
			List<String> errors = new LinkedList<String>();
			for (Diagnostic d : r.getErrors()) {
				errors.add(d.getMessage().replace("incontext", "end of line"));
			}
			destroyTempResource(r);
			return new ParseResult<TransitionInContext>(tic, errors);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SimMachineDef readSimMachine(EObject o, String name, String nmCycleDef, String context) {
		try {
			Resource r = createTempResource(o);
			String str = includeImports(o) + "label stm " + name + " cycleDef " + nmCycleDef + 
			((context != null) ?  (" incontext " + context) : "");
			InputStream in2 = new ByteArrayInputStream(str.getBytes());
			r.load(in2, null);
			r.getContents();
			EcoreUtil2.resolveAll(r.getResourceSet());
			Label p = getLabel(r);
			SimMachineDefInContext fic = (SimMachineDefInContext) p;
			SimMachineDef stm = RoboSimFactory.eINSTANCE.createSimMachineDef();
			stm.setName(fic.getName());
			stm.setCycleDef(fic.getExp());
			boolean b = r.getErrors().size() > 0;
			destroyTempResource(r);
			if (b) {
				return null;
			}
			
			return stm;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
		
		public SimMachineDef readSimMachine(EObject o,  String nmCycleDef, String context) {
			try {
				Resource r = createTempResource(o);
				String str = includeImports(o) + "label stm stm1 cycleDef " + nmCycleDef + 
				((context != null) ?  (" incontext " + context) : "");
				InputStream in2 = new ByteArrayInputStream(str.getBytes());
				r.load(in2, null);
				r.getContents();
				EcoreUtil2.resolveAll(r.getResourceSet());
				Label p = getLabel(r);
				SimMachineDefInContext fic = (SimMachineDefInContext) p;
				SimMachineDef stm = RoboSimFactory.eINSTANCE.createSimMachineDef();
				stm.setName(fic.getName());
				stm.setCycleDef(fic.getExp());
				boolean b = r.getErrors().size() > 0;
				destroyTempResource(r);
				if (b) {
					return null;
				}
				
				return stm;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	}
		
		public Expression readExpression(EObject o, String s, String context) {
			try {
				Resource r = createTempResource(o);
				String str = includeImports(o) + "label expression " + s
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



}
