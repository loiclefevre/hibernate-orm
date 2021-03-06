/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;

import java.sql.Types;

/**
 * An SQL dialect for Oracle Converged Database 21c.
 *
 * @author loiclefevre (loic.lefevre@gmail.com)
 */
public class Oracle21cDialect extends Oracle19cDialect {

	public Oracle21cDialect() {
		super();
		registerColumnType(Types.JAVA_OBJECT, "json");
	}
}
