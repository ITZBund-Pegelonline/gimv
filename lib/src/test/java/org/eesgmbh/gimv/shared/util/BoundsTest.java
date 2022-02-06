/*
 * Copyright 2010 EES GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eesgmbh.gimv.shared.util;

import static junit.framework.Assert.*;

import org.junit.Test;

public class BoundsTest {

	@Test
	public void testTransformProportionalWithOneTransformBoundsArgument() throws Exception {
		Bounds testBounds;
		Bounds actual;

		testBounds = new Bounds(10, 20, 10, 20);
		actual = testBounds.transformProportional(new Bounds(0.4, 0.6, 0.4, 0.6));

		assertEquals(14d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(16d, actual.getRight().doubleValue(), 0.001);
		assertEquals(14d, actual.getTop().doubleValue(), 0.001);
		assertEquals(16d, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(10, 20, 10, 20);
		actual = testBounds.transformProportional(new Bounds(0.0, 0.6, 0.4, 0.6));

		assertEquals(10d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(16d, actual.getRight().doubleValue(), 0.001);
		assertEquals(14d, actual.getTop().doubleValue(), 0.001);
		assertEquals(16d, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(10, 20, 10, 20);
		actual = testBounds.transformProportional(new Bounds(0.4, 0.6, 0.4, 1.1));

		assertEquals(14d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(16d, actual.getRight().doubleValue(), 0.001);
		assertEquals(14d, actual.getTop().doubleValue(), 0.001);
		assertEquals(21d, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(10, 20, 10, 20);
		actual = testBounds.transformProportional(new Bounds(-0.4, 0.6, 0.4, 0.6));

		assertEquals(6d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(16d, actual.getRight().doubleValue(), 0.001);
		assertEquals(14d, actual.getTop().doubleValue(), 0.001);
		assertEquals(16d, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(10, 20, 20, 10);
		actual = testBounds.transformProportional(new Bounds(0.4, 0.6, 0.4, 0.6));

		assertEquals(14d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(16d, actual.getRight().doubleValue(), 0.001);
		assertEquals(16d, actual.getTop().doubleValue(), 0.001);
		assertEquals(14d, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(10, 20, 20, 10);
		actual = testBounds.transformProportional(new Bounds(0.4, 0.6, -0.4, 0.6));

		assertEquals(14d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(16d, actual.getRight().doubleValue(), 0.001);
		assertEquals(24d, actual.getTop().doubleValue(), 0.001);
		assertEquals(14d, actual.getBottom().doubleValue(), 0.001);

		//zooming out
		testBounds = new Bounds(10, 20, 10, 30);
		actual = testBounds.transformProportional(new Bounds(-0.2, 1.2, -0.2, 1.2));

		assertEquals(8d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(22d, actual.getRight().doubleValue(), 0.001);
		assertEquals(6d, actual.getTop().doubleValue(), 0.001);
		assertEquals(34d, actual.getBottom().doubleValue(), 0.001);

		//zooming in
		testBounds = new Bounds(10, 20, 10, 30);
		actual = testBounds.transformProportional(new Bounds(0.2, 0.8, 0.2, 0.8));

		assertEquals(12d, actual.getLeft().doubleValue(), 0.001);
		assertEquals(18d, actual.getRight().doubleValue(), 0.001);
		assertEquals(14d, actual.getTop().doubleValue(), 0.001);
		assertEquals(26d, actual.getBottom().doubleValue(), 0.001);
	}

	@Test
	public void testTransformProportionalWithTwoTransformBoundsArguments() throws Exception {
		Bounds testBounds;
		Bounds actual;

		testBounds = new Bounds(100, 200, 100, 200);
		actual = testBounds.transform(new Bounds(0, 1000, 0, 1000), new Bounds(0, 10, 0, 10));

		assertEquals(1, actual.getLeft().doubleValue(), 0.001);
		assertEquals(2, actual.getRight().doubleValue(), 0.001);
		assertEquals(1, actual.getTop().doubleValue(), 0.001);
		assertEquals(2, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(10, 110, 0, 100);
		actual = testBounds.transform(new Bounds(0, 100, 0, 100), new Bounds(0, 1000, 0, 1000));

		assertEquals(100, actual.getLeft().doubleValue(), 0.001);
		assertEquals(1100, actual.getRight().doubleValue(), 0.001);
		assertEquals(0, actual.getTop().doubleValue(), 0.001);
		assertEquals(1000, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(110, 10, 0, 100);
		actual = testBounds.transform(new Bounds(100, 0, 0, 100), new Bounds(0, 1000, 0, 1000));

		assertEquals(-100, actual.getLeft().doubleValue(), 0.001);
		assertEquals(900, actual.getRight().doubleValue(), 0.001);
		assertEquals(0, actual.getTop().doubleValue(), 0.001);
		assertEquals(1000, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(20, 50, 0, 50);
		actual = testBounds.transform(new Bounds(0, 100, 0, 100), new Bounds(0, 1000, 0, 1000));

		assertEquals(200, actual.getLeft().doubleValue(), 0.001);
		assertEquals(500, actual.getRight().doubleValue(), 0.001);
		assertEquals(0, actual.getTop().doubleValue(), 0.001);
		assertEquals(500, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(0, 100, 50, 150);
		actual = testBounds.transform(new Bounds(0, 100, -50, 50), new Bounds(0, 1000, 0, 1000));

		assertEquals(0, actual.getLeft().doubleValue(), 0.001);
		assertEquals(1000, actual.getRight().doubleValue(), 0.001);
		assertEquals(1000, actual.getTop().doubleValue(), 0.001);
		assertEquals(2000, actual.getBottom().doubleValue(), 0.001);

		testBounds = new Bounds(0, 100, 0, 100);
		actual = testBounds.transform(new Bounds(10, 110, 0, 100), new Bounds(1000, 0, 0, 1000));

		assertEquals(1100, actual.getLeft().doubleValue(), 0.001);
		assertEquals(100, actual.getRight().doubleValue(), 0.001);
		assertEquals(0, actual.getTop().doubleValue(), 0.001);
		assertEquals(1000, actual.getBottom().doubleValue(), 0.001);
	}

	@Test
	public void testNormalizeBounds() throws Exception {
		Bounds testBounds;

		testBounds = new Bounds(2, 1, 2, 1);
		assertEquals(new Bounds(1, 2, 1, 2), testBounds.normalizeBounds());

		testBounds = new Bounds(2, 1, 1, 2);
		assertEquals(new Bounds(1, 2, 1, 2), testBounds.normalizeBounds());

		testBounds = new Bounds(1, 1, 1, 0);
		assertEquals(new Bounds(1, 1, 0, 1), testBounds.normalizeBounds());
	}
}
