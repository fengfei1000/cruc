package fengfei.cruc.avro;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.SocketServer;
import org.apache.avro.ipc.SocketTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.util.Utf8;

import example.proto.Mail;
import example.proto.Message;
import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificRequestor;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.avro.util.Utf8;

import java.io.IOException;
import java.net.InetSocketAddress;
import fengfei.cruc.CrucServer;

public class AvroServer implements CrucServer {
	private Server server;

	@Override
	public boolean start() {
//		server = new NettyServer(new SpecificResponder(Mail.class,
//				new MailImpl()), new InetSocketAddress(65111));
	 
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
