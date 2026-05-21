package org.hibernate.bugs;

import jakarta.persistence.AttributeConverter;

/**
 * Abstract base converter — the first level of the problematic hierarchy.
 * Declares TypeVariable T and directly implements AttributeConverter&lt;T, String&gt;.
 *
 * <p>This mirrors e.g. {@code JsonAttributeConverter<T>} in a production codebase.
 */
abstract class AbstractBaseConverter<T> implements AttributeConverter<T, String> {

	@Override
	public String convertToDatabaseColumn(T attribute) {
		return attribute == null ? null : attribute.toString();
	}

	@Override
	public abstract T convertToEntityAttribute(String dbData);
}
