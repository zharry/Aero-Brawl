// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

import util.AABB;

import java.util.HashMap;

public class PacketNewWorld extends Packet {

	private static final long serialVersionUID = -9125057284532008560L;
	public String level;
	public byte[] obj;
	public byte[] mtl;
	public HashMap<String, AABB> aabbs;

	public PacketNewWorld(String level, byte[] obj, byte[] mtl, HashMap<String, AABB> aabbs) {
		this.level = level;
		this.obj = obj;
		this.mtl = mtl;
		this.aabbs = aabbs;
	}
}
