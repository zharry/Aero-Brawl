package network;

public interface ConnectionListener {

	void received(Connection con, Object object);

	void connected(Connection con);

	void disconnected(Connection con);
}
