package org.hibernate.bugs;

/**
 * Abstract middle converter — the second level of the problematic hierarchy.
 *
 * <p>The critical pattern: the same TypeVariable T declared here is passed through
 * unchanged to the supertype {@code AbstractBaseConverter<T>}. When Hibernate walks
 * this hierarchy to resolve the type arguments of {@code AttributeConverter}, it
 * encounters T as an unresolved TypeVariable at both levels, which in Hibernate 7.3.x
 * causes {@code GenericsHelper.substituteTypeArguments} and
 * {@code GenericsHelper.replaceTypeVariableWithArgument} to call each other infinitely.
 *
 * <p>This mirrors e.g. {@code JsonCollectionAttributeConverter<T> extends JsonAttributeConverter<T>}.
 */
abstract class AbstractMiddleConverter<T> extends AbstractBaseConverter<T> {
	// Same TypeVariable T passed through unchanged — the trigger pattern
}
