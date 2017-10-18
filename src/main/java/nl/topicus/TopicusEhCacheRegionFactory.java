package nl.topicus;


import java.time.Duration;
import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.distribution.jgroups.JGroupsCacheReplicatorFactory;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.ehcache.EhCacheRegionFactory;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicusEhCacheRegionFactory extends EhCacheRegionFactory
{
	private static final long serialVersionUID = 1L;

	private static final String HIBERNATE = "hibernate-.";

	private static final Logger log = LoggerFactory.getLogger(TopicusEhCacheRegionFactory.class);

	private String jgroupsHosts;

	public TopicusEhCacheRegionFactory(Properties prop)
	{
		super(prop);

		this.jgroupsHosts = System.getProperty("jgroups/hosts");
			log.info("Replicating cache with hosts at " + jgroupsHosts);
	}

	@Override
	public void start(SessionFactoryOptions settings, Properties properties) throws CacheException
	{
		log.info(DefaultCacheKeysFactory.class.getClassLoader().toString());
		
		this.settings = settings;
		if (manager != null)
		{
			throw new IllegalStateException("CacheRegionFactory already initialized");
		}

		Configuration config = new Configuration();
		config.diskStore(new DiskStoreConfiguration().path("java.io.tmpdir"));
		config.defaultCache(
			setup(new CacheConfiguration().maxEntriesLocalHeap(9999), Duration.ofMinutes(5)));

		Duration oneHour = Duration.ofHours(1);

		addCache(config, "entity", 20000, oneHour);

		if (jgroupsHosts != null)
		{
			FactoryConfiguration<FactoryConfiguration< ? >> factory = new FactoryConfiguration<>();
			factory.className(PropertyJGroupsCacheManagerPeerProviderFactory.class.getName());
			config.cacheManagerPeerProviderFactory(factory);
		}
		manager = CacheManager.create(config);
		mbeanRegistrationHelper.registerMBean(manager, properties);
	}

	private void addCache(Configuration config, String cacheName, int maxEntriesLocalHeap,
			Duration duration)
	{
		config.addCache(
			setup(new CacheConfiguration(cacheName, maxEntriesLocalHeap), duration));
	}

	private CacheConfiguration setup(CacheConfiguration cache, Duration duration)
	{
		if (duration == null)
			cache.eternal(true);
		else
			cache.timeToIdleSeconds(duration.getSeconds())
				.timeToLiveSeconds(duration.getSeconds())
				.eternal(false);

		if (cache.getName() != null && !cache.getName().contains("QueryCache")
			&& jgroupsHosts != null)
		{
			CacheEventListenerFactoryConfiguration factoryConf =
				new CacheEventListenerFactoryConfiguration();
			final boolean replicate = cache.getName().contains("UpdateTimestampsCache");
			factoryConf.className(JGroupsCacheReplicatorFactory.class.getName())
				.properties("replicateAsynchronously=true, replicatePuts=" + replicate
					+ ", replicateUpdates=true, replicateUpdatesViaCopy=" + replicate
					+ ", replicateRemovals=true");
			cache.addCacheEventListenerFactory(factoryConf);
		}

		cache.persistence(new PersistenceConfiguration().strategy(Strategy.NONE));
		return cache.diskExpiryThreadIntervalSeconds(Duration.ofMinutes(2).getSeconds());
	}
}
