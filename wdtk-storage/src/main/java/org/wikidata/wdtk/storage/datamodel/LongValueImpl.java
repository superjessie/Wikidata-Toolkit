package org.wikidata.wdtk.storage.datamodel;

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

public class LongValueImpl implements LongValue {

	final long value;
	final Sort sort;

	public LongValueImpl(long value, Sort sort) {
		this.value = value;
		this.sort = sort;
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public long getLong() {
		return this.value;
	}

}
