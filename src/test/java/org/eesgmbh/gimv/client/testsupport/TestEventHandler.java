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

package org.eesgmbh.gimv.client.testsupport;

import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEventHandler;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEventHandler;
import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEventHandler;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetImageUrlEvent;
import org.eesgmbh.gimv.client.event.SetImageUrlEventHandler;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.event.StateChangeEventHandler;
import org.eesgmbh.gimv.client.event.ViewportMouseMoveEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseMoveEventHandler;
import org.eesgmbh.gimv.client.event.ViewportMouseOutEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseOutEventHandler;
import org.eesgmbh.gimv.client.event.ViewportMouseWheelEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseWheelEventHandler;

import com.google.gwt.event.shared.HandlerManager;

public class TestEventHandler implements ChangeImagePixelBoundsEventHandler, SetDomainBoundsEventHandler, LoadImageDataEventHandler, StateChangeEventHandler, SetImageUrlEventHandler, ViewportDragInProgressEventHandler, ViewportDragFinishedEventHandler, ViewportMouseWheelEventHandler, ViewportMouseOutEventHandler, ViewportMouseMoveEventHandler {

	public ChangeImagePixelBoundsEvent changeImagePixelBoundsEvent;
	public SetDomainBoundsEvent setDomainBoundsEvent;
	public LoadImageDataEvent loadImageDataEvent;
	public StateChangeEvent stateChangeEvent;
	public SetImageUrlEvent setImageUrlEvent;
	public ViewportMouseWheelEvent mouseWheelEvent;
	public ViewportMouseOutEvent mouseOutEvent;
	public ViewportMouseMoveEvent mouseMoveEvent;
	public ViewportDragInProgressEvent dragInProgressEvent;
	public ViewportDragFinishedEvent dragFinishedEvent;

	public TestEventHandler(HandlerManager hm) {
		hm.addHandler(ChangeImagePixelBoundsEvent.TYPE, this);
		hm.addHandler(SetDomainBoundsEvent.TYPE, this);
		hm.addHandler(LoadImageDataEvent.TYPE, this);
		hm.addHandler(StateChangeEvent.TYPE, this);
		hm.addHandler(ViewportMouseWheelEvent.TYPE, this);
		hm.addHandler(ViewportMouseOutEvent.TYPE, this);
		hm.addHandler(ViewportMouseMoveEvent.TYPE, this);
		hm.addHandler(ViewportDragInProgressEvent.TYPE, this);
		hm.addHandler(ViewportDragFinishedEvent.TYPE, this);
	}

	public void onSetImageBounds(ChangeImagePixelBoundsEvent event) {
		changeImagePixelBoundsEvent = event;
	}

	public void onSetDomainBounds(SetDomainBoundsEvent event) {
		setDomainBoundsEvent = event;
	}

	public void onLoadImageData(LoadImageDataEvent event) {
		loadImageDataEvent = event;
	}

	public void onStateChange(StateChangeEvent event) {
		stateChangeEvent = event;
	}

	public void onSetImageUrl(SetImageUrlEvent event) {
		setImageUrlEvent = event;
	}

	public void onMouseWheel(ViewportMouseWheelEvent event) {
		mouseWheelEvent = event;
	}

	public void onMouseOut(ViewportMouseOutEvent event) {
		mouseOutEvent = event;
	}

	public void onMouseMove(ViewportMouseMoveEvent event) {
		mouseMoveEvent = event;
	}

	public void onDragInProgress(ViewportDragInProgressEvent event) {
		dragInProgressEvent = event;
	}

	public void onDragFinished(ViewportDragFinishedEvent event) {
		dragFinishedEvent = event;
	}
}
