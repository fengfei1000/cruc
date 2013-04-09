package fengfei.cruc.proto;

import java.io.IOException;

import fengfei.cruc.proto.CrucProto.CrucService.BlockingInterface;
import fengfei.cruc.proto.CrucProto.PingRequest;
import fengfei.cruc.proto.CrucProto.PingResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

public class ProtoClient {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static void main(String[] args) throws IOException, ServiceException {
		PeerInfo client = new PeerInfo("clientHostname", 1234);
		PeerInfo server = new PeerInfo("serverHostname", 8080);
		DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory(
				client);
		RpcServerCallExecutor executor = new ThreadPoolCallExecutor(3, 100);
		clientFactory.setRpcServerCallExecutor(executor);
		clientFactory.setConnectResponseTimeoutMillis(10000);
		clientFactory.setCompression(true);
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.handler(clientFactory);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
		bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
		RpcClientChannel channel = clientFactory.peerWith(server, bootstrap);
		BlockingInterface blockingInterface = CrucProto.CrucService
				.newBlockingStub(channel);
		RpcController controller = channel.newRpcController();

		PingRequest request = PingRequest.newBuilder().setCmd("hello").build();
		PingResponse pong = blockingInterface.ping(controller, request);
		clientFactory.getRpcServiceRegistry().registerService(
				new ProtoCrucServiceImpl());
		channel.close();
	}
}