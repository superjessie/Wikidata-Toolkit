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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.Serializer;
import org.wikidata.wdtk.util.Timer;

public class PropertyDictionary implements Dictionary<PropertySignature> {

	final Timer timerGetId;

	protected final Atomic.Integer nextId;
	protected final DatabaseManager databaseManager;
	final Map<Integer, PropertySignature> values;
	final Map<PropertySignature, Integer> ids;

	PropertySignature[] valueCache;
	final Map<PropertySignature, Integer> idCache;

	public PropertyDictionary(DatabaseManager databaseManager) {
		Validate.notNull(databaseManager, "database manager cannot be null");

		this.databaseManager = databaseManager;

		nextId = databaseManager.getDb().getAtomicInteger("properties-inc");

		Serializer<PropertySignature> serializer = new PropertySignatureSerializer();

		this.values = databaseManager.getDb()
				.createTreeMap("properties-values")
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(serializer).makeOrGet();
		this.ids = databaseManager.getDb().createHashMap("properties-ids")
				.keySerializer(serializer).makeOrGet();

		int maxKey = 0;
		for (Integer key : this.values.keySet()) {
			if (key > maxKey) {
				maxKey = key;
			}
		}
		this.valueCache = new PropertySignature[maxKey + 1];
		this.idCache = new HashMap<>(maxKey + 1);

		this.timerGetId = Timer.getNamedTimer("Id-get-property");
	}

	@Override
	public Iterator<PropertySignature> iterator() {
		return this.values.values().iterator();
	}

	@Override
	public PropertySignature getValue(int id) {
		PropertySignature result = null;

		if (this.valueCache.length > id) {
			result = this.valueCache[id];
		}
		if (result == null) {
			result = this.values.get(id);
			if (this.valueCache.length <= id) {
				int newSize = Math.max(id + 1, this.valueCache.length * 2);
				this.valueCache = Arrays.copyOf(this.valueCache, newSize);
			}
			this.valueCache[id] = result;
		}

		return result;
	}

	@Override
	public int getId(PropertySignature value) {
		this.timerGetId.start();
		Integer result = this.idCache.get(value);
		if (result == null) {
			result = this.ids.get(value);
			this.idCache.put(value, result);
		}
		this.timerGetId.stop();
		if (this.timerGetId.getMeasurements() % 100000 == 0) {
			System.out.println(this.timerGetId);
		}
		return (result == null) ? -1 : result;
	}

	@Override
	public int getOrCreateId(PropertySignature value) {
		int id = getId(value);
		if (id == -1) {
			id = this.nextId.incrementAndGet();
			this.values.put(id, value);
			this.ids.put(value, id);
		}
		return id;
	}

}
