package com.jmr.wrapper.common.complex;

/**
 * Networking Library
 * ReceivedComplexPiece.java
 * Purpose: A piece of a complex entity received from a stream. This piece will later be used to form back together the complex
 * entity and pass it to the event listener.
 * 
 * @author Jon R (Baseball435)
 * @version 1.0 7/25/2014
 */

public class ReceivedComplexPiece implements Comparable<ReceivedComplexPiece>{

	/** The checksum of the complex entity. */
	private final String checksum;
	
	/** The ID. */
	private final int id;
	
	/** The amount of pieces in the entity. */
	private final int pieceSize;
	
	/** The exact data size */
	private final int dataSize;
	
	/** The piece of data from the complex entity. */
	private final byte[] data;
	
	/** Creates a new piece to a complex entity with the given data.
	 * @param checksum The checksum of the entity.
	 * @param id The ID of the piece.
	 * @param pieceSize The amount of pieces in the entity.
	 * @param data The piece of data.
	 * @param dataSize The size of the data.
	 */
	public ReceivedComplexPiece(String checksum, int id, int pieceSize, byte[] data, int dataSize) {
		this.checksum = checksum;
		this.id = id;
		this.pieceSize = pieceSize;
		this.data = data;
		this.dataSize = dataSize;
	}
	
	/** @return The checksum of the complex entity. */
	public String getChecksum() {
		return checksum;
	}
	
	/** @return The ID. */
	public int getId() {
		return id;
	}
	
	/** @return The amount of pieces in the complex entity. */
	public int getPieceSize() {
		return pieceSize;
	}
	
	/** @return The size of the data without the ID's in the front. */
	public int getDataSize() {
		return dataSize;
	}	
	
	/** @return The piece of data of the entity. */
	public byte[] getData() {
		return data;
	}

	@Override
	public int compareTo(ReceivedComplexPiece piece) {
		return id - piece.getId();
	}
	
}
