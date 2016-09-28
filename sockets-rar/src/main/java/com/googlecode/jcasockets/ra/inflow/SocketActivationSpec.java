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
package com.googlecode.jcasockets.ra.inflow;

import com.googlecode.jcasockets.SocketMessageEndpoint;
import com.googlecode.jcasockets.ra.SocketResourceAdapter;

import javax.resource.ResourceException;
import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Logger;

@Activation(messageListeners = SocketMessageEndpoint.class)
public class SocketActivationSpec implements ActivationSpec, Serializable {
	private final Logger logger = Logger.getLogger(SocketActivationSpec.class.getName());

	@ConfigProperty(description = "The Socket Listener Port", defaultValue = "1024")
	@NotNull
	private Integer port;
	private String encoding;
	private Integer maximumConnections;
	private Integer connectionTimeoutMilliseconds;

	private SocketResourceAdapter ra;

	private String ipAddress;

	public SocketActivationSpec() {
		logger.info("Creating Activation spec: " + this);
	}

	@Override
	public ResourceAdapter getResourceAdapter() {
		return ra;
	}

	@Override
	public void setResourceAdapter(ResourceAdapter resourceAdapter) throws ResourceException {
		this.ra = (SocketResourceAdapter) resourceAdapter;
	}

	@Override
	public void validate() throws InvalidPropertyException {
	    if ( encoding == null ){
	    	return;
	    }
	    Map<String,Charset> availableCharsets = Charset.availableCharsets( );
	    if (  !availableCharsets.containsKey( encoding ) ){
	    	throw new InvalidPropertyException( "Encoding " + encoding + " is unknown. It should be one of: " + availableCharsets.keySet( ) );
	    }
	}

	boolean accepts(String recipientAddress) throws InvalidPropertyException {
		return true; // accept anything
	}

	public void setIpAddress(String ipAddress) {
		logger.fine("Setting ipAddress: " + ipAddress);
		this.ipAddress = ipAddress;
	}
	public void setPort(Integer port) {
		logger.fine("Setting port: " + port);
		this.port = port;
	}

	public void setEncoding(String encoding) {
		logger.fine("Setting encoding: " + encoding);
		this.encoding = encoding;
	}

	public void setConnectionTimeoutMilliseconds(Integer connectionTimeoutMilliseconds) {
		doSetConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
	}

	public void setMaximumConnections(Integer maximumConnections) {
		doSetMaximumConnections(maximumConnections);
	}

	private void doSetConnectionTimeoutMilliseconds(Integer connectionTimeoutMilliseconds) {
		logger.fine("Setting connection timeout milliseconds: " + connectionTimeoutMilliseconds);
		this.connectionTimeoutMilliseconds = connectionTimeoutMilliseconds;
	}

	private void doSetMaximumConnections(Integer maximumConnections) {
		logger.fine("Setting maximum connections: " + maximumConnections);
		this.maximumConnections = maximumConnections;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Integer getPort() {
		return port;
	}

	public Integer getMaximumConnections() {
		return maximumConnections;
	}

	public Integer getConnectionTimeoutMilliseconds() {
		return connectionTimeoutMilliseconds;
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + " ipAddress=" + ipAddress +  ",port=" + port + ",encoding=" + encoding 
		+ ",maximumConnections=" + maximumConnections 
		+ ",connectionTimeoutMilliseconds=" + connectionTimeoutMilliseconds 
		+ "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SocketActivationSpec that = (SocketActivationSpec) o;

		if (logger != null ? !logger.equals(that.logger) : that.logger != null) return false;
		if (port != null ? !port.equals(that.port) : that.port != null) return false;
		if (encoding != null ? !encoding.equals(that.encoding) : that.encoding != null) return false;
		if (maximumConnections != null ? !maximumConnections.equals(that.maximumConnections) : that.maximumConnections != null)
			return false;
		if (connectionTimeoutMilliseconds != null ? !connectionTimeoutMilliseconds.equals(that.connectionTimeoutMilliseconds) : that.connectionTimeoutMilliseconds != null)
			return false;
		if (ra != null ? !ra.equals(that.ra) : that.ra != null) return false;
		return ipAddress != null ? ipAddress.equals(that.ipAddress) : that.ipAddress == null;
	}

	@Override
	public int hashCode() {
		int result = logger != null ? logger.hashCode() : 0;
		result = 31 * result + (port != null ? port.hashCode() : 0);
		result = 31 * result + (encoding != null ? encoding.hashCode() : 0);
		result = 31 * result + (maximumConnections != null ? maximumConnections.hashCode() : 0);
		result = 31 * result + (connectionTimeoutMilliseconds != null ? connectionTimeoutMilliseconds.hashCode() : 0);
		result = 31 * result + (ra != null ? ra.hashCode() : 0);
		result = 31 * result + (ipAddress != null ? ipAddress.hashCode() : 0);
		return result;
	}
}
