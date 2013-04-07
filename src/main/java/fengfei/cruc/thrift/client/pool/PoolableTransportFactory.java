package fengfei.cruc.thrift.client.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PoolableTransportFactory implements PoolableObjectFactory<TTransport> {

	private static Logger log = LoggerFactory
			.getLogger(PoolableTransportFactory.class);
	protected String host = "localhost";
	protected int port = 7915;
	protected int timeout = 10000;
	protected PingCallback pingCallback;
	protected boolean isPing = true;

	public PoolableTransportFactory(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public PoolableTransportFactory(String host, int port, int timeout) {
		super();
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}

	public void destroyObject(TTransport transport) throws Exception {
		if (transport != null) {
			// transport.flush();// need to be validated.
		    try {
		        transport.close();
		    } catch(Throwable e) {
		    }
			log.debug("transport is closed.");
		}
	}

	public abstract boolean isConnected(TTransport transport);

	public boolean validateObject(TTransport transport) {
		// byte[] bytes = new byte[128];
		if (transport != null && isConnected(transport)) {
			// try {
			// transport.write(bytes);
			// transport.flush();
			// return true;
			// } catch (TTransportException e) {
			// e.printStackTrace();
			// return false;
			// }
			if (pingCallback != null && isPing) {
				try {
					log.debug("ping checking.");
					return pingCallback.ping(transport);

				} catch (TTransportException e) {
					log.error("Transport can't ping!", e);
					return false;
				}

			} else {
				return true;
			}
		}

		return false;
	}

	public void activateObject(TTransport transport) throws Exception {
		if (transport == null) {
			log.debug("Transport is invalid.");
		}
		if (transport != null && !transport.isOpen()) {
			transport.open();
			log.debug("transport is opened.");
		}
	}

	public void passivateObject(TTransport transport) throws Exception {
		// TTransport transport = (TTransport) obj;
		// if (transport != null && transport.isOpen()) {
		// transport.flush();
		// }
	}

	public void setPingCallback(PingCallback callback) {
		this.pingCallback = callback;
	}

	public void setPing(boolean isPing) {
		this.isPing = isPing;
	}
}
