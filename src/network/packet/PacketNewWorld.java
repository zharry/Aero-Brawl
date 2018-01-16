// Jacky Liao and Harry Zhang
// Jan 12, 2017
// Summative
// ICS4U Ms.Strelkovska

package network.packet;

import java.util.ArrayList;

public class PacketNewWorld extends Packet {

	public String level;
	public byte[] obj;
	public byte[] mtl;
	public ArrayList<String> disabledBlocks;

	public PacketNewWorld(String level, byte[] obj, byte[] mtl, ArrayList<String> disabledBlocks) {
		this.level = level;
		this.obj = obj;
		this.mtl = mtl;
		this.disabledBlocks = disabledBlocks;
	}
}
