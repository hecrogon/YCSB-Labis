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
import com.yahoo.ycsb.generator.CounterGenerator;
import com.yahoo.ycsb.generator.DiscreteGenerator;
import com.yahoo.ycsb.generator.ExponentialGenerator;
import com.yahoo.ycsb.generator.Generator;
import com.yahoo.ycsb.generator.ConstantIntegerGenerator;
import com.yahoo.ycsb.generator.HotspotIntegerGenerator;
import com.yahoo.ycsb.generator.HistogramGenerator;
import com.yahoo.ycsb.generator.IntegerGenerator;
import com.yahoo.ycsb.generator.ScrambledZipfianGenerator;
import com.yahoo.ycsb.generator.SkewedLatestGenerator;
import com.yahoo.ycsb.generator.UniformIntegerGenerator;
import com.yahoo.ycsb.generator.ZipfianGenerator;
import com.yahoo.ycsb.measurements.Measurements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class QueryMongoDbWorkload extends Workload
{
	public static String table;
	public static HashMap filter;
	public static HashMap filters;

	public void init(Properties p) throws WorkloadException
	{
		System.out.println("Init MongoDb Queries");

		int numqueries = Integer.parseInt(p.getProperty("queries"));
		for (int i = 0; i < numqueries; i++)
		{
			String p1 = p.getProperty("query" + i);
			System.out.println("I1 " + p1);
		}


		filter = new HashMap<String, String>();
		filters = new HashMap<String, String>();
		filter.put("MUNICIPIOS", "24230_ZOTES_DEL_PARAMO");
		filter.put("GRUPOS_DE_EDAD_OCUPACION_PRINCIPAL", "TODAS_LAS_EDADES");

//		filter.put("MUNICIPIOS", "24002_ALGADEFE");

		filters.put("key1", filter);
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
		System.out.println("Transaction " + table);
		table = "artepxs";

		HashMap results = new HashMap<String, ByteIterator>();
		db.read(table, "key1", null, results);

		System.out.println("Res " + results.size());
		return true;
	}
}
