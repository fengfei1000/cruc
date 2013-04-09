package fengfei.cruc.avro;

import org.apache.avro.ipc.Server;

import fengfei.cruc.CrucServer;

public class AvroServer implements CrucServer {
	private Server server;

	@Override
	public boolean start() {
//		server = new NettyServer(new SpecificResponder(Mail.class,
//				new MailImpl()), new InetSocketAddress(65111));
	 
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
		// TODO Auto-generated method stub

	}

}
