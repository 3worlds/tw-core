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
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_SPACE_BORDERTYPE;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.TextTranslations;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.twcore.constants.BorderListType;
import fr.cnrs.iees.twcore.constants.SpaceType;
// TODO This needs looking at 
//* If edge effect correction = 'custom' and borderType property is not
//* provided!!!
//- also the whole 'tubular' thing is arbitrarily constrained to x direction only!

/**
 * <p>
 * Check on border details for a {@link SpaceNode}.
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
 * <li>'wrap' borders are not paired.</li>
 * <li>Wrapped in the y direction only.</li>
 * </ol>
 * </dt>
 * </dl>
 * 
 * @author Jacques Gignoux - 16 sept. 2020
 *
 */
public class BorderTypeValidityQuery extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode spnode = (SpaceNode) input;
		SpaceType stype = (SpaceType) spnode.properties().getPropertyValue(P_SPACETYPE.key());
		int spdim = stype.dimensions();
		BorderListType borderTypes = (BorderListType) spnode.properties().getPropertyValue(P_SPACE_BORDERTYPE.key());

		if (spdim != borderTypes.size() / 2) {
			String[] msgs = TextTranslations.getBorderTypeValidityQuery1(stype, spdim, borderTypes.size() / 2);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			return this;
		}
		int i = BorderListType.getUnpairedWrapIndex(borderTypes);
		if (i >= 0) {
			String[] msgs = TextTranslations.getBorderTypeValidityQuery2(i);
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			return this;
		}

		if (BorderListType.isWrongTubularOrientation(borderTypes)) {
			String[] msgs = TextTranslations.getBorderTypeValidityQuery3();
			actionMsg = msgs[0];
			errorMsg = msgs[1];
			return this;
		}

		return this;

	}

}
