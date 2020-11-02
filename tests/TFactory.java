/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tests;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcLiteHttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransport;

/**
 *
 * @author grigory
 */
public class TFactory extends XmlRpcLiteHttpTransportFactory{

    	public TFactory(XmlRpcClient pClient) {
		super(pClient);
	}
        
    public XmlRpcTransport getTransport() { return new Transport(getClient()); }

}
