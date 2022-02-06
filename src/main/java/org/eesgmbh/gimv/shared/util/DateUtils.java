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

import java.util.Date;

/**
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class DateUtils {

	/**
	 * Sets the time on the same day to 00:00:00.000
	 *
	 * @param date
	 * @return a new date
	 */
	@SuppressWarnings("deprecation")
	public static Date truncateTime(Date date) {
		Date newDate = (Date) date.clone();

		newDate.setHours(0);
		newDate.setMinutes(0);
		newDate.setSeconds(0);
		newDate.setTime(newDate.getTime() - newDate.getTime() % 1000); //Millisekunden auf 0

		return newDate;
	}

	/**
	 * Sets the time on the same day to 23:59:59.999
	 *
	 * @param date
	 * @return a new date
	 */
	@SuppressWarnings("deprecation")
	public static Date ceilTime(Date date) {
		Date newDate = (Date) date.clone();

		newDate.setHours(23);
		newDate.setMinutes(59);
		newDate.setSeconds(59);
		newDate.setTime(newDate.getTime() + 999 - newDate.getTime() % 1000); //Millisekunden auf 999

		return newDate;
	}
}
