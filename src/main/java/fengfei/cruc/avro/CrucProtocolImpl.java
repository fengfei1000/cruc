package fengfei.cruc.avro;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.avro.AvroRemoteException;

public class CrucProtocolImpl implements CrucProtocol {

	@Override
	public CharSequence ping(CharSequence cmd) throws AvroRemoteException,
			CrucException {
		String rv = "";
		if (null == cmd || "".equals(cmd)
				|| "ping".equalsIgnoreCase(new StringBuilder(cmd).toString())) {
			rv = "Pong";
		} else {
			rv = cmd + " : OK";
		}
		return rv;
	}

	@Override
	public ByteBuffer call(CharSequence namegroup, CharSequence interfaceName,
			int version, List<CharSequence> params) throws AvroRemoteException,
			CrucException {
		// TODO Auto-generated method stub
		return null;
	}

}
