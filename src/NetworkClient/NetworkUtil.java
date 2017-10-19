package NetworkClient;

import com.jmr.wrapper.common.Connection;

public class NetworkUtil {

	public static void log(Connection con, String msg) {
		System.out.println("[" + con.getAddress() + "]: " + msg);
	}

}
