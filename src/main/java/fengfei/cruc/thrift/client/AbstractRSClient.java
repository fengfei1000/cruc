package fengfei.cruc.thrift.client;

import java.io.IOException;
import java.util.Properties;

import javax.management.relation.RelationException;
import javax.management.relation.RelationService;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.cruc.thrift.client.pool.PingCallback;
import fengfei.cruc.thrift.client.pool.TransportPool;

public abstract class AbstractRSClient<C extends TServiceClient> {

	private static Logger log = LoggerFactory.getLogger(AbstractRSClient.class);
	private static TransportPool transportPool;
	protected int tryTimes = 3;

	public AbstractRSClient() {
		transportPool = TransportPool.get();
		this.init();
	}

	public AbstractRSClient(Properties props) {
		transportPool = TransportPool.get(props);
		System.out.println("transportPool:" + transportPool);
		this.init();
	}

	protected void init() {
		transportPool.setPingCallback(new PingCallback() {

			@Override
			public boolean ping(TTransport transport)
					throws TTransportException {
				try {
					return AbstractRSClient.this.ping(transport);
				} catch (RelationException e) {
					log.error("Transport can't ping!", e);
					return false;
				} catch (TException e) {
					log.error("Transport can't ping!", e);
					return false;
				}
			}
		});
	}

	public <T> T execute(ClientCallback<T, C> callback)
			throws RelationException, TException, Exception {
		TTransport transport = null;
		try {

			transport = transportPool.borrowSocket();

			C client = getClient(transport);
			T t = callback.execute(client);
			transport.flush();
			return t;

		} catch (TException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			returnTransport(transport);
		}
	}

	/**
	 * get client connection
	 * 
	 * @param transport
	 * @return
	 * @throws TTransportException
	 */
	protected C getClient(TTransport transport) throws TTransportException {
		TFramedTransport framedTransport = new TFramedTransport(transport);
		TProtocol protocol = new TBinaryProtocol(framedTransport);
		// transport.open();

		return newTServiceClient(protocol);
	}

	public abstract C newTServiceClient(TProtocol protocol);

	protected void returnTransport(TTransport transport) {
		if (transport == null) {
			log.error("the transport is null, can't return pool.");
			return;
		}
		if (transport instanceof TSocket) {
			TSocket socket = (TSocket) transport;
			transportPool.returnTransport(socket);
		} else if (transport instanceof TNonblockingSocket) {
			TNonblockingSocket socket = (TNonblockingSocket) transport;
			transportPool.returnTransport(socket);
		} else {
			throw new UnsupportedOperationException(
					"The transportation couldn't be supported.");
		}
	}

	public void destory() {
		if (transportPool != null)
			transportPool.destory();
	}

	protected boolean ping(TTransport transport) throws RelationException,
			TException {
		C client = getClient(transport);
		return true;
//		String ok = client.ping("");
//
//		if (ok == null || "".equals(ok)) {
//			return false;
//		} else {
//			log.debug("ping:" + ok);
//			return true;
//		}
	}
}
