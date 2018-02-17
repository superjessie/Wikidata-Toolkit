package org.wikidata.wdtk.datamodel.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.AbstractTermedStatementDocument;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImplTest;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class AbstractTermedStatementDocumentTest {
	
	private Map<String, List<Statement>> initialStatements = null;
	private Statement statementA = null;
	private String statementIdA = null;
	private String statementIdB = null;
	private Set<String> initialStatementIds = null;
	
	@Before
	public void setUp() {
		List<StatementGroup> groups = DataObjectFactoryImplTest.getTestStatementGroups(2,
				10, 3, EntityIdValue.ET_ITEM);
		initialStatements = new HashMap<>();
		initialStatementIds = new HashSet<>();
		for(StatementGroup group : groups) {
			initialStatements.put(group.getProperty().getId(), group.getStatements());
			if (statementIdA == null) {
				statementA = group.getStatements().get(0);
				statementIdA = statementA.getStatementId();
			}
			statementIdB = group.getStatements().get(0).getStatementId();
			
			for(Statement statement: group.getStatements()) {
				initialStatementIds.add(statement.getStatementId());
			}
		}
	}
	
	@Test
	public void removeNoStatements() {
		Map<String, List<Statement>> removed = AbstractTermedStatementDocument.removeStatements(
				Collections.emptySet(), initialStatements);
		assertEquals(removed, initialStatements);
	}
	
	@Test
	public void removeSomeStatements() {
		Map<String, List<Statement>> removed = AbstractTermedStatementDocument.removeStatements(
				Arrays.asList(statementIdA, statementIdB).stream().collect(Collectors.toSet()), initialStatements);

		Set<String> statementIds = new HashSet<>();
		for(Entry<String,List<Statement>> entry : removed.entrySet()) {
			List<Statement> statements = entry.getValue();
			assertFalse(statements.isEmpty());
			
			for(Statement statement : entry.getValue()) {
				statementIds.add(statement.getStatementId());
			}
		}
		assertFalse(statementIds.contains(statementIdA));
		assertFalse(statementIds.contains(statementIdB));
		initialStatementIds.remove(statementIdA);
		initialStatementIds.remove(statementIdB);
		assertEquals(initialStatementIds, statementIds);
	}
	
	@Test
	public void addExistingStatement() {
		Map<String, List<Statement>> added = AbstractTermedStatementDocument.addStatementToGroups(statementA, initialStatements);
		assertEquals(initialStatements, added);
	}
	
	@Test
	public void addSameStatementWithoutId() {
		Statement copy = new StatementImpl(statementA.getClaim(), statementA.getReferences(), statementA.getRank(), "");
		Map<String, List<Statement>> added = AbstractTermedStatementDocument.addStatementToGroups(copy, initialStatements);
		assertNotEquals(initialStatements, added);
	}
	
	@Test
	public void addFreshStatement() {
		Statement fresh = DataObjectFactoryImplTest.getTestStatement(5, 4, 2, EntityIdValue.ET_ITEM);
		Map<String, List<Statement>> added = AbstractTermedStatementDocument.addStatementToGroups(fresh, initialStatements);
		List<Statement> sameProp = added.get(fresh.getClaim().getMainSnak().getPropertyId().getId());
		assertTrue(sameProp.contains(fresh));
	}
}
