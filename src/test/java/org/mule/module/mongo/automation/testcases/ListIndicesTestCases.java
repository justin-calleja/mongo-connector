package org.mule.module.mongo.automation.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;
import org.mule.module.mongo.api.IndexOrder;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ListIndicesTestCases extends MongoTestParent {

	@Before
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("createCollection");
			MessageProcessor flow = lookupFlowConstruct("create-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			testObjects = (HashMap<String, Object>) context.getBean("createIndex");
			flow = lookupFlowConstruct("create-index");
			response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({SanityTests.class})
	@Test
	public void testListIndices() {
		try {
		
			String indexKey = testObjects.get("field").toString();
			IndexOrder indexOrder = IndexOrder.valueOf(testObjects.get("order").toString());
			
			String indexName = indexKey + "_" + indexOrder.getValue();
			
			MessageProcessor flow = lookupFlowConstruct("list-indices");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			
			List<BasicDBObject> payload = (List<BasicDBObject>) response.getMessage().getPayload();
			assertTrue(payload != null);
			assertTrue(existsInList(payload, "_id_")); // Automatically generated by MongoDB
			assertTrue(existsInList(payload, indexName));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	private boolean existsInList(List<BasicDBObject> objects, String indexName) {
		for (BasicDBObject obj : objects) {
			if (obj.get("name").equals(indexName)) {
				return true;
			}
		}
		return false;
	}
	
	@After
	public void tearDown() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("dropCollection");
			MessageProcessor flow = lookupFlowConstruct("drop-collection");
			MuleEvent response = flow.process(getTestEvent(testObjects));
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}