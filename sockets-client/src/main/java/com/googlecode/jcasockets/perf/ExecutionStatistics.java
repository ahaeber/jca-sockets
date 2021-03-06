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
package com.googlecode.jcasockets.perf;

import java.util.concurrent.TimeUnit;


public class ExecutionStatistics {
	
	private int minimumMessageSize = Integer.MAX_VALUE;
	private int maximumMessageSize = Integer.MIN_VALUE;  
	private long bytesReceived;
	private long bytesSent;
	private int messagesSent;
	private int messagesReceived;
	private final StopWatch sendingTimeStopWatch;

	public ExecutionStatistics(TimeProvider timeProvider) {
		sendingTimeStopWatch = new StopWatch(timeProvider);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append( "Total messages=" + messagesReceived );
		return sb.toString();
	}
	
	public void recordSend( String send ){
		
		int bytes = send.length() * 2;
		if( bytes < minimumMessageSize){
			minimumMessageSize = bytes;
		}
		if( bytes > maximumMessageSize){
			maximumMessageSize = bytes;
		}
		bytesSent += bytes;
		messagesSent++;
		sendingTimeStopWatch.start();
	}
	public void recordReceive( String receive ){
		sendingTimeStopWatch.stop();
		bytesReceived += receive.length() * 2;
		messagesReceived++;
	}
	public int getMinimumMessageSize() {
		return minimumMessageSize;
	}
	public int getMaximumMessageSize() {
		return maximumMessageSize;
	}
	public long getBytesReceived() {
		return bytesReceived;
	}
	public long getBytesSent() {
		return bytesSent;
	}
	
	
	public int getMessagesSent() {
		return messagesSent;
	}
	public int getMessagesReceived() {
		return messagesReceived;
	}
	public void combine(ExecutionStatistics that) {
		this.minimumMessageSize = Math.min(this.minimumMessageSize, that.minimumMessageSize);
		this.maximumMessageSize = Math.max(this.maximumMessageSize, that.maximumMessageSize);
		this.bytesReceived += that.bytesReceived;
		this.bytesSent += that.bytesSent;
		this.messagesSent += that.messagesSent;
		this.messagesReceived += that.messagesReceived;
		this.sendingTimeStopWatch.combine( that.sendingTimeStopWatch);
	}

	public long getElapsed(TimeUnit timeUnit) {
		return sendingTimeStopWatch.getElapsed(timeUnit);
	}

	public int getBytesSentPerSecond() {
		return calculateCountPerSecond( bytesSent );
	}
	public int getBytesReceivedPerSecond() {
		return calculateCountPerSecond( bytesReceived );
	}
	public int getMessagesPerSecond() {
		return calculateCountPerSecond( messagesReceived );
	}
	private int calculateCountPerSecond( long count ){
		double millis = getElapsed(TimeUnit.MILLISECONDS);
		return (int) Math.round((1000f * (count)/ millis)); 
	}

}
