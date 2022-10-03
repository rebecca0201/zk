/* B96_ZK_2259Test.java

	Purpose:
		
	Description:
		
	History:
		12:39 PM 2022/9/30, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_2259Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertTrue(jq(".z-tab-selected").exists());
		assertEquals("Second Panel", jq(".z-tab-selected").text());
	}
}
