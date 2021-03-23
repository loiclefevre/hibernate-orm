/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.mapping.converted.converter.lob;

import org.hibernate.cfg.AvailableSettings;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Steve Ebersole
 */
@ServiceRegistry( settings = @Setting( name = AvailableSettings.USE_SECOND_LEVEL_CACHE, value = "false" ) )
@DomainModel( annotatedClasses = Address.class )
@SessionFactory
public class ConverterAndLobTest {

	@Test
	@TestForIssue( jiraKey = "HHH-9615" )
	public void basicTest(SessionFactoryScope scope) {

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// test during store...
		PostalAreaConverter.clearCounts();

		scope.inTransaction(
				(session) -> session.save( new Address( 1, "123 Main St.", null, PostalArea._78729 ) )
		);

		assertThat( PostalAreaConverter.toDatabaseCallCount, is(1) );
		assertThat( PostalAreaConverter.toDomainCallCount, is(0) );

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// test during load...
		PostalAreaConverter.clearCounts();

		scope.inTransaction(
				(session) -> session.get( Address.class, 1 )
		);

		assertThat( PostalAreaConverter.toDatabaseCallCount, is(0) );
		assertThat( PostalAreaConverter.toDomainCallCount, is(1) );
	}

	@AfterEach
	public void dropTestData(SessionFactoryScope scope) {
		scope.inTransaction(
				(session) -> session.createQuery( "delete Address" ).executeUpdate()
		);
	}

}
