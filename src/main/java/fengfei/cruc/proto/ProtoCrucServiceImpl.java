package fengfei.cruc.proto;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import fengfei.cruc.proto.CrucProto.CallRequest;
import fengfei.cruc.proto.CrucProto.CallResponse;
import fengfei.cruc.proto.CrucProto.PingRequest;
import fengfei.cruc.proto.CrucProto.PingResponse;

public class ProtoCrucServiceImpl extends  CrucProto.CrucService implements
		CrucProto.CrucService.BlockingInterface,
		CrucProto.CrucService.Interface {

	@Override
	public PingResponse ping(RpcController controller, PingRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CallResponse call(RpcController controller, CallRequest request)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ping(RpcController controller, PingRequest request,
			RpcCallback<PingResponse> done) {
		// TODO Auto-generated method stub

	}

	@Override
	public void call(RpcController controller, CallRequest request,
			RpcCallback<CallResponse> done) {
		// TODO Auto-generated method stub
		
	}

}
