package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.lang.reflect.Constructor;
/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/function" node label in the 
 * 3Worlds configuration tree. Has the user class name property or a way to generate this class
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class FunctionNode 
		extends InitialisableNode 
		implements Singleton<TwFunction>, Sealable {

	private boolean sealed = false;
	private TwFunction function = null;
	
	public FunctionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public FunctionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		// this is for generating code
		String ftype = (String) properties().getPropertyValue(P_FUNCTIONTYPE.key());
		// this is once code has been generated and edited by the user
		String className = (String) properties().getPropertyValue(P_FUNCTIONCLASS.key());
		if (className!=null) {
			// instantiate the user code based function
			ClassLoader c = Thread.currentThread().getContextClassLoader();
			Class<? extends TwFunction> functionClass;
			try {
				functionClass = (Class<? extends TwFunction>) Class.forName(className,false,c);
				Constructor<? extends TwFunction> nodeConstructor = functionClass.getConstructor();
				function = nodeConstructor.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// add the consequences of the function, if any
			// if my parent is a function, I am a consequence of it
			if (getParent() instanceof FunctionNode) {
				FunctionNode parent = (FunctionNode) getParent();
				if (parent.isSealed())
					parent.getInstance().addConsequence(function);
			}
			// if my children are functions, they are consequences of me
			else for (TreeNode n:getChildren()) 
				if (n instanceof FunctionNode){
					FunctionNode csq = (FunctionNode) n;
					if (csq.isSealed())
						function.addConsequence(csq.getInstance());
			}
		}
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_FUNCTION.initRank();
	}

	@Override
	public TwFunction getInstance() {
		if (sealed)
			return function;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
