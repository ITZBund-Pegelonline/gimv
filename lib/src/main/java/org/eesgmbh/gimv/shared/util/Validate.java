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

import java.util.List;

/**
 * A few handy validation methods that support generics.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class Validate {

	public static void isTrue(boolean expression) {
		isTrue(expression, null);
	}

	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void isFalse(boolean expression) {
		isFalse(expression, null);
	}

	public static void isFalse(boolean expression, String message) {
		if (expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public static <T> T notNull(T obj) {
		return notNull(obj, null);
	}

	public static <T> T notNull(T obj, String message) {
		isTrue(obj != null, message);
		return obj;
	}

	public static <T> T[] notNullForEach(T[] objs) {
		return notNullForEach(objs, null);
	}

	public static <T> T[] notNullForEach(T[] objs, String message) {
		notNull(objs, message);

		for (T o : objs) {
			notNull(o, message);
		}

		return objs;
	}

	public static <T> List<T> notEmpty(List<T> list) {
		return notEmpty(list, null);
	}

	public static <T> List<T> notEmpty(List<T> list, String message) {
		notNull(list, message);
		isTrue(list.size() > 0, message);

		return list;
	}

	public static String notBlank(String string) {
		return notBlank(string, null);
	}
	public static String notBlank(String string, String message) {
		isTrue(string != null && string.trim().length() > 0, message);
		return string;
	}

	public static double isPositive(double number) {
		isTrue(number > 0);
		return number;
	}

	public static double isPositive(double number, String message) {
		isTrue(number > 0, message);
		return number;
	}

	public static double isPositiveOrZero(double number) {
		isTrue(number >= 0);
		return number;
	}

	public static double isPositiveOrZero(double number, String message) {
		isTrue(number >= 0, message);
		return number;
	}

	public static double isNegative(double number) {
		isTrue(number < 0);
		return number;
	}

	public static double isNegative(double number, String message) {
		isTrue(number < 0, message);
		return number;
	}

	public static double isNegativeOrZero(double number) {
		isTrue(number <= 0);
		return number;
	}

	public static double isNegativeOrZero(double number, String message) {
		isTrue(number <= 0, message);
		return number;
	}

	public static double isGreaterThan(double number, double greaterThan) {
		return isGreaterThan(number, greaterThan, null);
	}

	public static double isGreaterThan(double number, double greaterThan, String message) {
		isTrue(number > greaterThan, message);
		return number;
	}

	public static double isLowerThan(double number, double lowerThan) {
		return isLowerThan(number, lowerThan, null);
	}

	public static double isLowerThan(double number, double lowerThan, String message) {
		isTrue(number < lowerThan, message);
		return number;
	}
}
