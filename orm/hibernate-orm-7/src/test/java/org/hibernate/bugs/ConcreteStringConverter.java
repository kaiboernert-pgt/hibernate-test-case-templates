package org.hibernate.bugs;

import jakarta.persistence.Converter;

/**
 * Concrete converter — extends the two-level abstract hierarchy and binds T to {@code String}.
 *
 * <p>Annotated with {@code @Converter} so Hibernate registers it during
 * {@code EntityManagerFactory} bootstrap. When Hibernate processes this class it walks
 * the abstract supertype hierarchy ({@link AbstractMiddleConverter} → {@link AbstractBaseConverter})
 * to resolve the type arguments of {@code AttributeConverter}. In Hibernate 7.3.x this walk
 * causes infinite recursion in {@code GenericsHelper} because the unresolved TypeVariable T
 * appears at both levels without a cycle guard.
 */
@Converter
public class ConcreteStringConverter extends AbstractMiddleConverter<String> {

	@Override
	public String convertToEntityAttribute(String dbData) {
		return dbData;
	}
}
