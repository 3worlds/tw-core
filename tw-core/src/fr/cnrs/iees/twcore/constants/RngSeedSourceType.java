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
package fr.cnrs.iees.twcore.constants;

import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/**
 * @author Ian Davies
 *
 * @date 13 Dec 2019
 */
public enum RngSeedSourceType {
	/* seed source from table by give index [0..999] */
	table, //
	/* seed produced by a call to SecureRandom - i.e 'never' replicated. */
	secure, //
	/* seed set to a simple constant (0 or 1 depending on alg used */
	constant,//
	/*
	 * we could also have an option to create seed by system time but it serves no
	 * purpose as far as I can see.
	 */
	;

	public static RngSeedSourceType defaultValue() {
		return secure;
	}

	static {
		ValidPropertyTypes.recordPropertyType(RngSeedSourceType.class.getSimpleName(),
				RngSeedSourceType.class.getName(), defaultValue());
	}
}
