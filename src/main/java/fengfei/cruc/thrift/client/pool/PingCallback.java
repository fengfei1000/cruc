package fengfei.cruc.thrift.client.pool;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public interface PingCallback {
	boolean ping(TTransport transport) throws TTransportException;
}
