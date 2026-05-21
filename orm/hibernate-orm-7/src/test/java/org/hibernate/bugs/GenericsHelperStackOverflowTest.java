package org.hibernate.bugs;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Reproducer for StackOverflowError in Hibernate 7.3.x during {@code SessionFactory}
 * bootstrap when an {@code AttributeConverter} hierarchy uses a same-TypeVariable pass-through
 * across two abstract layers.
 *
 * <h2>Stack trace</h2>
 * <pre>
 * java.lang.StackOverflowError
 *     at org.hibernate.internal.util.GenericsHelper.replaceTypeVariableWithArgument(GenericsHelper.java:245)
 *     at org.hibernate.internal.util.GenericsHelper.substituteTypeArguments(GenericsHelper.java:267)
 *     at org.hibernate.internal.util.GenericsHelper.replaceTypeVariableWithArgument(GenericsHelper.java:254)
 *     at org.hibernate.internal.util.GenericsHelper.substituteTypeArguments(GenericsHelper.java:267)
 *     ... (repeating)
 * </pre>
 *
 * <h2>Trigger pattern</h2>
 * <pre>
 *   AbstractBaseConverter&lt;T&gt;   implements AttributeConverter&lt;T, String&gt;
 *   AbstractMiddleConverter&lt;T&gt; extends AbstractBaseConverter&lt;T&gt;   ← same TypeVariable T
 *   ConcreteStringConverter    extends AbstractMiddleConverter&lt;String&gt;  ← @Converter
 * </pre>
 * When Hibernate registers {@link ConcreteStringConverter} it walks the abstract supertype
 * chain to resolve the type arguments of {@code AttributeConverter}. Because T is passed
 * through unresolved at the middle layer, {@code GenericsHelper.substituteTypeArguments} and
 * {@code GenericsHelper.replaceTypeVariableWithArgument} call each other indefinitely.
 *
 * <h2>Regression</h2>
 * Works in Hibernate 7.2.x. Fails in Hibernate 7.3.4.Final.
 */
class GenericsHelperStackOverflowTest {

	@Test
	void hhh00000Test() {
		assertDoesNotThrow( () -> {
			try ( SessionFactory sf = new MetadataSources(
					new StandardServiceRegistryBuilder()
							.applySetting( "hibernate.hbm2ddl.auto", "create-drop" )
							.build() )
					// Registering this converter is sufficient to trigger the bug — no entity needed.
					.addAnnotatedClass( ConcreteStringConverter.class )
					.buildMetadata()
					.buildSessionFactory() ) {
				// SessionFactory creation is the only step required to reproduce the error.
			}
		} );
	}
}
