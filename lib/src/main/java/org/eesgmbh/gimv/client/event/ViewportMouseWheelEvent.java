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

import com.google.gwt.event.dom.client.MouseWheelEvent;


/**
 * Signals a mouse wheel over the viewport.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class ViewportMouseWheelEvent extends FilteredDispatchGwtEvent<ViewportMouseWheelEventHandler> {

	public static Type<ViewportMouseWheelEventHandler> TYPE = new Type<ViewportMouseWheelEventHandler>();

	private final MouseWheelEvent mouseWheelEvent;

	public ViewportMouseWheelEvent(MouseWheelEvent mouseWheelEvent, ViewportMouseWheelEventHandler... blockedHandlers) {
		super(blockedHandlers);
		this.mouseWheelEvent = mouseWheelEvent;
	}

	public MouseWheelEvent getMouseWheelEvent() {
		return this.mouseWheelEvent;
	}

	@Override
	public Type<ViewportMouseWheelEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(ViewportMouseWheelEventHandler handler) {
		handler.onMouseWheel(this);
	}
}