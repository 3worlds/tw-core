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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACETYPE;

import au.edu.anu.qgraph.queries.QueryAdaptor;
import au.edu.anu.qgraph.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.space.Box;

//TODO This duplicates a test in BorderTypeValid?!!
/**
 * 
 * <p>
 * Check that a box property in a {@link SpaceNode} the same dimension as the
 * space.
 * </p>
 * <dl>
 * <dt>Expected input</dt>
 * <dd>{@link SpaceNode}</dd>
 * <dt>Type of result</dt>
 * <dd>Same as input ({@code result=input})</dd>
 * <dt>Fails if</dt>
 * <ol>
 * <li>The number of dimensions are not compatible with the
 * {@link SpaceType};</li>
 * </ol>
 * </dt>
 * </dl>
 * 
 * @author Jacques Gignoux - 16 sept. 2020
 *
 */

public class BoxInSpaceDimensionQuery extends QueryAdaptor {
	private String propName;

	public BoxInSpaceDimensionQuery(String boxProp) {
		super();
		propName = boxProp;
	}

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode spn = (SpaceNode) input;
		SpaceType stype = (SpaceType) spn.properties().getPropertyValue(P_SPACETYPE.key());
		// no problem if no Box property
		if (spn.properties().hasProperty(propName)) {
			Box prop = (Box) spn.properties().getPropertyValue(propName);
			if (prop != null)
				if (prop.dim() != stype.dimensions()) {
					String[] msgs = TextTranslations.getBoxInSpaceDimensionQuery(propName, spn.toShortString(),
							stype.dimensions(), prop.dim());
					actionMsg = msgs[0];
					errorMsg = msgs[1];
				}
		}

		return this;
	}

}
