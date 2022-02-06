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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import org.eesgmbh.gimv.client.presenter.CalendarPresenter;
import org.eesgmbh.gimv.shared.util.Validate;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Subclasses can optionally specify {@link EventHandler} instances, that should not be invoked when an event
 * is dispatched, although these handlers are registered in the {@link HandlerManager} class for the particular event.
 *
 * <p>Usage: subclasss override {@link #onDispatch(EventHandler)} instead of {@link #dispatch(EventHandler)}, which is final in this class.
 * Usually a subclass constructor specifies only a single {@link EventHandler}. If no handler is specified no filter will be applied and the event will
 * be dispatched to all handlers. A handler will be identified by its <code>equals</code> method.
 *
 * <p>Purpose: Gimv applications usually have only a single {@link HandlerManager} / event bus. In certain modules e.g. in the presenter
 * {@link CalendarPresenter} it is necessary to fire the very same event the presenter is listening to. Without any filtering this will
 * eventually lead to the presenter invoking its own event handling method by firing the respective event. This might not break anything but it
 * is nevertheless a kind of behavior that ist hard to debug and error prone.
 *
 * <p>Limitation: this will only work if the event handlers which must not be called are known/accessible during event construction. On the
 * other hand the object firing the event in Gimv is usually also the one which must not be notified of the very same event.
 *
 * <p>The filtering mechanism can't be accomplished using the {@link GwtEvent#getSource()} as this field
 * can only be set by the {@link HandlerManager}. As there is only a single {@link HandlerManager} (an application event bus) with a single source,
 * event sources cannot differ in a {@link GwtEvent} in an application that uses only one event bus.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 * @param <H> handler type
 */
public abstract class FilteredDispatchGwtEvent<H extends EventHandler> extends GwtEvent<H> {

	private final List<H> blockedHandlers;

	/**
	 * Constructor.
	 *
	 * @param blockedHandlers
	 * 	Any number (0-n) of {@link EventHandler} instances that should no be invoked. An element must not null.
	 */
	protected FilteredDispatchGwtEvent(H... blockedHandlers) {
		this.blockedHandlers = Arrays.asList(Validate.notNullForEach(blockedHandlers));
	}

	@Override
	protected final void dispatch(H handler) {
		if (!isBlockedHandler(handler)) {
			onDispatch(handler);
		}
	};

	/**
	 * Invoked when a handler is contained in the blockedHandlers list
	 *
	 * @param handler
	 */
	protected abstract void onDispatch(H handler);

	/*
	 * subclasses might be interested
	 */

	protected boolean isBlockedHandler(H handler) {
		return getBlockedHandlers().contains(handler);
	}

	protected List<H> getBlockedHandlers() {
		return this.blockedHandlers;
	}

}
