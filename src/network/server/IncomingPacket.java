package network.server;

import com.jmr.wrapper.common.Connection;
import network.packet.Packet;

public class IncomingPacket {

	public Connection connection;
	public Packet packet;

	public IncomingPacket(Connection connection, Packet packet) {
		this.connection = connection;
		this.packet = packet;
	}
}
