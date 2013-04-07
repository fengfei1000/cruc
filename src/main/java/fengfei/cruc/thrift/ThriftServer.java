package fengfei.cruc.thrift;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fengfei.cruc.utils.ResourcesUtils;

public class ThriftServer {

	private static Logger logger = LoggerFactory.getLogger(ThriftServer.class);
	private int port = 8002;

	private ThreadPoolExecutor threadPoolExecutor = null;

	private TServer tserver = null;

	//
	private String rsMain;

	private Properties getProperties() {
		Properties properties = null;
		try {
			if (rsMain != null && !"".equals(rsMain)) {
				File file = new File(rsMain);
				System.out.println("read properties from file: "
						+ file.getAbsolutePath());
				InputStream in = new FileInputStream(file);
				properties = new Properties();
				properties.load(in);
			} else {
				System.out.println("read propertiest from classpath.");
				properties = ResourcesUtils.getResourceAsProperties(this
						.getClass().getClassLoader(), "cruc.properties");
			}
		} catch (IOException e) {
			logger.error("load zookeeper property error", e);
			return null;
		}
		return properties;
	}

	public ThriftServer() {
	}

	public ThriftServer(String rsMain) {
		this.rsMain = rsMain;
	}

	public boolean init() {
		Properties properties = getProperties();

		return true;
	}

	public ThriftServer(int port) {
		super();
		this.port = port;
	}

	public boolean start() {
		try {
			logger.info("Initializing...");
			init();
			logger.info("Starting relation server...");
			startTHsHaServer();
			Runtime.getRuntime().addShutdownHook(new Thread("hook-thread") {

				@Override
				public void run() {
					shutdown();
				}
			});
		} catch (Exception e) {
			logger.error("Exception!", e);
		}
		return true;
	}

	private boolean startTHsHaServer() {
		try {
			TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(
					port);
			SyncHandler handler = new SyncHandler();
			CrucService.Processor<CrucService.Iface> processor = new CrucService.Processor<CrucService.Iface>(
					handler);

			threadPoolExecutor = new ThreadPoolExecutor(4,// corePoolSize
					32,// maximumPoolSize
					6000,// keepAliveTime
					TimeUnit.SECONDS,// TimeUnit
					new SynchronousQueue<Runnable>()// BlockingQueue<Runnable>
			// workQueue
			);
			THsHaServer.Args args = new THsHaServer.Args(serverTransport)
					.processorFactory(new TProcessorFactory(processor))
					.transportFactory(new TFramedTransport.Factory())
					.protocolFactory(new TBinaryProtocol.Factory())
					.inputProtocolFactory(new TBinaryProtocol.Factory())
					.executorService(threadPoolExecutor)
					.workerThreads(
							Runtime.getRuntime().availableProcessors() + 1);
			tserver = new THsHaServer(args);
			// Options options = new Options();
			// TServer server = new THsHaServer(new
			// TProcessorFactory(processor),
			// serverTransport, new TFramedTransport.Factory(),
			// new TBinaryProtocol.Factory(),
			// new TBinaryProtocol.Factory(), threadPoolExecutor, options);
			logger.info("Starting relation server on port " + port + " ...");
			tserver.serve();
		} catch (TTransportException e) {
			logger.error("TTransportException!", e);
			return false;
		} catch (Exception e) {
			logger.error("Exception!", e);
			return false;
		}
		return true;
	}

	public boolean restart() {
		logger.info("restart begin");
		shutdown();
		start();
		logger.info("restart ok");
		return true;
	}

	public boolean shutdown() {
		if (threadPoolExecutor != null) {
			threadPoolExecutor.shutdown();
			logger.info("threadPoolExecutor is shutdown");
		}
		if (tserver != null) {
			tserver.stop();
			logger.info("thrift server is shutdown");
		}
		logger.info("server is shutdown");
		return true;
	}
}
