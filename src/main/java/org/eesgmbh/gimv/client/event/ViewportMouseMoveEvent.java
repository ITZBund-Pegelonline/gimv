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

package org.eesgmbh.gimv.client.event;

import com.google.gwt.event.dom.client.MouseMoveEvent;


/**
 * Signals a mouse move over the viewport.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class ViewportMouseMoveEvent extends FilteredDispatchGwtEvent<ViewportMouseMoveEventHandler> {

	public static Type<ViewportMouseMoveEventHandler> TYPE = new Type<ViewportMouseMoveEventHandler>();

	private final MouseMoveEvent gwtEvent;

	public ViewportMouseMoveEvent(MouseMoveEvent gwtEvent, ViewportMouseMoveEventHandler... blockedHandlers) {
		super(blockedHandlers);
		this.gwtEvent = gwtEvent;
	}

	public MouseMoveEvent getGwtEvent() {
		return this.gwtEvent;
	}

	@Override
	public Type<ViewportMouseMoveEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(ViewportMouseMoveEventHandler handler) {
		handler.onMouseMove(this);
	}
}