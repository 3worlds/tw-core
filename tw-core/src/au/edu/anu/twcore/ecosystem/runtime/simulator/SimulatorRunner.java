package au.edu.anu.twcore.ecosystem.runtime.simulator;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.io.File;

import au.edu.anu.twcore.ecosystem.dynamics.SimulatorNode;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.project.TwPaths;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.io.FileImporter;

/**
 * A standalone runner for the simulator - to use with openMole.
 * assumes
 * <ul>
 * <li>no logging argument (or maybe in a file)</li>
 * <li>a project dir argument</li>
 * <li>running from a jar</li>
 * </ul>
 * 
 * @author Jacques Gignoux - 20 févr. 2020
 *
 */
public class SimulatorRunner {

	
	public SimulatorRunner() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		Simulator simulator;
		int id=0; // the simulator id, to be read on the command line
		
		if (args.length < 1) {
			System.out.println("missing project argument");
			System.exit(1);
		}

		File prjDir = new File(TwPaths.TW_ROOT + File.separator + args[0]);
		if (!prjDir.exists()) {
			System.out.println("Project not found: [" + prjDir + "]");
			System.exit(1);
		}

		if (Project.isOpen()) {
			System.out.println("Project is already open: [" + prjDir + "]");
			System.exit(1);
		}

		Project.open(prjDir);
		
		TreeGraph<TreeGraphDataNode, ALEdge> config = 
			(TreeGraph<TreeGraphDataNode, ALEdge>) FileImporter.loadGraphFromFile(Project.makeConfigurationFile());

		// TODO: remove the UI subtree from the graph if any: (to disconnect widgets from datatrackers)
//		uiNode = (TreeGraphNode) get(config.root().getChildren(), 
//			selectZeroOrOne(hasTheLabel(N_UI.label())));
		// OR this case only if uiNode==null;

		// TODO: check the simulator has a stopping condition
		// ie infinite time simulations are forbidden
		
		//TODO: WHICH SYSTEM - there can be many?
		SimulatorNode simNode = (SimulatorNode) get(config.root().getChildren(),
			selectOne(hasTheLabel(N_SYSTEM.label())),
			children(),
			selectOne(hasTheLabel(N_DYNAMICS.label())));
		// TODO: HOW MANY instances - depends on exp design?
// TODO Create a set of headless widgets to save output files.
		simulator = simNode.getInstance(id);
		simulator.resetSimulation();
		while (!simulator.stop())
			simulator.step();
	}

}
