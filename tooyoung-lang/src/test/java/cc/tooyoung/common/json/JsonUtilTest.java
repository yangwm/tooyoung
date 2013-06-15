package cc.tooyoung.common.json;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author yangwm May 21, 2013 9:37:47 PM
 */
public class JsonUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToJsonStr() {
		Assert.assertEquals("dal\\\"?[]}das\\\\aa\\n33\\r24\\tkkh\\r86gg\\f11\\b", JsonUtil.toJsonStr("dal\"?[]}das\\aa\n33\r24\tkkh\r86gg\f11\b"));
	}
	
    @Test
    public void testIsValidJsonObject() {
        Assert.assertEquals(true, JsonUtil.isValidJsonObject("{}"));
        Assert.assertEquals(false, JsonUtil.isValidJsonObject(""));
        Assert.assertEquals(false, JsonUtil.isValidJsonObject(null));
        Assert.assertEquals(true, JsonUtil.isValidJsonObject("{\"uid\":1750715731}"));
        Assert.assertEquals(true, JsonUtil.isValidJsonObject("{\"id\":10001}"));
        
        Assert.assertEquals(true, JsonUtil.isValidJsonObject("{}", true));
        Assert.assertEquals(true, JsonUtil.isValidJsonObject("", true));
        Assert.assertEquals(true, JsonUtil.isValidJsonObject(null, true));
    }
    
    @Test
    public void testIsValidJsonArray() {
        Assert.assertEquals(true, JsonUtil.isValidJsonArray("[]"));
        Assert.assertEquals(false, JsonUtil.isValidJsonArray(""));
        Assert.assertEquals(false, JsonUtil.isValidJsonArray(null));
        Assert.assertEquals(true, JsonUtil.isValidJsonArray("[{\"uid\":1750715731},{\"uid\":1821155363}]"));
        Assert.assertEquals(true, JsonUtil.isValidJsonArray("[1750715731, 1821155363]"));
        
        Assert.assertEquals(true, JsonUtil.isValidJsonArray("[]", true));
        Assert.assertEquals(true, JsonUtil.isValidJsonArray("", true));
        Assert.assertEquals(true, JsonUtil.isValidJsonArray(null, true));
    }
    
    @Test
    public void testToJson() {
        Assert.assertEquals("[1, 2, 3]", JsonUtil.toJson(new long[]{1, 2, 3}));
    }
	
}
