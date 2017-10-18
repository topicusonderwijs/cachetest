package nl.topicus;

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerProvider;
import net.sf.ehcache.distribution.jgroups.JGroupsCacheManagerPeerProvider;
import net.sf.ehcache.distribution.jgroups.JGroupsCacheManagerPeerProviderFactory;
import org.jgroups.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyJGroupsCacheManagerPeerProviderFactory
		extends JGroupsCacheManagerPeerProviderFactory
{
	private static final Logger log =
		LoggerFactory.getLogger(PropertyJGroupsCacheManagerPeerProviderFactory.class);

	@Override
	public CacheManagerPeerProvider createCachePeerProvider(CacheManager cacheManager,
			Properties properties)
	{
		LogFactory.setCustomLogFactory(new JGroupsCustomLoggerFactory());

		String initialHosts = System.getProperty("jgroups/hosts");
		String bindIp = System.getProperty("jgroups/bindip", "127.0.0.1");
		String bindPort = System.getProperty("jgroups/bindport", "7800");

		String connect = "TCP(bind_port=" + bindPort + ";bind_addr=" + bindIp
			+ ";recv_buf_size=20000000;send_buf_size=640000;send_queue_size=500000"
			+ ";sock_conn_timeout=300;conn_expire_time=30000;reaper_interval=60000):\n";

		connect += "TCPPING(initial_hosts=" + initialHosts + ";port_range=0):\n";

		connect += "MERGE3():\n" + "FD_SOCK(start_port=" + (Integer.parseInt(bindPort) + 100)
			+ ";bind_addr=" + bindIp + "):\n" + "FD(timeout=10000;max_tries=5):\n"
			+ "VERIFY_SUSPECT(timeout=1500):\n"
			+ "pbcast.NAKACK(use_mcast_xmit=false;retransmit_timeout=300,600,1200,2400,4800;"
			+ "discard_delivered_msgs=true):\n"
			+ "pbcast.STABLE(stability_delay=1000;desired_avg_gossip=50000;max_bytes=400000):\n"
			+ "pbcast.GMS(print_local_addr=true;join_timeout=3000;view_bundling=true):\n"
			+ "FRAG2(frag_size=60000):\n" + "pbcast.STATE()";

		log.info("Custom JGroups connection: " + connect);

		JGroupsCacheManagerPeerProvider provider =
			new JGroupsCacheManagerPeerProvider(cacheManager, connect);
		return provider;
	}
}
