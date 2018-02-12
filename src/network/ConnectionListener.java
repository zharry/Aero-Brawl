// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network;

// A listener for events on connection
public interface ConnectionListener {

	// Packet received
	void received(Connection con, Object object);

	// Connected
	void connected(Connection con);

	// Disconnected
	void disconnected(Connection con);
}
