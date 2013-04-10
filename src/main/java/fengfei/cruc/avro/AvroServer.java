package fengfei.cruc.avro;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import fengfei.cruc.CrucServer;

public class AvroServer implements CrucServer {
	private Server server;

	@Override
	public boolean start() {
		server = new NettyServer(new SpecificResponder(CrucProtocol.class,
				new CrucProtocolImpl()), new InetSocketAddress(8022));

		return false;
	}

	@Override
	public boolean restart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new NettyServer(new SpecificResponder(
				CrucProtocol.class, new CrucProtocolImpl()),
				new InetSocketAddress(8022));
		server.start();
	}

}
