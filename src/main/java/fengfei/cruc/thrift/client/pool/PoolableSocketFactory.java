package fengfei.cruc.thrift.client.pool;

import java.net.Socket;

import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class PoolableSocketFactory extends PoolableTransportFactory {

	public PoolableSocketFactory(String host, int port) {
		super(host, port);
	}

	public PoolableSocketFactory(String host, int port, int timeout) {
		super(host, port, timeout);
	}

	@Override
	public TTransport makeObject() throws Exception {
		Socket socket = new Socket(host, port);
		socket.setKeepAlive(true);
		socket.setSoTimeout(timeout);
		TSocket transport = new TSocket(socket);
		// new TSocket(host, port, timeout);

		return transport;
	}

	public boolean isConnected(TTransport transport) {
		TSocket tSocket = (TSocket) transport;
		Socket socket = tSocket.getSocket();
		return socket != null && socket.isBound() && !socket.isClosed()
				&& socket.isConnected() && !socket.isInputShutdown()
				&& !socket.isOutputShutdown();
	}

}
