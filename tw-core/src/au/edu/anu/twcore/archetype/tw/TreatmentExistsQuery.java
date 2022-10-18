/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.qgraph.queries.*;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * @author Ian Davies - 27 Jan 2022
 */
public class TreatmentExistsQuery extends QueryAdaptor {

	/**
	 * input: Design node
	 *
	 * If design is cross-factorial or sensitivity analysis then there must be a
	 * treatment node.
	 * 
	 * This could be generalised to a PropertyValueRequiresChild?
	 */
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeGraphDataNode dsn = (TreeGraphDataNode) input;
		if (!dsn.properties().hasProperty(P_DESIGN_TYPE.key()))
			return this;
		ExperimentDesignType edt = (ExperimentDesignType) dsn.properties().getPropertyValue(P_DESIGN_TYPE.key());
		if (edt.equals(ExperimentDesignType.singleRun))
			return this;
		TreeNode exp = ((TreeNode)dsn).getParent();
		TreeNode trt = (TreeNode) get(exp,children(),selectZeroOrOne(hasTheLabel(N_TREATMENT.label())));
		if (trt==null) {
			String[] msgs = TextTranslations.getTreatmentExistsQuery(edt.name(),N_TREATMENT.label());
			actionMsg = msgs[0];
			errorMsg = msgs[1];
		}

		return this;
	}

}
