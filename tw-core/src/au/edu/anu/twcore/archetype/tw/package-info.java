/**
 *
 */
/**
 *
 * <p>This package contains (1) all the <strong>archetypes</strong> (*.ugt) files that define the specification of a model in
 * 3Worlds; and (2) all the {@code Query} subclasses required by the previous files to define constraints on
 * specifications.</p>
 *
 * <p><strong>For developers: about Query scope.</strong> constraint Queries tend to generate a lot of warning or error messages which may
 * overwhelm the naive reader with difficult-to-decipher information. To maximize the usefulness
 * of these message, one should be careful to only report what the query is effectively testing
 * (remember that any exception will be caught and reported to the log window by the checker). Also,
 * notice that the use in a Query of the CoreQueries.get(...) method may generate many messages in
 * the context of the ModelMaker graph editor, where the being constructed graph can be in any state,
 * most of the time invalid. When another constraint is not fulfilled that prevents the Query to
 * make a decision, it should be mute, as the problem will be handled by another Query. In particular,
 * Avoid triggering a lower level query by <em>not</em> using oneOrMany: use zeroOrMany
 * instead.
 * </p>
 *
 * @author J. Gignoux & I.D. Davies - 19 nov. 2020
 *
 */
// This file only created to generate package-level javadoc
package au.edu.anu.twcore.archetype.tw;