package at.stefl.gatekeeper.shared.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class NetworkInterfaces {

	public static Set<InetAddress> getBroadcastAddresses() throws SocketException {
		Set<InetAddress> result = new HashSet<InetAddress>();

		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		while (enumeration.hasMoreElements()) {
			NetworkInterface interfaze = enumeration.nextElement();

			if (interfaze.isLoopback())
				continue;
			if (!interfaze.isUp())
				continue;

			for (InterfaceAddress address : interfaze.getInterfaceAddresses()) {
				InetAddress broadcast = address.getBroadcast();
				if (broadcast == null)
					continue;
				result.add(broadcast);
			}
		}

		return result;
	}

	private NetworkInterfaces() {
	}

}
