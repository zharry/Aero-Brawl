// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network;

import network.packet.Packet;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

// A class for handling a remote connection
public class Connection {

	public Socket socket;
	public ConnectionListener listener;

	// Threads for IO
	public ReadThread readThread;
	public WriteThread writeThread;

	// Streams for IO
	public ObjectInputStream input;
	public ObjectOutputStream output;

	public boolean isConnected = true;

	// Queue for outgoing packets
	public ArrayBlockingQueue<Packet> packetQueue = new ArrayBlockingQueue<>(4096);

	public Connection(ConnectionListener listener, Socket socket) throws IOException {

		// Initialize connection

		this.listener = listener;
		this.socket = socket;

		writeThread = new WriteThread();
		readThread = new ReadThread();

		output = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		output.flush();
		input = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

		writeThread.start();
		readThread.start();

		listener.connected(this);
	}

	// Send the packet
	public synchronized void sendPacket(Packet packet) {
		// Queue it up
		packetQueue.offer(packet);
	}

	// When disconnected
	public synchronized void disconnect() {
		if(isConnected) {
			// Call listener's disconnected function
			listener.disconnected(this);
			isConnected = false;
			try {
				socket.close();
			} catch (IOException e) {
			}
			writeThread.interrupt();
		}
	}

	// Thread for reading packets
	public class ReadThread extends Thread {
		public ReadThread() {
			setName("ReadThread for " + socket.getInetAddress());
		}
		public void run() {
			try {
				while(isConnected) {
					// Receive packets
					listener.received(Connection.this, input.readObject());
				}
			} catch(Exception e) {
				e.printStackTrace();
				disconnect();
			}
		}
	}

	// Thread for writing packets
	public class WriteThread extends Thread {
		public WriteThread() {
			setName("WriteThread for " + socket.getInetAddress());
		}
		public void run() {
			try {
				while(isConnected) {
					try {
						// Write packets from the queue
						output.writeObject(packetQueue.take());
						output.flush();
					} catch(InterruptedException e) {
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
				disconnect();
			}
		}
	}
}
