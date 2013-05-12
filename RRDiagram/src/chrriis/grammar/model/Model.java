/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.grammar.model;

import java.util.Collection;

/**
 * @author Lukas Eder
 */
public class Model {
	
	/**
	 * Create a new <code>Grammar</code>.
	 */
	public static Grammar grammar(Rule... rules) {
		return new Grammar(rules);
	}

	/**
	 * Create a new <code>Grammar</code>.
	 */
	public static Grammar grammar(Collection<? extends Rule> rules) {
		return new Grammar(rules.toArray(new Rule[rules.size()]));
	}

	/**
	 * Create a new <code>Choice</code>.
	 */
	public static Choice choice(Expression... expressions) {
		return new Choice(expressions);
	}

	/**
	 * Create a new <code>Choice</code>.
	 */
	public static Choice choice(Collection<? extends Expression> expressions) {
		return new Choice(expressions.toArray(new Expression[expressions.size()]));
	}

	/**
	 * Create a new <code>Literal</code>.
	 */
	public static Literal literal(String text) {
		return new Literal(text);
	}

	/**
	 * Create a new <code>Repetition</code>.
	 */
	public static Repetition repetition(Expression expression) {
		return new Repetition(expression, 0, null);
	}

	/**
	 * Create a new <code>Repetition</code>.
	 */
	public static Repetition repetition(Expression expression, int min, Integer max) {
		return new Repetition(expression, min, max);
	}

	/**
	 * Create a new <code>Rule</code>.
	 */
	public static Rule rule(String name, Expression expression) {
		return new Rule(name, expression);
	}

	/**
	 * Create a new <code>RuleReference</code>.
	 */
	public static RuleReference reference(String name) {
		return new RuleReference(name);
	}

	/**
	 * Create a new <code>Sequence</code>.
	 */
	public static Sequence sequence(Expression... expressions) {
		return new Sequence(expressions);
	}

	/**
	 * Create a new <code>Sequence</code>.
	 */
	public static Sequence sequence(Collection<? extends Expression> expressions) {
		return new Sequence(expressions.toArray(new Expression[expressions.size()]));
	}
}
