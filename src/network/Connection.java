// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network;

import network.packet.Packet;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Connection {

	public Socket socket;
	public ConnectionListener listener;

	public ReadThread readThread;
	public WriteThread writeThread;

	public ObjectInputStream input;
	public ObjectOutputStream output;

	public boolean isConnected = true;

	public ArrayBlockingQueue<Packet> packetQueue = new ArrayBlockingQueue<>(4096);

	public Connection(ConnectionListener listener, Socket socket) throws IOException {
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

	public synchronized void sendPacket(Packet packet) {
		packetQueue.offer(packet);
	}

	public synchronized void disconnect() {
		if(isConnected) {
			listener.disconnected(this);
			isConnected = false;
			try {
				socket.close();
			} catch (IOException e) {
			}
			writeThread.interrupt();
		}
	}

	public class ReadThread extends Thread {
		public ReadThread() {
			setName("ReadThread for " + socket.getInetAddress());
		}
		public void run() {
			try {
				while(isConnected) {
					listener.received(Connection.this, input.readObject());
				}
			} catch(Exception e) {
				e.printStackTrace();
				disconnect();
			}
		}
	}

	public class WriteThread extends Thread {
		public WriteThread() {
			setName("WriteThread for " + socket.getInetAddress());
		}
		public void run() {
			try {
				while(isConnected) {
					try {
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
