package org.wikidata.wdtk.storage.db;

/*
 * #%L
 * Wikidata Toolkit Storage
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;

import org.wikidata.wdtk.storage.datamodel.Sort;

class DbSortData implements Serializable {
	private static final long serialVersionUID = -261524859509400983L;

	public final Sort sort;
	public final int id;
	public final boolean useDictionary;

	public DbSortData(Sort sort, int id, boolean useDictionary) {
		this.sort = sort;
		this.id = id;
		this.useDictionary = useDictionary;
	}
}