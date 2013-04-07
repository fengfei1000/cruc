package fengfei.cruc.thrift.client.pool;

import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransportPool {

	// private final static String TRANSPORT_BIO = "BIO";
	// private final static String TRANSPORT_NIO = "NIO";
	// private final static String TRANSPORT_ALL = "ALL";
	// private String transportType = "BIO";
	private static TransportPool instance;
	private static Logger log = LoggerFactory.getLogger(TransportPool.class);
	private int tryTimes = 4;

	public synchronized static TransportPool get() {
		if (instance == null) {
			instance = new TransportPool();
			TransportPoolConfig config = TransportPoolConfig.getInstance();
			instance.initPool(config);
		}
		return instance;
	}

	public synchronized static TransportPool get(Properties props) {
		if (instance == null) {
			instance = new TransportPool();
			TransportPoolConfig config = new TransportPoolConfig(props);
			instance.initPool(config);
		}
		return instance;
	}

	private ObjectPool<TTransport> bioPool;
	private ObjectPool<TTransport> nioPool;
	private PoolableSocketFactory socketFactory;

	private TransportPool() {

	}

	/**
	 * get a client socket from pool
	 * 
	 * @return
	 */
	public TNonblockingSocket borrowNonblockingSocket() {
		TNonblockingSocket transport = null;
		try {
			transport = (TNonblockingSocket) nioPool.borrowObject();
			log.debug("return TTransport instance to pool:" + transport);
		} catch (NoSuchElementException e) {
			log.error("borrow Object TTransport error.", e);
			throw e;
		} catch (IllegalStateException e) {
			log.error("borrow Object TTransport error.", e);
			throw e;
		} catch (Exception e) {
			log.error("borrow Object TTransport error.", e);
			throw new IllegalStateException("Can't borrow Object TTransport.",
					e);
		}
		if (transport == null || !transport.isOpen()) {
			throw new NullPointerException(
					"Can't borrow Object TTransport.The Transport is null.");
		}
		return transport;
	}

	/**
	 * get a client socket from pool
	 * 
	 * @return
	 */
	public TSocket borrowSocket() {
		TSocket transport = null;
		try {
			TryborrowSocket socket = new TryborrowSocket();
			transport = socket.borrowSocket(getTryTimes(), null);
			// transport = (TSocket) bioPool.borrowObject();
			if (transport == null) {
				throw new NullPointerException(
						"borrow Object TTransport error,transport is null.");
			}
			log.debug(String.format(
					"get TTransport instance form pool:Active=%s,Idle=%s,(%s)",
					bioPool.getNumActive(), bioPool.getNumIdle(), transport));
			// } catch (NoSuchElementException e) {
			// log.error("borrow Object TTransport error.", e);
			// throw e;
			// } catch (IllegalStateException e) {
			// log.error("borrow Object TTransport error.", e);
			// throw e;
		} catch (Exception e) {
			log.error("borrow Object TTransport error.", e);
			throw new IllegalStateException("Can't borrow Object TTransport.",
					e);
		}

		return transport;
	}

	protected class TryborrowSocket {
		private int times = 0;

		public TSocket borrowSocket(int tryTimes, Exception exception)
				throws Exception {
			TSocket transport = null;
			times++;
			if (times > tryTimes) {
				if (exception != null) {
					throw exception;
				}
				return transport;
			}
			try {
				transport = (TSocket) bioPool.borrowObject();
				// throw new Exception();
				return transport;
			} catch (NoSuchElementException e) {
				log.error("borrow Object TTransport error.", e);
				transport = borrowSocket(tryTimes, e);
				return transport;
			} catch (IllegalStateException e) {
				log.error("borrow Object TTransport error.", e);
				transport = borrowSocket(tryTimes, e);
				return transport;
			} catch (Exception e) {
				log.error("borrow Object TTransport error.", e);
				transport = borrowSocket(tryTimes, e);
				return transport;
			}
		}

	}

	public ObjectPool<TTransport> getBioPool() {
		return bioPool;
	}

	public ObjectPool<TTransport> getNioPool() {
		return nioPool;
	}

	private void initPool(TransportPoolConfig config) {
		log.info("Initializing transport pool...");
		// if (TRANSPORT_BIO.equalsIgnoreCase(transportType)) {
		socketFactory = new PoolableSocketFactory(config.getHost(),
				config.getPort(), config.getTimeout());
		socketFactory.setPing(config.isPing);
		bioPool = new GenericObjectPool<>(socketFactory, config.get());
		tryTimes = config.getTryTimes();
		// } else {
		// throw new UnsupportedOperationException(
		// "The transportation type couldn't be suported.");
		// }
		log.info("Initialized.");
	}

	/**
	 * return a client nonblockingSocket
	 * 
	 * @param borrowed
	 */
	public void returnTransport(TNonblockingSocket borrowed) {
		if (borrowed == null) {
			return;
		}
		try {
			nioPool.returnObject(borrowed);
		} catch (Exception e) {
			log.error("return Object TTransport error:" + borrowed, e);
		}
	}

	/**
	 * return a client socket
	 * 
	 * @param borrowed
	 */
	public void returnTransport(TSocket borrowed) {
		if (borrowed == null) {
			if (borrowed == null) {
				log.error("the transport is null, can't return pool.");
			}
			return;
		}
		try {
			bioPool.returnObject(borrowed);
		} catch (Exception e) {
			log.error("return Object TTransport error:" + borrowed, e);
		}
	}

	public void setPingCallback(PingCallback callback) {
		socketFactory.setPingCallback(callback);
	}

	public synchronized void destory() {
		instance = null;
		if (nioPool != null && nioPool instanceof GenericObjectPool) {
			try {
				((GenericObjectPool<TTransport>) nioPool).close();
			} catch (Exception e) {
			}
		}
		nioPool = null;
		if (bioPool != null && (bioPool instanceof GenericObjectPool)) {
			try {
				((GenericObjectPool<TTransport>) bioPool).close();
			} catch (Exception e) {
			}
		}
		bioPool = null;
	}

	public int getTryTimes() {
		return tryTimes;
	}
}
