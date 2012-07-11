/**
 * VirtuosoDB client binding for YCSB.
 *
 * Submitted on 24/6/2012.
 *
 * https://gist.github.com/000a66b8db2caf42467b#file_mongo_db.java
 *
 */

package com.yahoo.ycsb.db;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.query.*;
import virtuoso.jena.driver.*;

import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.DBException;
import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.workloads.QueryVirtuosoWorkload;

/**
 * VirtuosoDB client for YCSB framework.
 *
 * Properties to set:
 *
 * virtuoso.url=virtuoso://localhost:1111
 * virtuoso.user=ycsb
 * virtuoso.password=ycsb
 *
 * @author ypai
 *
 */
public class VirtuosoClient extends DB
{
	private VirtGraph virtGraph;
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
		props.getProperty("virtuoso.database");
		user = props.getProperty("virtuoso.user");
		password = props.getProperty("virtuoso.password");

		try
		{
			virtGraph = new VirtGraph(url, user, password);
			//System.out.println("Virtuoso connection created with " + url);
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
	public int read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result)
	{
		String query = QueryVirtuosoWorkload.filters.get(key);
		Query sparql = QueryFactory.create(query);
		sparql.setLimit(1);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, virtGraph);
		try
		{
			ResultSet queryResult = vqe.execSelect();

			QuerySolution solution = queryResult.next();
			for (String field : queryResult.getResultVars())
				result.put(field, new StringByteIterator(solution.get(field).toString()));
			return queryResult != null ? 0 : 1;
		}
		catch (Exception e)
		{
			System.err.println(e.toString() + ":" + query);
			return 1;
		}
		finally
		{
			vqe.close();
		}
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
		String query = QueryVirtuosoWorkload.filters.get(startkey);
		Query sparql = QueryFactory.create(query);
		sparql.setLimit(recordcount);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(sparql, virtGraph);
		try
		{
			ResultSet queryResult = vqe.execSelect();
			for (int i = 0; i < recordcount && queryResult.hasNext(); i++) {
				QuerySolution solution = queryResult.next();
				HashMap<String, ByteIterator> values = new HashMap<String, ByteIterator>();
				for (String field : queryResult.getResultVars()) {
					String value = solution.get(field).toString();
					values.put(field, new StringByteIterator(value));
				}
				result.add(values);
			}

			return queryResult != null ? 0 : 1;
		}
		catch (Exception e)
		{
			System.err.println(e.toString() + ":" + query);
			return 1;
		}
		finally
		{
			vqe.close();
		}
	}
}

