/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.googlecode.jcasockets;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.jca.arquillian.embedded.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RunWith(Arquillian.class)
@Configuration(autoActivate = true)
public class ResourceAdapterTest {

	@Deployment
	public static EnterpriseArchive deploy() throws Exception {
		
		JavaArchive rarjar = ShrinkWrap.create(JavaArchive.class, "rar.jar")
				.addPackage("com.googlecode.jcasockets.ra");

		ResourceAdapterArchive rar = ShrinkWrap.create(ResourceAdapterArchive.class, "socket-ra.rar")
				.addAsLibrary(rarjar);

        //rar.addAsManifestResource("simple.rar/META-INF/ra.xml", "ra.xml");

		
/*
		JavaArchive ejbjar = ShrinkWrap.create(JavaArchive.class, "ejb.jar")
				.addClasses(
						FileEvent.class,
						FSWatcherMDB.class)
				.addAsManifestResource("jboss-ejb3.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
*/

		JavaArchive libjar = ShrinkWrap.create(JavaArchive.class, "lib.jar")
				.addClasses(ResourceAdapterTest.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

		return ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
				.addAsModules(
						rar)
//						, ejbjar)
				.addAsLibraries(libjar);
	}

	@Before
	public void init() throws Exception {

	}

	@Test
	@InSequence(1)
	public void testConnect() throws Exception {
		String hostName = "localhost";
		int portNumber = 25;

		try (
				Socket smtpSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(smtpSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
		) {
			in.readLine();
			out.println("HELO localhost");

/*
			S: 220 smtp.server.com Simple Mail Transfer Service Ready
			C: EHLO client.example.com
			S: 250-smtp.server.com Hello client.example.com
			S: 250-SIZE 1000000
			S: 250 AUTH LOGIN PLAIN CRAM-MD5
			C: AUTH LOGIN
			S: 334 VXNlcm5hbWU6
			C: adlxdkej
			S: 334 UGFzc3dvcmQ6
			C: lkujsefxlj
			S: 235 2.7.0 Authentication successful
*/
		}
	}
	
}