package fengfei.cruc.avro;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

public class Client {
	public static void main(String[] args) throws IOException {
		NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(
				8022));
		// client code - attach to the server and send a message
		CrucProtocol proxy = (CrucProtocol) SpecificRequestor.getClient(
				CrucProtocol.class, client);
		System.out.println("Client built, got proxy");

		// fill in the Message record and send it

		System.out.println("Calling proxy.ping with message:  "
				+ proxy.ping("hello"));

		// cleanup
		client.close();
	}
}
