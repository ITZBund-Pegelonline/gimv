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


/**
 * An event that signals global state changes that might affect a number
 * of components in a gimv application.
 *
 * <p>States:
 * <ul>
 * 	<li> Zoom: the user selected zooming into the image to be used as the effect of mouse dragging
 * 	<li> Move: the user selected move the image to be used as the effect of mouse dragging
 * </ul>
 * </p>
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class StateChangeEvent extends FilteredDispatchGwtEvent<StateChangeEventHandler> {

	public static Type<StateChangeEventHandler> TYPE = new Type<StateChangeEventHandler>();

	private enum State {ZOOM, MOVE};

	private final State state;

	private StateChangeEvent(State state, StateChangeEventHandler... blockedHandlers) {
		super(blockedHandlers);

		this.state = state;
	}

	public static StateChangeEvent createZoom(StateChangeEventHandler... blockedHandlers) {
		return new StateChangeEvent(State.ZOOM, blockedHandlers);
	}

	public static StateChangeEvent createMove(StateChangeEventHandler... blockedHandlers) {
		return new StateChangeEvent(State.MOVE, blockedHandlers);
	}

	public boolean isZoom() {
		return this.state == State.ZOOM;
	}

	public boolean isMove() {
		return this.state == State.MOVE;
	}

	@Override
	public Type<StateChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void onDispatch(StateChangeEventHandler handler) {
		handler.onStateChange(this);
	}
}
