package fengfei.cruc.thrift.client.pool;

import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PoolableNIOSocketFactory extends PoolableTransportFactory {
	private static Logger log = LoggerFactory
			.getLogger(PoolableNIOSocketFactory.class);

	public PoolableNIOSocketFactory(String host, int port) {
		super(host, port);
	}

	public PoolableNIOSocketFactory(String host, int port, int timeout) {
		super(host, port, timeout);
	}

	@Override
	public TTransport makeObject() throws Exception {
		TTransport transport = new TNonblockingSocket(host, port);
		return transport;
	}

	public void activateObject(TTransport transport) throws Exception {
		TNonblockingSocket nonblockingSocket = (TNonblockingSocket) transport;
		if (nonblockingSocket != null && !nonblockingSocket.isOpen()) {
			nonblockingSocket.startConnect();
			nonblockingSocket.finishConnect();
			log.debug("transport is opened.");
		}
	}

	@Override
	public boolean isConnected(TTransport transport) {
		TNonblockingSocket socket = (TNonblockingSocket) transport;
		return socket.isOpen();

	}
}
