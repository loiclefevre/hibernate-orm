/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect.identity;

import org.hibernate.dialect.Dialect;
import org.hibernate.id.PostInsertIdentityPersister;

import java.sql.Types;

/**
 * @author loiclefevre (loic.lefevre@gmail.com)
 */
public class Oracle19cIdentityColumnSupport extends Oracle12cIdentityColumnSupport {

	@Override
	public boolean hasDataTypeInIdentityColumn() {
		return false;
	}

	@Override
	public String getIdentityColumnString(int type) {
		return "number not null";
	}

	@Override
	public String getIdentityInsertString() {
		return "generated as identity";
	}

	@Override
	public String getIdentitySelectString(String table, String column, int type) {
		return "select currval('" + table + '_' + column + "_seq')";
	}

}
