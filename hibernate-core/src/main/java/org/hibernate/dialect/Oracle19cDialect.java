/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.identity.Oracle19cIdentityColumnSupport;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.StandardBasicTypes;

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

	protected boolean hasMaxStringSizeExtendedEnabled;

	public Oracle19cDialect() {
		super();

		registerKeywords();

		// register missing functions
	}

	protected void registerKeywords() {
		// from https://docs.oracle.com/en/database/oracle/oracle-database/19/sqlrf/Oracle-SQL-Reserved-Words.html
		registerKeyword("all");
		registerKeyword("alter");
		registerKeyword("and");
		registerKeyword("any");
		registerKeyword("access");
		registerKeyword("add");
		registerKeyword("as");
		registerKeyword("asc");
		registerKeyword("audit");
		registerKeyword("between");
		registerKeyword("by");
		registerKeyword("char");
		registerKeyword("check");
		registerKeyword("cluster");
		registerKeyword("column_value");
		registerKeyword("comment");
		registerKeyword("compress");
		registerKeyword("connect");
		registerKeyword("create");
		registerKeyword("date");
		registerKeyword("decimal");
		registerKeyword("default");
		registerKeyword("delete");
		registerKeyword("desc");
		registerKeyword("distinct");
		registerKeyword("drop");
		registerKeyword("else");
		registerKeyword("exclusive");
		registerKeyword("exists");
		registerKeyword("file");
		registerKeyword("float");
		registerKeyword("for");
		registerKeyword("from");
		registerKeyword("grant");
		registerKeyword("group");
		registerKeyword("having");
		registerKeyword("identified");
		registerKeyword("immediate");
		registerKeyword("in");
		registerKeyword("index");
		registerKeyword("initial");
		registerKeyword("insert");
		registerKeyword("integer");
		registerKeyword("intersect");
		registerKeyword("into");
		registerKeyword("is");
		registerKeyword("like");
		registerKeyword("level");
		registerKeyword("lock");
		registerKeyword("long");
		registerKeyword("maxextents");
		registerKeyword("minus");
		registerKeyword("mlslabel");
		registerKeyword("mode");
		registerKeyword("modify");
		registerKeyword("nested_table_id");
		registerKeyword("noaudit");
		registerKeyword("nocompress");
		registerKeyword("not");
		registerKeyword("nowait");
		registerKeyword("null");
		registerKeyword("number");
		registerKeyword("of");
		registerKeyword("offline");
		registerKeyword("on");
		registerKeyword("online");
		registerKeyword("option");
		registerKeyword("or");
		registerKeyword("order");
		registerKeyword("pctfree");
		registerKeyword("prior");
		registerKeyword("public");
		registerKeyword("raw");
		registerKeyword("rename");
		registerKeyword("resource");
		registerKeyword("revoke");
		registerKeyword("rowid");
		registerKeyword("rownum");
		registerKeyword("select");
		registerKeyword("session");
		registerKeyword("set");
		registerKeyword("share");
		registerKeyword("size");
		registerKeyword("smallint");
		registerKeyword("start");
		registerKeyword("successful");
		registerKeyword("synonym");
		registerKeyword("sysdate");
		registerKeyword("table");
		registerKeyword("then");
		registerKeyword("to");
		registerKeyword("trigger");
		registerKeyword("uid");
		registerKeyword("union");
		registerKeyword("unique");
		registerKeyword("update");
		registerKeyword("validate");
		registerKeyword("values");
		registerKeyword("varchar");
		registerKeyword("varchar2");
		registerKeyword("view");
		registerKeyword("where");
		registerKeyword("with");
	}

	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.contributeTypes(typeContributions, serviceRegistry);

		// account for Oracle's max_string_size = EXTENDED
		this.hasMaxStringSizeExtendedEnabled = serviceRegistry.getService(ConfigurationService.class).getSetting(
				MAX_STRING_SIZE_EXTENDED,
				StandardConverters.BOOLEAN,
				false
		);

		// called from parent constructor
		registerColumnType(Types.CHAR, "char(1 char)");
		registerColumnType(Types.VARCHAR, hasMaxStringSizeExtendedEnabled ? 32767 : 4000, "varchar2($l char)");
		registerColumnType(Types.NVARCHAR, "nvarchar2($l)");
		registerColumnType(Types.LONGNVARCHAR, "nvarchar2($l)");
	}

	@Override
	public String getNativeIdentifierGeneratorStrategy() {
		return "identity";
	}

	@Override
	public String getCurrentTimestampSelectString() {
		return "select current_timestamp from dual";
	}

	/**
	 * Avoids setting these from parent constructor
	 *
	 * @see #contributeTypes
	 */
	@Override
	protected void registerCharacterTypeMappings() {
	}

	@Override
	protected void registerLargeObjectTypeMappings() {
		registerColumnType(Types.BINARY, 2000, "raw($l)");

		registerColumnType(Types.VARBINARY, 2000, "raw($l)");

		registerColumnType(Types.CLOB, "clob");
		registerColumnType(Types.BLOB, "blob");

		registerColumnType(Types.LONGVARCHAR, "clob");
		registerColumnType(Types.LONGVARBINARY, "blob");

		registerHibernateType(Types.CLOB, StandardBasicTypes.TEXT.getName());
		registerHibernateType(Types.BLOB, StandardBasicTypes.IMAGE.getName());
	}

	@Override
	public void augmentRecognizedTableTypes(List<String> tableTypesList) {
		super.augmentRecognizedTableTypes(tableTypesList);
		tableTypesList.add("MATERIALIZED VIEW");
	}

	@Override
	public IdentityColumnSupport getIdentityColumnSupport() {
		return new Oracle19cIdentityColumnSupport();
	}
}
