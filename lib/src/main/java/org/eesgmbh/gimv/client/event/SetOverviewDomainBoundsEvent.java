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
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * Sets the bounds of an overviews clipping section. These are the current domain bounds
 * of the main image.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class SetOverviewDomainBoundsEvent extends FilteredDispatchGwtEvent<SetOverviewDomainBoundsEventHandler> {

	public static Type<SetOverviewDomainBoundsEventHandler> TYPE = new Type<SetOverviewDomainBoundsEventHandler>();

	private Bounds bounds;

	public SetOverviewDomainBoundsEvent(Bounds bounds, SetOverviewDomainBoundsEventHandler... blockedHandlers) {
		super(blockedHandlers);
		this.bounds = Validate.notNull(bounds, "bounds must not be null");
	}

	public Bounds getBounds() {
		return this.bounds;
	}

	@Override
	public Type<SetOverviewDomainBoundsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetOverviewDomainBoundsEventHandler handler) {
		handler.onSetOverviewDomainBounds(this);
	}
}

