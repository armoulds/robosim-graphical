/*
 * generated by Xtext 2.19.0
 */
package circus.robocalc.robosim.graphical.label


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class RoboSimLabelStandaloneSetup extends RoboSimLabelStandaloneSetupGenerated {

	def static void doSetup() {
		new RoboSimLabelStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}