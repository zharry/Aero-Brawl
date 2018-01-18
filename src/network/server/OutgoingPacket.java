// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.server;

import network.packet.Packet;

// A packet to be sent to a player
public class OutgoingPacket {
	public long id;
	public Packet packet;

	public OutgoingPacket(long id, Packet packet) {
		this.id = id;
		this.packet = packet;
	}
}
