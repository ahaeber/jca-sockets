package com.googlecode.jcasockets.ra.inflow;

import com.googlecode.jcasockets.ra.SocketResourceAdapter;

import javax.resource.ResourceException;
import javax.resource.spi.endpoint.MessageEndpointFactory;

/**
 * SocketActivation
 *
 * @version $Revision: $
 */
public class SocketActivation {

   private SocketResourceAdapter ra;

   private SocketActivationSpec spec;

   private MessageEndpointFactory endpointFactory;

   /**
    * Default constructor
    * @exception ResourceException Thrown if an error occurs
    */
   public SocketActivation() throws ResourceException {
      this(null, null, null);
   }

   /**
    * Constructor
    * @param ra SocketResourceAdapter
    * @param endpointFactory MessageEndpointFactory
    * @param spec SocketActivationSpec
    * @exception ResourceException Thrown if an error occurs
    */
   public SocketActivation(SocketResourceAdapter ra, 
      MessageEndpointFactory endpointFactory,
      SocketActivationSpec spec) throws ResourceException
   {
      this.ra = ra;
      this.endpointFactory = endpointFactory;
      this.spec = spec;
   }

   /**
    * Get activation spec class
    * @return Activation spec
    */
   public SocketActivationSpec getActivationSpec()
   {
      return spec;
   }

   /**
    * Get message endpoint factory
    * @return Message endpoint factory
    */
   public MessageEndpointFactory getMessageEndpointFactory()
   {
      return endpointFactory;
   }

   /**
    * Start the activation
    * @throws ResourceException Thrown if an error occurs
    */
   public void start() throws ResourceException {

   }

   /**
    * Stop the activation
    */
   public void stop() {

   }

}
