package com.jmr.wrapper.common.complex;

import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import com.jmr.wrapper.common.IProtocol;
import com.jmr.wrapper.common.threads.ComplexUdpSendThread;

/**
 * Networking Library ComplexObject.java Purpose: An entity that takes another
 * entity's byte array and splits it into pieces. It then sends these pieces to
 * the server and the server receives them and recreates the entity. This class
 * adds the checksum to the beginning of each piece as the identifier for which
 * entity it corresponds to.
 * 
 * @author Jon R (Baseball435)
 * @version 1.0 7/25/2014
 */

public class ComplexObject {

	/** Increments the complex entity's id's. */
	private static int ID_INCREMENT = 0;

	/** The amount of splits to make. */
	private int splitAmount;

	/** The entity's id. */
	@SuppressWarnings("unused")
	private final int id;

	/** The data and checksum byte arrays. */
	private final byte[] data, checksum;

	/** Instance of the protocol being used. */
	private final IProtocol protocol;

	/** Array to hold all of the pieces. */
	private final ArrayList<ComplexPiece> pieces = new ArrayList<ComplexPiece>();

	/**
	 * Creates a new complex entity and loads the pieces by splitting the data.
	 * 
	 * @param data
	 *            The entity's byte array.
	 * @param checksum
	 *            The entity's checksum value.
	 * @param protocol
	 *            Instance of the protocol.
	 */
	public ComplexObject(byte[] data, byte[] checksum, IProtocol protocol) {
		this(data, checksum, protocol, 3);
	}

	/**
	 * Creates a new complex entity and loads the pieces by splitting the data.
	 * 
	 * @param data
	 *            The entity's byte array.
	 * @param checksum
	 *            The entity's checksum value.
	 * @param protocol
	 *            Instance of the protocol.
	 * @param splitAmount
	 *            The amount of splits to make.
	 */
	public ComplexObject(byte[] data, byte[] checksum, IProtocol protocol, int splitAmount) {
		this.id = ID_INCREMENT++;
		this.data = data;
		this.protocol = protocol;
		this.splitAmount = splitAmount;
		this.checksum = checksum;
		loadPieces();

	}

	/**
	 * Splits the entity's byte array into pieces and gets them ready to be sent
	 * to over the stream.
	 */
	private void loadPieces() {
		int bytesPerSend = data.length / splitAmount;
		if (bytesPerSend <= 10) { // Bytes per send needs to be > 10 so that the
									// checksum can be extracted correctly. If
									// not, change the split amount.
			splitAmount = data.length / 11;
			bytesPerSend = 11;
		}
		int extra = 0;
		int pieceAmount = splitAmount;

		if (bytesPerSend * splitAmount < data.length) {
			extra = data.length - (bytesPerSend * splitAmount);
			pieceAmount++;
		}
		for (int i = 0; i < splitAmount; i++) {
			byte[] splitData = copyArray(data, bytesPerSend, bytesPerSend * i);
			pieces.add(new ComplexPiece(i, pieceAmount, splitData, protocol, checksum));
		}
		if (extra > 0) {
			byte[] splitData = copyArray(data, extra, bytesPerSend * (splitAmount));
			pieces.add(new ComplexPiece(splitAmount, pieceAmount, splitData, protocol, checksum));
		}
	}

	/**
	 * Sends the entity over TCP.
	 * 
	 * @param tcpOut
	 *            The TCP output stream.
	 */
	public void sendTcp(ObjectOutputStream tcpOut) {
		for (ComplexPiece piece : pieces)
			piece.sendTcp(tcpOut);
	}

	/**
	 * Sends the entity over UDP.
	 * 
	 * @param udpOut
	 *            The UDP output stream.
	 * @param InetAddress
	 *            The address to send it to.
	 * @param port
	 *            The port to send it over.
	 */
	public void sendUdp(DatagramSocket udpOut, InetAddress address, int port) {
		for (ComplexPiece piece : pieces)
			protocol.executeThread(new ComplexUdpSendThread(piece, udpOut, address, port));
	}

	/**
	 * Copies part of one array to another.
	 * 
	 * @param src
	 *            The array to copy.
	 * @param sizeToCopy
	 *            The amount of bytes being copied.
	 * @param startIndex
	 *            The starting position in the source.
	 * @return The new byte array.
	 */
	private byte[] copyArray(byte[] src, int sizeToCopy, int startIndex) {
		byte[] ret = new byte[sizeToCopy];
		for (int i = 0; i < sizeToCopy; i++) {
			ret[i] = src[i + startIndex];
		}
		return ret;
	}

}
