/**
 *
 */
/**
 *
 * <p>
 * This package contains: (1) all the <strong>archetype</strong> (*.ugt) files
 * that define the specification of a model in 3Worlds; and (2) all the
 * {@code Query} subclasses required by these archetype files to define
 * constraints on specifications.
 * </p>
 *
 * <p>
 * <strong>For developers: about Query scope.</strong> Constraint Queries can be
 * overly verbose at times and make interpretation of the error difficult to
 * decipher. To maximize the usefulness of these messages, take care to only
 * report what the query is effectively testing (remember that any exception
 * will be caught and reported to the log window by the checker). Also, notice
 * that using the query system within the body of a query (e.g by calling
 * CoreQueries.get(...)) risks generating additional error messages if that
 * query fails for some reason. Therefore, if using queries within a query, make
 * sure the queries you call cannot fail. Remember the graph being tested may be
 * in any state during editing. When another constraint is not fulfilled that
 * prevents the Query from making a decision, it should remain mute (i.e
 * satisfied), as the problem will be handled by another Query. For example, if
 * your query uses selectOneOrMany(...) and there are none of what you are
 * searching for, an exception will be generated for the selectOneOrMany() query
 * itself. Instead, consider using selectZeroOrMany() and handle the case of the
 * empty list within your query.
 * </p>
 *
 * @author J. Gignoux & I.D. Davies - 19 nov. 2020
 *
 */
// This file only created to generate package-level javadoc
package au.edu.anu.twcore.archetype.tw.old;