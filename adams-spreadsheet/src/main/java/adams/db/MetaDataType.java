/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * MetaDataType.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.db;

/**
 * The types of available meta-data.
 */
public enum MetaDataType {
  BASIC,
  CONNECTION,
  ATTRIBUTES,
  CATALOGS,
  CLIENT_INFO_PROPERTIES,
  COLUMN_PRIVILEGES,
  COLUMNS,
  EXPORTED_KEYS,
  FUNCTION_COLUMNS,
  FUNCTIONS,
  IMPORTED_KEYS,
  INDEX_INFO,
  PRIMARY_KEYS,
  PROCEDURE_COLUMNS,
  PROCEDURES,
  PSEUDO_COLUMNS,
  SCHEMAS,
  SUPER_TABLES,
  SUPER_TYPES,
  TABLES,
  TABLE_TYPES,
  TYPE_INFO,
  USER_DEFINED_TYPES,
  VERSION_COLUMNS
}
