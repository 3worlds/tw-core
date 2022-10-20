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

import java.util.List;

import fr.cnrs.iees.omugi.collections.tables.*;
import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import fr.cnrs.iees.omugi.graph.TreeNode;
import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import au.edu.anu.twcore.ui.*;

/**
 * A query to constrain the multiplicity between {@linkplain UITab},
 * {@linkplain UIContainer} and {@linkplain WidgetNode}.
 * <p>
 * Input is either a {@linkplain UITab} or a {@linkplain UIContainer}.
 * </p>
 * <p>
 * Constraint: Either 1 or 2 of widgets or just 2 containers for
 * {@linkplain UITab} and {@linkplain UIContainer}s in the GUI.
 * </p>
 * <p>
 * 
 * Cannot have: just one container; nothing; or, more that 2 of either in total.
 * 
 * 
 * <li>1 widget OR
 * 
 * <li>1 widget and 1 container OR
 * 
 * <li>2 widgets OR
 * 
 * <li>2 containers
 * 
 * @author Ian Davies - 27 Sep 2019
 */
public class ChildAtLeastOneOfOneOrTwoOfTwoQuery extends QueryAdaptor {
	private final String widgetLabel;
	private final String containerLabel;

	public ChildAtLeastOneOfOneOrTwoOfTwoQuery(String nodeLabel1, String nodeLabel2) {
		super();
		this.widgetLabel = nodeLabel1;
		this.containerLabel = nodeLabel2;
	}

	public ChildAtLeastOneOfOneOrTwoOfTwoQuery(StringTable table) {
		super();
		widgetLabel = table.getWithFlatIndex(0);
		containerLabel = table.getWithFlatIndex(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode localItem = (TreeNode) input;
		List<TreeNode> widgets = (List<TreeNode>) get(localItem.getChildren(),
				selectZeroOrMany(hasTheLabel(widgetLabel)));
		List<TreeNode> containers = (List<TreeNode>) get(localItem.getChildren(),
				selectZeroOrMany(hasTheLabel(containerLabel)));

		switch (widgets.size()) {
		case 0: {
			if (containers.isEmpty()) {
				String[] msgs = TextTranslations.getChildAtLeastOneOfOneOrTwoOfTwoQuery1(localItem.toShortString(),
						widgetLabel, containerLabel);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
			} else if (containers.size() == 1) {
				String[] msgs = TextTranslations.getChildAtLeastOneOfOneOrTwoOfTwoQuery2(localItem.toShortString(),
						widgetLabel, containerLabel);
				actionMsg = msgs[0];
				errorMsg = msgs[1];
			}
			return this;
		}
		case 1: {
//			if (containers.size()>1)
//				
//				ok = false;
//			// Remove a container OR add widget

			break;
		}
		case 2: {
//			if (!containers.isEmpty())
//				ok = false;
//			// remove all containers
			break;
		}
		default: {
//			ok = false;
			// remove n widgets
		}
		}
		return this;
	}
}
