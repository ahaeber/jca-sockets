package com.googlecode.jcasockets.ra;

import com.googlecode.jcasockets.ra.inflow.SocketActivationSpec;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;
import org.unitils.mock.Mock;

import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;

public class SocketResourceAdapterTest extends UnitilsJUnit4 {
	
	@TestedObject
	SocketResourceAdapter socketResourceAdapter = new SocketResourceAdapter();
	
	@InjectIntoByType
	Mock<WorkManager> workManager;

	Mock<MessageEndpointFactory> messageEndpointFactory;
	
	@Before
	public void setUp(){
		socketResourceAdapter.setDefaultEncoding("encoding");
		socketResourceAdapter.setDefaultMaximumConnections(5);
		socketResourceAdapter.setDefaultConnectionTimeoutMilliseconds(7);
	}

	@Test(expected=ResourceException.class)
	public void testEndPointActivationOfDuplicateSpecsFails() throws ResourceException {
		int port = 100;
		SocketActivationSpec socketActivationSpec1 = createActivationSpec(port);
		SocketActivationSpec socketActivationSpec2 = createActivationSpec(port);
		
		socketResourceAdapter.endpointActivation(messageEndpointFactory.getMock(), socketActivationSpec1);
		socketResourceAdapter.endpointActivation(messageEndpointFactory.getMock(), socketActivationSpec2);
	}

	private SocketActivationSpec createActivationSpec(int port) {
		SocketActivationSpec socketActivationSpec1 = new SocketActivationSpec();
		socketActivationSpec1.setPort(port);
		return socketActivationSpec1;
	}
}
