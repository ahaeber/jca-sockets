package com.googlecode.jcasockets.sample;

import com.googlecode.jcasockets.SocketMessage;
import com.googlecode.jcasockets.SocketMessageEndpoint;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.logging.Logger;

@MessageDriven(
		name = "SOCKET_MDB",
		activationConfig = {
				@ActivationConfigProperty(propertyName = "port", propertyValue = "1024"),
				@ActivationConfigProperty(propertyName = "encoding", propertyValue = "UTF-8"),
				@ActivationConfigProperty(propertyName = "maximumConnections", propertyValue = "5"),
				@ActivationConfigProperty(propertyName = "connectionTimeoutMilliseconds", propertyValue = "10000")},
		description = "Socket message driven bean")
public class SocketMessageDrivenBean implements SocketMessageEndpoint {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(SocketMessageDrivenBean.class.getName());

	public SocketMessageDrivenBean() {
		log.info("Construct MDB");
	}

	public void ejbRemove() throws EJBException {
		log.info("ejbRemove");
	}

	public void ejbCreate() throws EJBException {
		log.info("EJBCreate");
	}

	public void setMessageDrivenContext(MessageDrivenContext ctx) throws EJBException {
        log.info("setMessageDrivenContext");
	}

	@Override
	public void onMessage(SocketMessage socketMessage) throws Exception {
		LineNumberReader in = new LineNumberReader(socketMessage.getReader());
		final PrintStream socketOutput = new PrintStream(socketMessage.getOutputStream());
		String line;
		int size = 0;
		while ((line = in.readLine()) != null) {
			if ( "EXIT".equals( line )){
				socketMessage.getRawSocket().close();
				break;	
			}else{
				socketOutput.println(line);
				size += line.length();
				socketOutput.flush();
			}
		}
		log.info("Processed message size was: " + size );
	}
}
