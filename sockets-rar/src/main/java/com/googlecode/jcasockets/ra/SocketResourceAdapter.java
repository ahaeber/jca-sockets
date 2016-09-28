/*
 * Copyright 2009 Mark Jeffrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.jcasockets.ra;

import com.googlecode.jcasockets.ra.inflow.SocketActivationSpec;
import com.googlecode.jcasockets.ra.inflow.SocketListener;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.Connector;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.resource.spi.work.WorkManager;
import javax.transaction.xa.XAResource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Connector(
        displayName = "Socket Resource Adapter",
        description = "",
        vendorName = "Systems Architects SPRL",
        eisType = "General Socket Server",
        version = "2.0.0",
        reauthenticationSupport = false)
public class SocketResourceAdapter implements ResourceAdapter {
	private static Map<ActivationSpec, SocketListener> socketListeners = new ConcurrentHashMap<>();

	private WorkManager workManager;
	private final Logger logger = Logger.getLogger(SocketResourceAdapter.class.getName());

    @ConfigProperty(
            description = "The default encoding for the input and output readers of the socket",
            defaultValue = "ISO-8859-1")
	private String defaultEncoding;
    @ConfigProperty(
            description = "The default maximum number of simultaneous connections",
            defaultValue = "100")
    private Integer defaultMaximumConnections;
    @ConfigProperty(
            description = "The default timeout in milliseconds to get a connection from the pool",
            defaultValue = "1000")
    private Integer defaultConnectionTimeoutMilliseconds;
    @ConfigProperty(
            description = "The default Hostname or IP adddress to listen on",
            defaultValue = "localhost")
	private String defaultIpAddress;

	public SocketResourceAdapter() {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SocketResourceAdapter that = (SocketResourceAdapter) o;

		if (workManager != null ? !workManager.equals(that.workManager) : that.workManager != null) return false;
		if (logger != null ? !logger.equals(that.logger) : that.logger != null) return false;
		if (defaultEncoding != null ? !defaultEncoding.equals(that.defaultEncoding) : that.defaultEncoding != null)
			return false;
		if (defaultMaximumConnections != null ? !defaultMaximumConnections.equals(that.defaultMaximumConnections) : that.defaultMaximumConnections != null)
			return false;
		if (defaultConnectionTimeoutMilliseconds != null ? !defaultConnectionTimeoutMilliseconds.equals(that.defaultConnectionTimeoutMilliseconds) : that.defaultConnectionTimeoutMilliseconds != null)
			return false;
		return defaultIpAddress != null ? defaultIpAddress.equals(that.defaultIpAddress) : that.defaultIpAddress == null;
	}

	@Override
	public int hashCode() {
		int result = workManager != null ? workManager.hashCode() : 0;
		result = 31 * result + (logger != null ? logger.hashCode() : 0);
		result = 31 * result + (defaultEncoding != null ? defaultEncoding.hashCode() : 0);
		result = 31 * result + (defaultMaximumConnections != null ? defaultMaximumConnections.hashCode() : 0);
		result = 31 * result + (defaultConnectionTimeoutMilliseconds != null ? defaultConnectionTimeoutMilliseconds.hashCode() : 0);
		result = 31 * result + (defaultIpAddress != null ? defaultIpAddress.hashCode() : 0);
		return result;
	}

	@Override
	public void start(BootstrapContext ctx) throws ResourceAdapterInternalException {
		logger.info("start");
    	workManager = ctx.getWorkManager();
		for (SocketListener socketListener: socketListeners.values()) {
			socketListener.start();
		}
	}

	@Override
	public void stop() {
		logger.info("stop");
		for (SocketListener socketListener: socketListeners.values()) {
			socketListener.release();
		}
	}

	@Override
	public void endpointActivation(MessageEndpointFactory messageEndpointFactory, ActivationSpec activationSpec)
			throws  ResourceException
    {
		logger.info("endpointActivation");
		if (!(activationSpec instanceof SocketActivationSpec)) {
			throw new NotSupportedException("Invalid spec, Should be a " + SocketActivationSpec.class.getName() + " was: " + activationSpec);
		}
		SocketActivationSpec socketActivationSpec = (SocketActivationSpec) activationSpec;
		createSocketActivationSpec(socketActivationSpec);
		SocketListener socketListener = new SocketListener(workManager, socketActivationSpec, messageEndpointFactory);
		addToKnownListeners(socketActivationSpec, socketListener);
		try {
			socketListener.start();
		} catch (ResourceException e) {
			socketListener.release();
			throw e;
		}
	}

	private void addToKnownListeners(SocketActivationSpec socketActivationSpec, SocketListener socketListener) throws NotSupportedException {
		SocketListener previousValue = socketListeners.putIfAbsent(socketActivationSpec, socketListener);
		if ( previousValue!= null ){
			throw new NotSupportedException( "A socket activation spec already exists with the same port: \n " 
					+ " previous: " + previousValue 
					+ " this: " + socketActivationSpec 
					);
		}
	}

	private void createSocketActivationSpec(SocketActivationSpec socketActivationSpec) {
		if ( socketActivationSpec.getIpAddress() == null){
			socketActivationSpec.setIpAddress(defaultIpAddress);
		}
		if ( socketActivationSpec.getEncoding() == null){
			socketActivationSpec.setEncoding(defaultEncoding);
		}
		if ( socketActivationSpec.getMaximumConnections() <= 0){
			socketActivationSpec.setMaximumConnections(defaultMaximumConnections);
		}
		if ( socketActivationSpec.getConnectionTimeoutMilliseconds() <= 0){
			socketActivationSpec.setConnectionTimeoutMilliseconds(defaultConnectionTimeoutMilliseconds);
		}
	}

	@Override
	public void endpointDeactivation(MessageEndpointFactory endpointFactory, ActivationSpec spec) {
		stop();
		logger.info("endpointDeactivation");
		// nothing to do.
	}

	@Override
	public XAResource[] getXAResources(ActivationSpec[] arg0) throws ResourceException {
		return new XAResource[0]; // XA is unsupported
	}

    public String getDefaultIpAddress() {
        return defaultIpAddress;
    }

	public void setDefaultIpAddress(String defaultIpAddress) {
		logger.info("Default ipAddress (may be overridden when activated later) is: " + defaultIpAddress);
		this.defaultIpAddress = defaultIpAddress;
	}

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

	public void setDefaultEncoding(String defaultEncoding) {
		logger.info("Default encoding (may be overridden when activated later) is: " + defaultEncoding);
		this.defaultEncoding = defaultEncoding;
	}

    public Integer getDefaultMaximumConnections() {
        return defaultMaximumConnections;
    }

	public void setDefaultMaximumConnections(Integer defaultMaximumConnections) {
		logger.info("Default maximumConnections (may be overridden when activated later) is: " + defaultMaximumConnections);
		this.defaultMaximumConnections = defaultMaximumConnections;
	}

    public Integer getDefaultConnectionTimeoutMilliseconds() {
        return defaultConnectionTimeoutMilliseconds;
    }

	public void setDefaultConnectionTimeoutMilliseconds(Integer defaultConnectionTimeoutMilliseconds) {
		logger.info("Default connectionTimeoutMilliseconds (may be overridden when activated later) is: " + defaultConnectionTimeoutMilliseconds);
		this.defaultConnectionTimeoutMilliseconds = defaultConnectionTimeoutMilliseconds;
	}

	private void blah() {

	}
}
