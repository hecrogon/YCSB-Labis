/**
 * VirtuosoDB client binding for YCSB.
 *
 * Submitted by Yen Pai on 5/11/2010.
 *
 * https://gist.github.com/000a66b8db2caf42467b#file_mongo_db.java
 *
 */

package com.yahoo.ycsb.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import virtuoso.jena.driver.*;

import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.ByteIterator;
//import com.yahoo.ycsb.workloads.QueryVirtuosoDbWorkload;

/**
 * VirtuosoDB client for YCSB framework.
 *
 * Properties to set:
 *
 * virtuoso.url=virtuoso://localhost:27017
 * virtuoso.database=ycsb
 * virtuoso.writeConcern=normal
 *
 * @author ypai
 *
 */
public class VirtuosoDbClient extends DB
{
	private VirtGraph virtGraph;
	private String database;
	private String user;
	private String password;

	/**
	 * Initialize any state for this DB. Called once per DB instance; there is
	 * one DB instance per client thread.
	 */
	public void init() throws DBException
	{
		Properties props = getProperties();
		String url = props.getProperty("virtuoso.url");
		database = props.getProperty("virtuoso.database");
		user = props.getProperty("virtuoso.user");
		password = props.getProperty("virtuoso.password");

		try
		{
//			virtGraph = new VirtGraph("jdbc:virtuoso://localhost:1111", "dba", "dba");
			virtGraph = new VirtGraph(url, user, password);

			System.out.println("Virtuoso connection created with " + url);
		}
		catch (Exception e1)
		{
			System.err.println("Could not initialize VirtuosoDB connection pool for Loader: " + e1.toString());
			e1.printStackTrace();
		}
	}

	@Override
	/**
	 * Delete a record from the database.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to delete.
	 * @return Zero on success, a non-zero error code on error. See this class's description for a discussion of error codes.
	 */
	public int delete(String table, String key)
	{
		return 0;
	}

	@Override
	/**
	 * Insert a record in the database. Any field/value pairs in the specified values HashMap will be written into the record with the specified
	 * record key.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to insert.
	 * @param values A HashMap of field/value pairs to insert in the record
	 * @return Zero on success, a non-zero error code on error. See this class's description for a discussion of error codes.
	 */
	public int insert(String table, String key, HashMap<String, ByteIterator> values)
	{
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	/**
	 * Read a record from the database. Each field/value pair from the result will be stored in a HashMap.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to read.
	 * @param fields The list of fields to read, or null for all of them
	 * @param result A HashMap of field/value pairs for the result
	 * @return Zero on success, a non-zero error code on error or "not found".
	 */
	public int read(String table, String key, Set<String> fields,
			HashMap<String, ByteIterator> result) {

		Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100");

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, virtGraph);

		ResultSet results = vqe.execSelect();
      while (results.hasNext())
		{
			QuerySolution res = results.nextSolution();
			RDFNode graph = res.get("graph");
			RDFNode s = res.get("s");
			RDFNode p = res.get("p");
			RDFNode o = res.get("o");
			System.out.println(graph + " { " + s + " " + p + " " + o + " . }");
		}

		return 0;
/*
		com.mongodb.DB db = null;
		try {
			db = mongo.getDB(database);

			db.requestStart();

			HashMap<String, DBObject> query = (HashMap<String, DBObject>)QueryMongoDbWorkload.filters.get(key);

			DBCollection collection = db.getCollection(table);

			DBObject fieldsToReturn = new BasicDBObject();
			boolean returnAllFields = fields == null;

			DBObject queryResult = null;
			if (!returnAllFields) {
				Iterator<String> iter = fields.iterator();
				while (iter.hasNext()) {
					fieldsToReturn.put(iter.next(), 1);
				}
				queryResult = collection.findOne((DBObject)query, fieldsToReturn);
			} else {
				queryResult = collection.findOne((DBObject)query);
			}

			if (queryResult != null) {
				result.putAll(queryResult.toMap());
			}
			return queryResult != null ? 0 : 1;
		} catch (Exception e) {
			System.err.println(e.toString());
			return 1;
		} finally {
			if (db!=null)
			{
				db.requestDone();
			}
		}
*/
	}


	@Override
	/**
	 * Update a record in the database. Any field/value pairs in the specified values HashMap will be written into the record with the specified
	 * record key, overwriting any existing values with the same field name.
	 *
	 * @param table The name of the table
	 * @param key The record key of the record to write.
	 * @param values A HashMap of field/value pairs to update in the record
	 * @return Zero on success, a non-zero error code on error. See this class's description for a discussion of error codes.
	 */
	public int update(String table, String key, HashMap<String, ByteIterator> values)
	{
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	/**
	 * Perform a range scan for a set of records in the database. Each field/value pair from the result will be stored in a HashMap.
	 *
	 * @param table The name of the table
	 * @param startkey The record key of the first record to read.
	 * @param recordcount The number of records to read
	 * @param fields The list of fields to read, or null for all of them
	 * @param result A Vector of HashMaps, where each HashMap is a set field/value pairs for one record
	 * @return Zero on success, a non-zero error code on error. See this class's description for a discussion of error codes.
	 */
	public int scan(String table, String startkey, int recordcount, Set<String> fields, Vector<HashMap<String, ByteIterator>> result)
	{
		Query sparql = QueryFactory.create("SELECT * WHERE { GRAPH ?graph { ?s ?p ?o } } limit 100");

		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, virtGraph);

		ResultSet results = vqe.execSelect();
      while (results.hasNext())
		{
			QuerySolution res = results.nextSolution();
			RDFNode graph = res.get("graph");
			RDFNode s = res.get("s");
			RDFNode p = res.get("p");
			RDFNode o = res.get("o");
			System.out.println(graph + " { " + s + " " + p + " " + o + " . }");
		}

		return 0;
/*
		com.mongodb.DB db=null;
		try {
			db = mongo.getDB(database);
			db.requestStart();

			HashMap<String, DBObject> query = (HashMap<String, DBObject>)QueryMongoDbWorkload.filters.get(startkey);

			DBCollection collection = db.getCollection(table);

			DBCursor cursor = collection.find((DBObject)query).limit(recordcount);
			while (cursor.hasNext()) {
				//toMap() returns a Map, but result.add() expects a Map<String,String>. Hence, the suppress warnings.
				result.add((HashMap<String, ByteIterator>) cursor.next().toMap());
			}

			return 0;
		} catch (Exception e) {
			System.err.println(e.toString());
			return 1;
		}
		finally
		{
			if (db!=null)
			{
				db.requestDone();
			}
		}
*/
	}
}

