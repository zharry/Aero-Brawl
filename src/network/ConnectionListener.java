// Jacky Liao and Harry Zhang
// Jan 18, 2017
// Summative
// ICS4U Ms.Strelkovska

package network;

public interface ConnectionListener {

	void received(Connection con, Object object);

	void connected(Connection con);

	void disconnected(Connection con);
}
