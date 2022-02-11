/*
 * Copyright 2022 EES GmbH
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

package org.eesgmbh.gimv.client.history;

import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.shared.util.Bounds;

/**
 * <p>
 * Creates a representation of a {@link SetDomainBoundsEvent} as a history token and
 * can also create an event based on a history token.
 *
 * <p>
 * The two methods will produce or parse a history token value of the following
 * form: left-bound,top-bound,right-bound,bottom-bound as floating point values
 * separated with a '.'.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class DefaultSetDomainBoundsEventHistoryTokenTransformer {


	/**
	 * Creates an event from the history token value.
	 *
	 * @param boundsHistoryTokenValue
	 * @return A {@link SetDomainBoundsEvent}
	 * @throws UnparsableHistoryTokenException In case the history token cannot be parsed
	 */
	public static SetDomainBoundsEvent toEvent(String boundsHistoryTokenValue) throws UnparsableHistoryTokenException {
		String[] boundsElements = boundsHistoryTokenValue.split(",");

		if (boundsElements.length != 4) {
			throw new UnparsableHistoryTokenException("Could not parse history token value. The are no four bounds elements, or these elements are not separated with a comma character (,). Token value found: " + boundsHistoryTokenValue);
		}

		Bounds bounds;
		try {
			bounds = new Bounds(Double.parseDouble(
					boundsElements[0]),
					Double.parseDouble(boundsElements[2]),
					Double.parseDouble(boundsElements[1]),
					Double.parseDouble(boundsElements[3]));
		} catch (NumberFormatException e) {
			throw new UnparsableHistoryTokenException("Could not parse history token value. One of the four bounds elements is not a floating point number separated with a '.'. Token value found: " + boundsHistoryTokenValue, e);
		}

		return new SetDomainBoundsEvent(bounds);
	}

	/**
	 * Creates a history token value from an event.
	 *
	 * @param setDomainBoundsEvent A {@link SetDomainBoundsEvent}
	 * @return a history token value
	 */
	public static String toHistoryTokenValue(SetDomainBoundsEvent setDomainBoundsEvent) {
		Bounds bounds = setDomainBoundsEvent.getBounds();

		return
			bounds.getLeft() + "," +
			bounds.getTop() + "," +
			bounds.getRight() + "," +
			bounds.getBottom();
	}
}
