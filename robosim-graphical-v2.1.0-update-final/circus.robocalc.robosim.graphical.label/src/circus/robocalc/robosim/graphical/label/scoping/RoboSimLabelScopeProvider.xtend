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
 *   Alvaro Miyazawa - initial definition
 ********************************************************************************/

/*
 * This is based on the scope provider for the label plugin for RoboChart.
 * I need to test and revise to make sure that the resolved objects are part
 * of RoboSim and not RoboChart.
 * 
 */
package circus.robocalc.robosim.graphical.label.scoping

import circus.robocalc.robochart.StateClockExp
import circus.robocalc.robochart.StateMachineDef
import circus.robocalc.robosim.graphical.label.roboSimLabel.ControllerRefInContext
import circus.robocalc.robosim.graphical.label.roboSimLabel.Label
import circus.robocalc.robosim.graphical.label.roboSimLabel.ProvidedInterfaceInContext
import circus.robocalc.robosim.graphical.label.roboSimLabel.RequiredInterfaceInContext
import circus.robocalc.robosim.graphical.label.roboSimLabel.RoboticPlatformRefInContext
import circus.robocalc.robosim.graphical.label.roboSimLabel.SimTransitionInContext
import circus.robocalc.robosim.graphical.label.roboSimLabel.StateMachineRefInContext
import circus.robocalc.robosim.graphical.label.roboSimLabel.UsedInterfaceInContext
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.scoping.IScope

import static circus.robocalc.robosim.graphical.label.roboSimLabel.RoboSimLabelPackage.Literals.*

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class RoboSimLabelScopeProvider extends AbstractRoboSimLabelScopeProvider {
	override getScope(EObject context, EReference reference) {
		if (context instanceof RequiredInterfaceInContext) {
			if (reference === REQUIRED_INTERFACE_IN_CONTEXT__INTERFACE) {
				var parentScope = super.getScope(context, reference)
				var scope = context.interfacesDeclared(parentScope)
				return scope
			} else {
				return super.getScope(context, reference)
			}
		} else if (context instanceof ProvidedInterfaceInContext) {
			if (reference === PROVIDED_INTERFACE_IN_CONTEXT__INTERFACE) {
				var parentScope = super.getScope(context, reference)
				var scope = context.interfacesDeclared(parentScope)
				return scope
			} else {
				return super.getScope(context, reference)
			}
		} else if (context instanceof UsedInterfaceInContext) {
			if (reference === USED_INTERFACE_IN_CONTEXT__INTERFACE) {
				var parentScope = super.getScope(context, reference)
				var scope = context.interfacesDeclared(parentScope)
				return scope
			} else {
				return super.getScope(context, reference)
			}
		} else if (context instanceof RoboticPlatformRefInContext) {
			if (reference === ROBOTIC_PLATFORM_REF_IN_CONTEXT__REF) {
				var parentScope = super.getScope(context, reference)
				var scope = context.rpsDeclared(parentScope)
				return scope
			} else {
				return super.getScope(context, reference)
			}
		} else if (context instanceof ControllerRefInContext) {
			if (reference === CONTROLLER_REF_IN_CONTEXT__REF) {
				var parentScope = super.getScope(context, reference)
				var scope = context.ctrlsDeclared(parentScope)
				return scope
			} else {
				return super.getScope(context, reference)
			}
		} else if (context instanceof StateMachineRefInContext) {
			if (reference === STATE_MACHINE_REF_IN_CONTEXT__REF) {
				var parentScope = super.getScope(context, reference)
				var scope = context.stmsDeclared(parentScope)
				return scope
			} else {
				return super.getScope(context, reference)
			}
		} else {
			super.getScope(context,reference)
		}
	}

	def dispatch IScope typesDeclared(Label o, IScope p) {
		if (o === null) {
			return p
		} else {
			if (o.context === null) {
				if (o.eContainer === null) {
					return p
				} else {
					return o.eContainer.typesDeclared(p)
				}
			} else {
				return o.context.typesDeclared(p)
			}
		}
	}

	def dispatch IScope rpsDeclared(Label o, IScope p) {
		if (o === null) {
			return p
		} else {
			if (o.context === null) {
				if (o.eContainer === null) {
					return p
				} else {
					return o.eContainer.rpsDeclared(p)
				}
			} else {
				return o.context.rpsDeclared(p)
			}
		}
	}

	def dispatch IScope stmsDeclared(Label o, IScope p) {
		if (o === null) {
			return p
		} else {
			if (o.context === null) {
				if (o.eContainer === null) {
					return p
				} else {
					return o.eContainer.stmsDeclared(p)
				}
			} else {
				return o.context.stmsDeclared(p)
			}
		}
	}

	def dispatch IScope ctrlsDeclared(Label o, IScope p) {
		if (o === null) {
			return p
		} else {
			if (o.context === null) {
				if (o.eContainer === null) {
					return p
				} else {
					return o.eContainer.ctrlsDeclared(p)
				}
			} else {
				return o.context.ctrlsDeclared(p)
			}
		}
	}

	def dispatch IScope interfacesDeclared(Label o, IScope p) {
		if (o === null) {
			return p
		} else {
			if (o.context === null) {
				if (o.eContainer === null) {
					return p
				} else {
					return o.eContainer.interfacesDeclared(p)
				}
			} else {
				return o.context.interfacesDeclared(p)
			}
		}
	}

	def dispatch IScope eventsDeclared(Label cont, IScope p) {
		cont?.context?.eventsDeclared(p);
	}
	
	def dispatch IScope outputEventsDeclared(Label cont, IScope p) {
		cont?.context?.outputEventsDeclared(p);
	}

	def dispatch IScope variablesDeclared(Label cont, IScope p) {
		if (cont === null)
			return p
		else {
			if (cont.context !== null)
				return cont.context.variablesDeclared(p)
			else if (cont.eContainer !== null)
				return cont.eContainer.variablesDeclared(p)
			else
				return p
		}
	}
	
	def dispatch IScope inputVariablesDeclared(Label cont, IScope p) {
		if (cont === null)
			return p
		else {
			if (cont.context !== null)
				return cont.context.inputVariablesDeclared(p)
			else if (cont.eContainer !== null)
				return cont.eContainer.inputVariablesDeclared(p)
			else
				return p
		}
	}
	
	def dispatch IScope outputVariablesDeclared(Label cont, IScope p) {
		if (cont === null)
			return p
		else {
			if (cont.context !== null)
				return cont.context.outputVariablesDeclared(p)
			else if (cont.eContainer !== null)
				return cont.eContainer.outputVariablesDeclared(p)
			else
				return p
		}
	}
	
	
	

	def dispatch IScope operationsDeclared(Label cont, IScope parent) {
		if (cont === null)
			return parent
		else {
			if (cont.context !== null)
				return cont.context.operationsDeclared(parent)
			else if (cont.eContainer !== null)
				return cont.eContainer.operationsDeclared(parent)
			else
				return parent
		}
	}
	
	/** @author: Madiel
	 * Added to consider output operations.
	 */
	
	def dispatch IScope outputOperationsDeclared(Label cont, IScope parent) {
		if (cont === null)
			return parent
		else {
			if (cont.context !== null)
				return cont.context.outputOperationsDeclared(parent)
			else if (cont.eContainer !== null)
				return cont.eContainer.outputOperationsDeclared(parent)
			else
				return parent
		}
	}

	def dispatch IScope functionsDeclared(Label cont, IScope parent) {
		if (cont === null)
			return parent
		else {
			if (cont.context !== null)
				return cont.context.functionsDeclared(parent)
			else if (cont.eContainer !== null)
				return cont.eContainer.functionsDeclared(parent)
			else
				return parent
		}
	}

	/** @author: Pedro
	 * Added to bring timed instants declared in transitions inside states
	 * into the state machine scope.
	 */
	def dispatch IScope clocksDeclared(Label cont) {
		if (cont === null)
			return IScope::NULLSCOPE
		else {
			if (cont.context !== null)
				return cont.context.clocksDeclared
			else if (cont.eContainer !== null)
				return cont.eContainer.clocksDeclared
			else
				return IScope::NULLSCOPE
		}
	}

	def dispatch IScope statesDeclared(Label cont, IScope p) {
		if (cont === null)
			return p
		else {
			if (cont.context !== null)
				return cont.context.statesDeclared(p)
			else if (cont.eContainer !== null)
				return cont.eContainer.statesDeclared(p)
			else
				return p
		}
	}

	override StateMachineDef contextOfSinceEntry(StateClockExp e) {
		var EObject container = e
		while (container !== null && !(container instanceof SimTransitionInContext) &&
			!(container instanceof StateMachineDef)) {
			container = container.eContainer
		}
		if (container instanceof SimTransitionInContext) {
			container = container.context
			while (container !== null && !(container instanceof StateMachineDef)) {
				container = container.eContainer
			}
		}
		if (container instanceof StateMachineDef) {
			return container
		} else {
			return null
		}
	}

	def dispatch IScope variantsDeclared(Label cont, IScope parent) {
		if (cont === null)
			return parent
		else {
			if (cont.context !== null)
				return cont.context.variantsDeclared(parent)
			else if (cont.eContainer !== null)
				return cont.eContainer.variantsDeclared(parent)
			else
				return parent
		}
	}
}
