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

import java.util.Arrays;

import org.junit.Test;

public class ValidateTest {

	@Test
	public void testIsTrue() {
		Validate.isTrue(true);
		Validate.isTrue(true, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsTrueThrowsIllegalArgumentException() {
		Validate.isTrue(false);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsTrueWithMessageThrowsIllegalArgumentException() {
		Validate.isTrue(false, "message");
	}

	@Test
	public void testIsFalse() {
		Validate.isFalse(false);
		Validate.isFalse(false, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsFalseThrowsIllegalArgumentException() {
		Validate.isFalse(true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsFalseWithMessageThrowsIllegalArgumentException() {
		Validate.isFalse(true, "message");
	}

	@Test
	public void testNotNull() {
		Validate.notNull("");
		Validate.notNull("", "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotNullThrowsIllegalArgumentException() {
		Validate.notNull(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotNullWithMessageThrowsIllegalArgumentException() {
		Validate.notNull(null, "message");
	}

	@Test
	public void testNotNullForEach() {
		Validate.notNullForEach(new String[] {"", ""});
		Validate.notNullForEach(new String[] {"", ""}, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotNullForEachThrowsIllegalArgumentException() {
		Validate.notNullForEach(new String[] {"", null});
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotNullForEachWithMessageThrowsIllegalArgumentException() {
		Validate.notNullForEach(null, "message");
	}

	@Test
	public void testNotEmpty() {
		Validate.notEmpty(Arrays.asList(""));
		Validate.notEmpty(Arrays.asList(""), "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotEmptyThrowsIllegalArgumentException() {
		Validate.notEmpty(Arrays.asList());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotEmptyWithMessageThrowsIllegalArgumentException() {
		Validate.notEmpty(null, "message");
	}

	@Test
	public void testNotBlank() {
		Validate.notBlank("notblank");
		Validate.notBlank("notblank", "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotBlankThrowsIllegalArgumentException() {
		Validate.notBlank("");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testNotBlankWithMessageThrowsIllegalArgumentException() {
		Validate.notBlank("", "message");
	}

	@Test
	public void testIsPositive() {
		Validate.isPositive(1);
		Validate.isPositive(0.1, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsPositiveThrowsIllegalArgumentException() {
		Validate.isPositive(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsPositiveWithMessageThrowsIllegalArgumentException() {
		Validate.isPositive(0, "message");
	}

	@Test
	public void testIsPositiveOrZero() {
		Validate.isPositiveOrZero(0);
		Validate.isPositiveOrZero(1, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsPositiveOrZeroThrowsIllegalArgumentException() {
		Validate.isPositiveOrZero(-1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsPositiveOrZeroWithMessageThrowsIllegalArgumentException() {
		Validate.isPositiveOrZero(-0.2, "message");
	}

	@Test
	public void testIsNegative() {
		Validate.isNegative(-1);
		Validate.isNegative(-0.1, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsNegativeThrowsIllegalArgumentException() {
		Validate.isNegative(0);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsNegativeWithMessageThrowsIllegalArgumentException() {
		Validate.isNegative(1, "message");
	}

	@Test
	public void testIsNegativeOrZero() {
		Validate.isNegativeOrZero(0);
		Validate.isNegativeOrZero(-1, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsNegativeOrZeroThrowsIllegalArgumentException() {
		Validate.isNegativeOrZero(1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsNegativeOrZeroWithMessageThrowsIllegalArgumentException() {
		Validate.isNegativeOrZero(12221, "message");
	}

	@Test
	public void testIsGreaterThan() {
		Validate.isGreaterThan(12, 11);
		Validate.isGreaterThan(0.12, 0.11, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsGreaterThanThrowsIllegalArgumentException() {
		Validate.isGreaterThan(11, 12);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsGreaterThanWithMessageThrowsIllegalArgumentException() {
		Validate.isGreaterThan(11, 11, "message");
	}

	@Test
	public void testIsLowerThan() {
		Validate.isLowerThan(1, 2);
		Validate.isLowerThan(-2, -1, "message");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsLowerThanThrowsIllegalArgumentException() {
		Validate.isLowerThan(2, 1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsLowerThanWithMessageThrowsIllegalArgumentException() {
		Validate.isLowerThan(-2, -2, "message");
	}
}
