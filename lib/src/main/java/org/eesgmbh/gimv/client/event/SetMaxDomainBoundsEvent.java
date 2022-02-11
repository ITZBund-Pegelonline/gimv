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

package org.eesgmbh.gimv.client.event;

import org.eesgmbh.gimv.shared.util.Bounds;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Used in a Gimv application to specify the maximum bounds within which an
 * image can be rendered.
 *
 * <p>For example consider a time series chart, which can only offer data
 * in certain date ranges.
 *
 * <p>Any number of {@link Bounds} can be specified, to define several areas. Normally just one is needed.
 *
 * <p>Constraining the bounds might not be neccessary in a particular application or
 * it might only be necessary to constrain the bounds vertically or horizontally. {@link Bounds} objects,
 * which will be passed into the constructor can be specified as such.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class SetMaxDomainBoundsEvent extends FilteredDispatchGwtEvent<SetMaxDomainBoundsEventHandler> {

	public static Type<SetMaxDomainBoundsEventHandler> TYPE = new Type<SetMaxDomainBoundsEventHandler>();

	private final List<Bounds> boundsList = new ArrayList<Bounds>();

	/**
	 * Constructor with a single {@link Bounds} argument.
	 *
	 * @param bounds if null, no constraining bounds will be specified
	 * @param blockedHandlers
	 */
	public SetMaxDomainBoundsEvent(Bounds bounds, SetMaxDomainBoundsEventHandler... blockedHandlers) {
		super(blockedHandlers);

		addBounds(bounds);
	}

	public void addBounds(Bounds bounds) {
		if (bounds != null) {
			boundsList.add(bounds);
		}
	}

	public boolean containsHorizontally(double... values) {
		for (Bounds bounds : this.boundsList) {
			if (!bounds.containsHorizontally(values)) {
				return false;
			}
		}

		return true;
	}

	public boolean containsVertically(double... values) {
		for (Bounds bounds : this.boundsList) {
			if (!bounds.containsVertically(values)) {
				return false;
			}
		}

		return true;
	}

	public List<Bounds> getBounds() {
		return this.boundsList;
	}

	@Override
	public Type<SetMaxDomainBoundsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetMaxDomainBoundsEventHandler handler) {
		handler.onSetMaxDomainBounds(this);
	}
}
