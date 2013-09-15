package org.lightmare.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationMergeTest {

    private Map<Object, Object> config;

    private Map<Object, Object> defaults;

    private String subConfigKey = "subConfig";

    private String subSubConfigKey = "subSubConfig";

    private Configuration configuration = new Configuration();

    @Before
    public void start() {

	config = new HashMap<Object, Object>();

	Map<Object, Object> subConfig1 = new HashMap<Object, Object>();
	subConfig1.put(Config.DATA_SOURCE_PATH.key, "path1");
	subConfig1.put(Config.DEMPLOYMENT_PATH.key, "deployment1");
	Map<Object, Object> subSubConfig1 = new HashMap<Object, Object>();
	subSubConfig1.put(Config.PORT.key, "port_key1");
	subSubConfig1.put(Config.BOSS_POOL.key, "boss_pool_key1");
	subConfig1.put(subSubConfigKey, subSubConfig1);
	config.put(subConfigKey, subConfig1);
	config.put("falseOneConfig1", "falseOneConfigValue1");
	config.put("falseTwoConfig1", "falseTwoConfigValue1");

	defaults = new HashMap<Object, Object>();

	Map<Object, Object> subConfig2 = new HashMap<Object, Object>();
	subConfig2.put(Configuration.DATA_SOURCE_PATH_KEY, "path2");
	Map<Object, Object> subSubConfig2 = new HashMap<Object, Object>();
	subSubConfig2.put("port_false_key2", "port_key2");
	subConfig2.put(subSubConfigKey, subSubConfig2);
	defaults.put(subConfigKey, subConfig2);
	defaults.put("falseOneConfig1", "falseOneConfigValue1");
    }

    @Test
    public void deepMergeTest() {

	defaults = configuration.deepMerge(defaults, config);
	String dump = defaults.toString();

	System.out.println(dump);
    }
}
