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
 * StreamableTextBasedDataContainerReader.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.data.container.DataContainer;

/**
 * Indicator interface for {@link StreamableDataContainerReader} classes that use text-based data.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface StreamableTextBasedDataContainerReader<T extends DataContainer>
  extends StreamableDataContainerReader<T> {

}
