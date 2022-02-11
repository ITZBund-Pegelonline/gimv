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
 * Encapsulates the domain bounds of what is currently displayed.
 *
 * <p>Domain bounds might be coordinates, time ranges etc.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class SetDomainBoundsEvent extends FilteredDispatchGwtEvent<SetDomainBoundsEventHandler> {

	public static Type<SetDomainBoundsEventHandler> TYPE = new Type<SetDomainBoundsEventHandler>();

	private Bounds bounds;

	public SetDomainBoundsEvent(Bounds bounds, SetDomainBoundsEventHandler... blockedHandlers) {
		super(blockedHandlers);
		this.bounds = Validate.notNull(bounds, "bounds must not be null");
	}

	public Bounds getBounds() {
		return this.bounds;
	}

	@Override
	public Type<SetDomainBoundsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(SetDomainBoundsEventHandler handler) {
		handler.onSetDomainBounds(this);
	}
}
