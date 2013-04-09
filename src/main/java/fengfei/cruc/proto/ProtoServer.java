package fengfei.cruc.proto;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

import fengfei.cruc.CrucServer;

public class ProtoServer implements CrucServer {

	@Override
	public boolean start() {
		PeerInfo serverInfo = new PeerInfo("serverHostname", 8080);
		RpcServerCallExecutor executor = new ThreadPoolCallExecutor(3, 200);

		DuplexTcpServerPipelineFactory serverFactory = new DuplexTcpServerPipelineFactory(
				serverInfo);
		serverFactory.setRpcServerCallExecutor(executor);
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(
				new NioEventLoopGroup(0, new RenamingThreadFactoryProxy("boss",
						Executors.defaultThreadFactory())),
				new NioEventLoopGroup(0, new RenamingThreadFactoryProxy(
						"worker", Executors.defaultThreadFactory())));
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(serverFactory);
		bootstrap.localAddress(serverInfo.getPort());
		bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
		bootstrap.childOption(ChannelOption.SO_RCVBUF, 1048576);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, 1048576);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);

		serverFactory.getRpcServiceRegistry().registerService(
				new ProtoCrucServiceImpl());    
		
 
		bootstrap.bind();
		return false;
	}

	@Override
	public boolean restart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shutdown() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
