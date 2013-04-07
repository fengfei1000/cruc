package fengfei.cruc.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;

public interface ClientCallback<T, C extends TServiceClient> {
	T execute(C client) throws  TException;
}