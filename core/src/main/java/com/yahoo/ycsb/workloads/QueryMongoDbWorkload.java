/**
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */

package com.yahoo.ycsb.workloads;

import java.util.Properties;
import com.yahoo.ycsb.*;
import com.yahoo.ycsb.generator.DiscreteGenerator;
import com.yahoo.ycsb.generator.IntegerGenerator;
import com.yahoo.ycsb.generator.UniformIntegerGenerator;
import com.yahoo.ycsb.generator.ZipfianGenerator;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class QueryMongoDbWorkload extends Workload
{
	/**
	 * The name of the property for the proportion of transactions that are reads.
	 */
	public static final String READ_PROPORTION_PROPERTY="readproportion";

	/**
	 * The default proportion of transactions that are reads.	
	 */
	public static final String READ_PROPORTION_PROPERTY_DEFAULT="0.95";

	/**
	 * The name of the property for the proportion of transactions that are scans.
	 */
	public static final String SCAN_PROPORTION_PROPERTY="scanproportion";

	/**
	 * The default proportion of transactions that are scans.
	 */
	public static final String SCAN_PROPORTION_PROPERTY_DEFAULT="0.0";

	/**
	 * The name of the property for the min scan length (number of records)
	 */
	public static final String MIN_SCAN_LENGTH_PROPERTY="minscanlength";

	/**
	 * The default min scan length.
	 */
	public static final String MIN_SCAN_LENGTH_PROPERTY_DEFAULT="1";

	/**
	 * The name of the property for the max scan length (number of records)
	 */
	public static final String MAX_SCAN_LENGTH_PROPERTY="maxscanlength";

	/**
	 * The default max scan length.
	 */
	public static final String MAX_SCAN_LENGTH_PROPERTY_DEFAULT="1000";

	/**
	 * The name of the property for the scan length distribution. Options are "uniform" and "zipfian" (favoring short scans)
	 */
	public static final String SCAN_LENGTH_DISTRIBUTION_PROPERTY="scanlengthdistribution";

	/**
	 * The default max scan length.
	 */
	public static final String SCAN_LENGTH_DISTRIBUTION_PROPERTY_DEFAULT="uniform";

	DiscreteGenerator operationchooser;

	IntegerGenerator scanlength;

	public static String table;
	public static HashMap<String, DBObject> filters;

	public void init(Properties p) throws WorkloadException
	{
		System.out.println("Init MongoDb Queries");
		filters = new HashMap<String, DBObject>();

		double readproportion=Double.parseDouble(p.getProperty(READ_PROPORTION_PROPERTY,READ_PROPORTION_PROPERTY_DEFAULT));
		double scanproportion=Double.parseDouble(p.getProperty(SCAN_PROPORTION_PROPERTY,SCAN_PROPORTION_PROPERTY_DEFAULT));
		int minscanlength=Integer.parseInt(p.getProperty(MIN_SCAN_LENGTH_PROPERTY,MIN_SCAN_LENGTH_PROPERTY_DEFAULT));
		int maxscanlength=Integer.parseInt(p.getProperty(MAX_SCAN_LENGTH_PROPERTY,MAX_SCAN_LENGTH_PROPERTY_DEFAULT));
		String scanlengthdistrib=p.getProperty(SCAN_LENGTH_DISTRIBUTION_PROPERTY,SCAN_LENGTH_DISTRIBUTION_PROPERTY_DEFAULT);

		String[] queries = p.getProperty("queries").split(";");
		for (int i = 0; i < queries.length; i++)
		{
			filters.put(String.valueOf(i), (BasicDBObject)JSON.parse(queries[i]));
		}

		operationchooser=new DiscreteGenerator();
		if (readproportion>0)
		{
			operationchooser.addValue(readproportion,"READ");
		}

		if (scanproportion>0)
		{
			operationchooser.addValue(scanproportion,"SCAN");
		}

		if (scanlengthdistrib.compareTo("uniform")==0)
		{
			scanlength=new UniformIntegerGenerator(minscanlength,maxscanlength);
		}
		else if (scanlengthdistrib.compareTo("zipfian")==0)
		{
			scanlength=new ZipfianGenerator(minscanlength,maxscanlength);
		}
		else
		{
			throw new WorkloadException("Distribution \""+scanlengthdistrib+"\" not allowed for scan length");
		}
	}

	public boolean doInsert(DB db, Object threadstate)
	{
		return true;
	}

	/**
	 * Do one transaction operation. Because it will be called concurrently from multiple client threads, this 
	 * function must be thread safe. However, avoid synchronized, or the threads will block waiting for each 
	 * other, and it will be difficult to reach the target throughput. Ideally, this function would have no side
	 * effects other than DB operations.
	 */
	public boolean doTransaction(DB db, Object threadstate)
	{
		String op=operationchooser.nextString();

		if (op.compareTo("READ")==0)
		{
			doTransactionRead(db);
		}
		else if (op.compareTo("SCAN")==0)
		{
			doTransactionScan(db);
		}

		return true;
	}

	public void doTransactionRead(DB db)
	{
		table = "artepxs";

		HashMap<String, ByteIterator> results = new HashMap<String, ByteIterator>();

		Random random = new Random();
		int max = filters.size();
		int randomNum = random.nextInt(max);

		db.read(table, String.valueOf(randomNum), null, results);

		//System.out.println("Read: " + randomNum + ", 1");
	}

	public void doTransactionScan(DB db)
	{
		table = "artepxs";

		Vector<HashMap<String, ByteIterator>> results = new Vector<HashMap<String, ByteIterator>>();

		Random random = new Random();
		int max = filters.size();
		int queryNum = random.nextInt(max);

		int len=scanlength.nextInt();

		db.scan(table, String.valueOf(queryNum), len, null, results);

		//System.out.println("Scan: " + queryNum + ", " + results.size());
	}
}
