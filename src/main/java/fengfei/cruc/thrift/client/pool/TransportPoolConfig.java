package fengfei.cruc.thrift.client.pool;

import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang.math.NumberUtils.toInt;
import static org.apache.commons.lang.math.NumberUtils.toLong;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * When coupled with the appropriate {@link PoolableObjectFactory},
 * <tt>GenericObjectPool</tt> provides robust pooling functionality for
 * arbitrary objects.
 * <p>
 * A <tt>GenericObjectPool</tt> provides a number of configurable parameters:
 * <ul>
 * <li>
 * {@link #setMaxActive <i>maxActive</i>} controls the maximum number of objects
 * that can be allocated by the pool (checked out to clients, or idle awaiting
 * checkout) at a given time. When non-positive, there is no limit to the number
 * of objects that can be managed by the pool at one time. When
 * {@link #setMaxActive <i>maxActive</i>} is reached, the pool is said to be
 * exhausted. The default setting for this parameter is 8.</li>
 * <li>
 * {@link #setMaxIdle <i>maxIdle</i>} controls the maximum number of objects
 * that can sit idle in the pool at any time. When negative, there is no limit
 * to the number of objects that may be idle at one time. The default setting
 * for this parameter is 8.</li>
 * <li>
 * {@link #setWhenExhaustedAction <i>whenExhaustedAction</i>} specifies the
 * behavior of the {@link #borrowObject} method when the pool is exhausted:
 * <ul>
 * <li>
 * When {@link #setWhenExhaustedAction <i>whenExhaustedAction</i>} is
 * {@link #WHEN_EXHAUSTED_FAIL}, {@link #borrowObject} will throw a
 * {@link NoSuchElementException}</li>
 * <li>
 * When {@link #setWhenExhaustedAction <i>whenExhaustedAction</i>} is
 * {@link #WHEN_EXHAUSTED_GROW}, {@link #borrowObject} will create a new object
 * and return it (essentially making {@link #setMaxActive <i>maxActive</i>}
 * meaningless.)</li>
 * <li>
 * When {@link #setWhenExhaustedAction <i>whenExhaustedAction</i>} is
 * {@link #WHEN_EXHAUSTED_BLOCK}, {@link #borrowObject} will block (invoke
 * {@link Object#wait()}) until a new or idle object is available. If a positive
 * {@link #setMaxWait <i>maxWait</i>} value is supplied, then
 * {@link #borrowObject} will block for at most that many milliseconds, after
 * which a {@link NoSuchElementException} will be thrown. If {@link #setMaxWait
 * <i>maxWait</i>} is non-positive, the {@link #borrowObject} method will block
 * indefinitely.</li>
 * </ul>
 * The default <code>whenExhaustedAction</code> setting is
 * {@link #WHEN_EXHAUSTED_BLOCK} and the default <code>maxWait</code> setting is
 * -1. By default, therefore, <code>borrowObject</code> will block indefinitely
 * until an idle instance becomes available.</li>
 * <li>
 * When {@link #setTestOnBorrow <i>testOnBorrow</i>} is set, the pool will
 * attempt to validate each object before it is returned from the
 * {@link #borrowObject} method. (Using the provided factory's
 * {@link PoolableObjectFactory#validateObject} method.) Objects that fail to
 * validate will be dropped from the pool, and a different object will be
 * borrowed. The default setting for this parameter is <code>false.</code></li>
 * <li>
 * When {@link #setTestOnReturn <i>testOnReturn</i>} is set, the pool will
 * attempt to validate each object before it is returned to the pool in the
 * {@link #returnObject} method. (Using the provided factory's
 * {@link PoolableObjectFactory#validateObject} method.) Objects that fail to
 * validate will be dropped from the pool. The default setting for this
 * parameter is <code>false.</code></li>
 * </ul>
 * <p>
 * Optionally, one may configure the pool to examine and possibly evict objects
 * as they sit idle in the pool and to ensure that a minimum number of idle
 * objects are available. This is performed by an "idle object eviction" thread,
 * which runs asynchronously. Caution should be used when configuring this
 * optional feature. Eviction runs contend with client threads for access to
 * objects in the pool, so if they run too frequently performance issues may
 * result. The idle object eviction thread may be configured using the following
 * attributes:
 * <ul>
 * <li>
 * {@link #setTimeBetweenEvictionRunsMillis
 * <i>timeBetweenEvictionRunsMillis</i>} indicates how long the eviction thread
 * should sleep before "runs" of examining idle objects. When non-positive, no
 * eviction thread will be launched. The default setting for this parameter is
 * -1 (i.e., idle object eviction is disabled by default).</li>
 * <li>
 * {@link #setMinEvictableIdleTimeMillis <i>minEvictableIdleTimeMillis</i>}
 * specifies the minimum amount of time that an object may sit idle in the pool
 * before it is eligible for eviction due to idle time. When non-positive, no
 * object will be dropped from the pool due to idle time alone. This setting has
 * no effect unless <code>timeBetweenEvictionRunsMillis > 0.</code> The default
 * setting for this parameter is 30 minutes.</li>
 * <li>
 * {@link #setTestWhileIdle <i>testWhileIdle</i>} indicates whether or not idle
 * objects should be validated using the factory's
 * {@link PoolableObjectFactory#validateObject} method. Objects that fail to
 * validate will be dropped from the pool. This setting has no effect unless
 * <code>timeBetweenEvictionRunsMillis > 0.</code> The default setting for this
 * parameter is <code>false.</code></li>
 * <li>
 * {@link #setSoftMinEvictableIdleTimeMillis
 * <i>softMinEvictableIdleTimeMillis</i>} specifies the minimum amount of time
 * an object may sit idle in the pool before it is eligible for eviction by the
 * idle object evictor (if any), with the extra condition that at least
 * "minIdle" object instances remain in the pool. When non-positive, no objects
 * will be evicted from the pool due to idle time alone. This setting has no
 * effect unless <code>timeBetweenEvictionRunsMillis > 0.</code> and it is
 * superceded by {@link #setMinEvictableIdleTimeMillis
 * <i>minEvictableIdleTimeMillis</i>} (that is, if
 * <code>minEvictableIdleTimeMillis</code> is positive, then
 * <code>softMinEvictableIdleTimeMillis</code> is ignored). The default setting
 * for this parameter is -1 (disabled).</li>
 * <li>
 * {@link #setNumTestsPerEvictionRun <i>numTestsPerEvictionRun</i>} determines
 * the number of objects examined in each run of the idle object evictor. This
 * setting has no effect unless <code>timeBetweenEvictionRunsMillis > 0.</code>
 * The default setting for this parameter is 3.</li>
 * </ul>
 * <p>
 * <p>
 * The pool can be configured to behave as a LIFO queue with respect to idle
 * objects - always returning the most recently used object from the pool, or as
 * a FIFO queue, where borrowObject always returns the oldest object in the idle
 * object pool.
 * <ul>
 * <li>
 * {@link #setLifo <i>lifo</i>} determines whether or not the pool returns idle
 * objects in last-in-first-out order. The default setting for this parameter is
 * <code>true.</code></li>
 * </ul>
 * <p>
 * GenericObjectPool is not usable without a {@link PoolableObjectFactory}. A
 * non-<code>null</code> factory must be provided either as a constructor
 * argument or via a call to {@link #setFactory} before the pool is used.
 * <p>
 * 
 * @author Administrator
 * 
 */
public class TransportPoolConfig {

	private static final String RS_PROPERTIES = "/opt/etc/rs-client/rs.properties";

	private static TransportPoolConfig instance;

	private static Logger log = LoggerFactory
			.getLogger(TransportPoolConfig.class);

	public synchronized static TransportPoolConfig getInstance() {
		if (instance == null) {
			instance = new TransportPoolConfig();
			instance.initPool();
		}
		return instance;
	}
	
	public TransportPoolConfig(Properties props) {
	    initPool(props);
	}

	public synchronized static TransportPoolConfig getInstance(Properties props) {
		if (instance == null) {
			instance = new TransportPoolConfig();
			instance.initPool(props);
		}
		return instance;
	}

	private GenericObjectPool.Config config;
	public String host;
	// public String transportType = "BIO";
	public boolean isPing = true;
	public int maxIdle = 10;
	public int minIdle = 5;

	public int port;

	public int timeout = 30000;
	public int tryTimes = 4;

	private TransportPoolConfig() {
	}

	public GenericObjectPool.Config get() {
		return config;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public int getTimeout() {
		return timeout;
	}

	private void initPool() {
		Properties defaultConfig = new Properties();
		InputStream in = null;
		try {
			File file = new File(RS_PROPERTIES);
			if (file.exists()) {
				in = new FileInputStream(file);
			} else {
				in = TransportPoolConfig.class.getClassLoader()
						.getResourceAsStream("rs.properties");
			}
			log.info("Reading and loading config file:rs.properties");
			defaultConfig.load(in);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("loading rs.properties error.", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		log.info("initialize configuration from file ...");
		initPool(defaultConfig);
	}

	private void initPool(Properties props) {
		log.info(props.toString());

		host = props.getProperty("rs.thrift.host");
		port = toInt(props.getProperty("rs.thrift.port"));
		isPing = toBoolean(props.getProperty("rs.thrift.isPing"));

		config = new GenericObjectPool.Config();
		config.maxIdle = toInt(
				props.getProperty("thrift.transport.pool.maxIdle"), maxIdle);
		minIdle = toInt(props.getProperty("thrift.transport.pool.minIdle"),
				minIdle);
		timeout = toInt(props.getProperty("rs.thrift.timeout"), timeout);
		tryTimes = toInt(props.getProperty("rs.thrift.tryTimes"), tryTimes);
		// transportType =
		// defaultConfig.getProperty("rs.thrift.transport.type");

		config.maxActive = toInt(props
				.getProperty("thrift.transport.pool.maxActive"));
		config.maxWait = toInt(props
				.getProperty("thrift.transport.pool.maxWait"));

		config.whenExhaustedAction = new Integer(
				toInt(props
						.getProperty("thrift.transport.pool.whenExhaustedAction")))
				.byteValue();

		config.testOnBorrow = toBoolean(props
				.getProperty("thrift.transport.pool.testOnBorrow"));
		config.testOnReturn = toBoolean(props
				.getProperty("thrift.transport.pool.testOnReturn"));
		config.testWhileIdle = toBoolean(props
				.getProperty("thrift.transport.pool.testWhileIdle"));

		config.timeBetweenEvictionRunsMillis = toLong(props
				.getProperty("thrift.transport.pool.timeBetweenEvictionRunsMillis"));
		config.numTestsPerEvictionRun = toInt(props
				.getProperty("thrift.transport.pool.numTestsPerEvictionRun"));
		config.minEvictableIdleTimeMillis = toLong(props
				.getProperty("thrift.transport.pool.minEvictableIdleTimeMillis"));
		config.softMinEvictableIdleTimeMillis = toLong(props
				.getProperty("thrift.transport.pool.softMinEvictableIdleTimeMillis"));
		config.lifo = toBoolean(props.getProperty("thrift.transport.pool.lifo"));
	}

	public boolean isPing() {
		return isPing;
	}

	public int getTryTimes() {
		return tryTimes;
	}
}
