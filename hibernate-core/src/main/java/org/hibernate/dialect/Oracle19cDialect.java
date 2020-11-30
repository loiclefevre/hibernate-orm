/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.service.ServiceRegistry;

import java.sql.Types;
import java.util.List;

/**
 * An SQL dialect for Oracle Converged Database 19c.
 *
 * @author loiclefevre (loic.lefevre@gmail.com)
 */
public class Oracle19cDialect extends Oracle12cDialect {

	// Tests:
	// - ./gradlew sDB -Pdb=oracle
	// - ./gradlew clean test

	public static final String MAX_STRING_SIZE_EXTENDED = "hibernate.dialect.oracle.max_string_size_extended";

	public Oracle19cDialect() {
		super();

		// register missing functions
	}

	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.contributeTypes(typeContributions, serviceRegistry);

		// Use setString / getString for CLOB and NCLOB
//		typeContributions.contributeType(MaterializedClobType.INSTANCE, "String", String.class.getName());
//		typeContributions.contributeType(MaterializedNClobType.INSTANCE, "String", String.class.getName());

		// account for Oracle's max_string_size = EXTENDED
		boolean hasMaxStringSizeExtendedEnabled = serviceRegistry.getService(ConfigurationService.class).getSetting(
				MAX_STRING_SIZE_EXTENDED,
				StandardConverters.BOOLEAN,
				false
		);

		if (hasMaxStringSizeExtendedEnabled) {
			registerColumnType(Types.CHAR, "char(1 char)");
			registerColumnType(Types.VARCHAR, 32767, "varchar2($l char)");
			registerColumnType(Types.VARCHAR, "long");
			registerColumnType(Types.NVARCHAR, "nvarchar2($l)");
			registerColumnType(Types.LONGNVARCHAR, "nvarchar2($l)");
		}
		else {
			super.registerCharacterTypeMappings();
		}
	}

	@Override
	public String getNativeIdentifierGeneratorStrategy() {
		return "identity";
	}

	@Override
	public String getCurrentTimestampSelectString() {
		return "select current_timestamp from dual";
	}

	@Override
	protected void registerCharacterTypeMappings() {
	}

	@Override
	protected void registerLargeObjectTypeMappings() {
		registerColumnType(Types.BINARY, 2000, "raw($l)");

		registerColumnType(Types.VARBINARY, 2000, "raw($l)");

		registerColumnType(Types.BLOB, "blob");
		registerColumnType(Types.CLOB, "clob");

		registerColumnType(Types.LONGVARCHAR, "clob");
		registerColumnType(Types.LONGVARBINARY, "blob");
	}

	@Override
	public void augmentRecognizedTableTypes(List<String> tableTypesList) {
		super.augmentRecognizedTableTypes( tableTypesList );
		tableTypesList.add( "MATERIALIZED VIEW" );
	}
}
