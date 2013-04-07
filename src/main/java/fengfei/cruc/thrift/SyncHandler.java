package fengfei.cruc.thrift;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.thrift.TException;

import fengfei.cruc.thrift.CrucException;
import fengfei.cruc.thrift.CrucService.Iface;

public class SyncHandler implements Iface {

	AtomicLong ct = new AtomicLong();

	@Override
	public String ping(String cmd) throws CrucException, TException {
		String rv = "";
		if (null == cmd || "".equals(cmd) || "ping".equalsIgnoreCase(cmd)) {
			rv = "Pong";
		} else {
			rv = cmd + " : OK";
		}
		return rv;
	}

	@Override
	public ByteBuffer call(String namegroup, String interfaceName, int version,
			List<String> params) throws CrucException, TException {
		// TODO Auto-generated method stub
		return null;
	}

}
