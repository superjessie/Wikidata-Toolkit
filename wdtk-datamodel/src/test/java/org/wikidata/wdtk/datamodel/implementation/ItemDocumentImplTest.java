package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class ItemDocumentImplTest {

	ItemDocument ir1;
	ItemDocument ir2;

	Statement s;

	ItemIdValue iid;
	List<StatementGroup> statementGroups;
	List<SiteLink> sitelinks;

	@Before
	public void setUp() throws Exception {
		iid = new ItemIdValueImpl("Q42", "http://wikibase.org/entity/");

		Claim c = new ClaimImpl(iid, new SomeValueSnakImpl(
				new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.<SnakGroup> emptyList());
		s = new StatementImpl(c, Collections.<Reference> emptyList(),
				StatementRank.NORMAL, "MyId");
		StatementGroup sg = new StatementGroupImpl(Collections.singletonList(s));
		statementGroups = Collections.singletonList(sg);

		SiteLink sl = new SiteLinkImpl("Douglas Adams", "enwiki",
				Collections.<String> emptyList());
		sitelinks = Collections.singletonList(sl);

		ir1 = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks, 1234);
		ir2 = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks, 1234);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(ir1.getItemId(), iid);
		assertEquals(ir1.getEntityId(), iid);
		assertEquals(ir1.getStatementGroups(), statementGroups);
		assertEquals(ir1.getSiteLinks().values().stream().collect(Collectors.toList()),
				sitelinks);
	}

	@Test
	public void equalityBasedOnContent() {
		ItemDocument irDiffStatementGroups = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), sitelinks, 1234);
		ItemDocument irDiffSiteLinks = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, Collections.<SiteLink> emptyList(),
				1234);
		ItemDocument irDiffRevisions = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks, 1235);

		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), new DatatypeIdImpl(
						DatatypeIdValue.DT_STRING), 1234);

		// we need to use empty lists of Statement groups to test inequality
		// based on different item ids with all other data being equal
		ItemDocument irDiffItemIdValue = new ItemDocumentImpl(
				new ItemIdValueImpl("Q23", "http://example.org/"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), sitelinks, 1234);

		assertEquals(ir1, ir1);
		assertEquals(ir1, ir2);
		assertThat(ir1, not(equalTo(irDiffStatementGroups)));
		assertThat(ir1, not(equalTo(irDiffSiteLinks)));
		assertThat(ir1, not(equalTo(irDiffRevisions)));
		assertThat(irDiffStatementGroups, not(equalTo(irDiffItemIdValue)));
		assertFalse(ir1.equals(pr));
		assertThat(ir1, not(equalTo(null)));
		assertFalse(ir1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(ir1.hashCode(), ir2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new ItemDocumentImpl(null,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks, 1234);
	}

	@Test
	public void labelsCanBeNull() {
		ItemDocument doc = new ItemDocumentImpl(iid, null,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getLabels().isEmpty());
	}

	@Test
	public void descriptionsNotNull() {
		ItemDocument doc = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(), null,
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getDescriptions().isEmpty());
	}

	@Test
	public void aliasesCanBeNull() {
		ItemDocument doc =new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), null,
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getAliases().isEmpty());
	}

	@Test
	public void statementGroupsCanBeNull() {
		ItemDocument doc = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), null,
				sitelinks, 1234);
		assertTrue(doc.getStatementGroups().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		ItemIdValue iid2 = new ItemIdValueImpl("Q23", "http://example.org/");
		Claim c2 = new ClaimImpl(iid2, new SomeValueSnakImpl(
				new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.<SnakGroup> emptyList());
		Statement s2 = new StatementImpl(c2,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"MyId");
		StatementGroup sg2 = new StatementGroupImpl(
				Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<StatementGroup>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups2, sitelinks, 1234);
	}

	@Test(expected = NullPointerException.class)
	public void sitelinksNotNull() {
		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, null, 1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = ir1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}
	
	@Test
	public void testWithRevisionId() {
		assertEquals(1235L, ir1.withRevisionId(1235L).getRevisionId());
		assertEquals(ir1, ir1.withRevisionId(1325L).withRevisionId(ir1.getRevisionId()));
	}
	
	@Test
	public void testWithLabelInNewLanguage() {
		MonolingualTextValue newLabel = new MonolingualTextValueImpl(
				"Item Q42", "fr");
		ItemDocument withLabel = ir1.withLabel(newLabel);
		assertEquals("Item Q42", withLabel.findLabel("fr"));
	}
	
	@Test
	public void testWithDescriptionInNewLanguage() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"l'item 42 bien connu", "fr");
		ItemDocument withDescription = ir1.withDescription(newDescription);
		assertEquals("l'item 42 bien connu", withDescription.findDescription("fr"));
	}

	@Test
	public void testWithOverridenDescription() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"eine viel bessere Beschreibung", "de");
		ItemDocument withDescription = ir1.withDescription(newDescription);
		assertEquals("eine viel bessere Beschreibung", withDescription.findDescription("de"));
	}
	
	@Test
	public void testWithAliasInNewLanguage() {
		MonolingualTextValue newAlias = new MonolingualTextValueImpl(
				"Item42", "fr");
		ItemDocument withAliases = ir1.withAliases("fr", Collections.singletonList(newAlias));
		assertEquals(Collections.singletonList(newAlias), withAliases.getAliases().get("fr"));
	}

	@Test
	public void testWithOverridenAliases() {
		MonolingualTextValue newAlias = new MonolingualTextValueImpl(
				"A new alias of Q42", "en");

		ItemDocument withAlias = ir1.withAliases("en", Collections.singletonList(newAlias));
		assertEquals(Collections.singletonList(newAlias), withAlias.getAliases().get("en"));
	}
	
	@Test
	public void testAddStatement() {
		Statement fresh = DataObjectFactoryImplTest.getTestStatement(5, 4, 2, EntityIdValue.ET_ITEM);
		Claim claim = fresh.getClaim();
		assertFalse(ir1.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
		ItemDocument withStatement = ir1.withStatement(fresh);
		assertTrue(withStatement.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
	}
	
	@Test
	public void testDeleteStatements() {
		Statement toRemove = statementGroups.get(0).getStatements().get(0);
		ItemDocument withoutStatement = ir1.withoutStatementIds(Collections.singleton(toRemove.getStatementId()));
		assertNotEquals(withoutStatement, ir1);
	}

}
