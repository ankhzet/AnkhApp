/*
 * Copyright (C) 2015 Ankh Zet (ankhzet@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ankh.ioc.resolver;

import ankh.ioc.exceptions.FactoryException;

/**
 *
 * @author Ankh Zet (ankhzet@gmail.com)
 */
public interface DependencyNode extends Iterable<DependencyNode> {

  void resolve(Object instance) throws FactoryException;

  void append(DependencyNode node);

  boolean empty();

  DependencyNode next();

  DependencyNode fold();

}
