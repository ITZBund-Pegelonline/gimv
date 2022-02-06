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

package org.eesgmbh.gimv.client.controls;

import java.util.ArrayList;
import java.util.List;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEventHandler;
import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEvent;
import org.eesgmbh.gimv.client.event.ChangeImagePixelBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEventHandler;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvGwtTest;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bounds;

import com.google.gwt.user.client.Timer;

public class ViewportDimensionsListenerControlTest extends AbstractGimvGwtTest {

	private TestEventHandlerImpl testEventHandler;

    @Override
    protected void gwtSetUp() throws Exception {
    	super.gwtSetUp();

    	testEventHandler = new TestEventHandlerImpl();
		testHM.addHandler(LoadImageDataEvent.TYPE, testEventHandler);
		testHM.addHandler(ChangeImagePixelBoundsEvent.TYPE, testEventHandler);
		testHM.addHandler(SetViewportPixelBoundsEvent.TYPE, testEventHandler);
    }

	public void testConstructorInvalidArgumentWidth() {
		try {
			new ViewportDimensionsListenerControl(createViewport(0, 100), testHM);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	public void testConstructorInvalidArgumentHeight() {
		try {
			new ViewportDimensionsListenerControl(createViewport(100, 0), testHM);
			fail();
		} catch (IllegalArgumentException e) {}
	}

	/*
	 * Test with delayed assertions and setup. time periods must be setup
	 * to be quite long, so that the tests will always succeed even if the testrunners
	 * CPU is busy with other things
	 */

	public void testFiringLoadImageDataEvent() {
		delayTestFinish(20000);

		Viewport mockViewport = createViewport(100, 100);
		ViewportDimensionsListenerControl vct = new ViewportDimensionsListenerControl(mockViewport, testHM);
		vct.setListeningInterval(200);
		vct.setLoadImageDataEventFiringDelay(300);

		delayedReceivedLoadImageDataEventAssertion(0, 600); //nothing, as there was no change in viewport dimensions

		delayedSetViewportDimensions(120, -1, mockViewport, 800); //change width

		delayedReceivedLoadImageDataEventAssertion(0, 1000); //nothing yet
		delayedReceivedLoadImageDataEventAssertion(1, 1500); //received one event (200 + 300ms)

		delayedSetViewportDimensions(-1, 90, mockViewport, 1700); //change height
		delayedReceivedLoadImageDataEventAssertion(0, 1800); //nothing yet

		delayedSetViewportDimensions(-1, 80, mockViewport, 1900); //change height again, event accumulation starts again
		delayedReceivedLoadImageDataEventAssertion(0, 2100); //nothing yet
		delayedReceivedLoadImageDataEventAssertion(1, 2600);

		delayedFinishTest(2700);
	}

	public void testDeactivatedFiringOfLoadImageDataEvent() {
		delayTestFinish(20000);

		Viewport mockViewport = createViewport(100, 100);
		ViewportDimensionsListenerControl vct = new ViewportDimensionsListenerControl(mockViewport, testHM);
		vct.setListeningInterval(200);
		vct.setLoadImageDataEventFiringDelay(300);
		vct.setFireLoadImageDataEvent(false);

		delayedSetViewportDimensions(120, -1, mockViewport, 300); //change width

		delayedReceivedSetViewportBoundsEventAssertion(new SetViewportPixelBoundsEvent(new Bounds(0, 120, 0, 100)), 600); //always fired
		delayedReceivedLoadImageDataEventAssertion(0, 1000); //nothing received

		delayedFinishTest(1100);
	}

	public void testFiringSetImagePositionEvent() {
		delayTestFinish(20000);

		Viewport mockViewport = createViewport(100, 100);
		ViewportDimensionsListenerControl vct = new ViewportDimensionsListenerControl(mockViewport, testHM);
		vct.setListeningInterval(200);

		delayedReceivedSetImagePositionEventAssertion(null, 400); //nothing, as there was no change in viewport dimensions

		delayedSetViewportDimensions(101, -1, mockViewport, 600); //increase width
		delayedReceivedSetImagePositionEventAssertion(new ChangeImagePixelBoundsEvent(0, 0, 1, 0), 900); //after max of 200ms listening interval

		delayedSetViewportDimensions(-1, 95, mockViewport, 1000); //increase height
		delayedReceivedSetImagePositionEventAssertion(new ChangeImagePixelBoundsEvent(0, 0, 0, -5), 1300); //after max of 200ms listening interval

		delayedFinishTest(1400);
	}

	public void testDeactivatedFiringOfSetImagePositionEvent() {
		delayTestFinish(20000);

		Viewport mockViewport = createViewport(100, 100);
		ViewportDimensionsListenerControl vct = new ViewportDimensionsListenerControl(mockViewport, testHM);
		vct.setListeningInterval(200);
		vct.setLoadImageDataEventFiringDelay(300);
		vct.setFireSetImagePositionEvent(false);

		delayedSetViewportDimensions(120, -1, mockViewport, 300); //change width

		delayedReceivedSetViewportBoundsEventAssertion(new SetViewportPixelBoundsEvent(new Bounds(0, 120, 0, 100)), 600); //always fired
		delayedReceivedSetImagePositionEventAssertion(null, 900); //nothing received

		delayedFinishTest(1000);
	}

	public void testFiringSetViewportBoundsEvent() {
		delayTestFinish(20000);

		Viewport mockViewport = createViewport(100, 100);
		ViewportDimensionsListenerControl vct = new ViewportDimensionsListenerControl(mockViewport, testHM);
		vct.setListeningInterval(200);

		delayedReceivedSetViewportBoundsEventAssertion(null, 300); //nothing, as there was no change in viewport dimensions

		delayedSetViewportDimensions(101, -1, mockViewport, 400); //increase width
		delayedReceivedSetViewportBoundsEventAssertion(new SetViewportPixelBoundsEvent(new Bounds(0, 101, 0, 100)), 700); //after max of 200ms listening interval

		delayedSetViewportDimensions(-1, 95, mockViewport, 900); //increase height
		delayedReceivedSetViewportBoundsEventAssertion(new SetViewportPixelBoundsEvent(new Bounds(0, 101, 0, 95)), 1200); //after max of 200ms listening interval

		delayedFinishTest(1300);
	}

	private void delayedSetViewportDimensions(final int width, final int height, final Viewport viewport, int delay) {
		Timer timer = new Timer() {
			public void run() {
				if (width >= 0) {
					viewport.setWidth(width + "px");
				}
				if (height >= 0) {
					viewport.setHeight(height + "px");
				}
			}
		};

		timer.schedule(delay);
	}

	private void delayedReceivedLoadImageDataEventAssertion(final int num, final int delay) {
		Timer timer = new Timer() {
			public void run() {
				assertEquals("Failure after " + delay + " ms", num, testEventHandler.receivedLoadEvents.size());

				testEventHandler.receivedLoadEvents.clear();
			}
		};

		timer.schedule(delay);
	}

	private void delayedReceivedSetImagePositionEventAssertion(final ChangeImagePixelBoundsEvent expEvent, final int delay) {
		Timer timer = new Timer() {
			public void run() {
				if (expEvent != null) {
					assertEquals("Failure after " + delay + " ms", 1, testEventHandler.receivedPositionEvents.size());

					ChangeImagePixelBoundsEvent actualEvent = testEventHandler.receivedPositionEvents.get(0);

					assertEquals("Failure after " + delay + " ms", expEvent.getOffsetWidth(), actualEvent.getOffsetWidth());
					assertEquals("Failure after " + delay + " ms", expEvent.getOffsetHeight(), actualEvent.getOffsetHeight());

					assertEquals("Failure after " + delay + " ms", 0d, actualEvent.getOffsetX());
					assertEquals("Failure after " + delay + " ms", 0d, actualEvent.getOffsetY());

					testEventHandler.receivedPositionEvents.clear();
				} else {
					assertEquals("Failure after " + delay + " ms", 0, testEventHandler.receivedPositionEvents.size());
				}
			}
		};

		timer.schedule(delay);
	}

	private void delayedReceivedSetViewportBoundsEventAssertion(final SetViewportPixelBoundsEvent expEvent, final int delay) {
		Timer timer = new Timer() {
			public void run() {
				if (expEvent != null) {
					assertEquals("Failure after " + delay + " ms", 1, testEventHandler.receivedViewportBoundsEvents.size());

					SetViewportPixelBoundsEvent actualEvent = testEventHandler.receivedViewportBoundsEvents.get(0);

					assertEquals("Failure after " + delay + " ms", expEvent.getBounds(), actualEvent.getBounds());

					testEventHandler.receivedViewportBoundsEvents.clear();
				} else {
					assertEquals("Failure after " + delay + " ms", 0, testEventHandler.receivedViewportBoundsEvents.size());
				}
			}
		};

		timer.schedule(delay);
	}

	private void delayedFinishTest(int delay) {
		Timer timer = new Timer() {
			public void run() {
				finishTest();
			}
		};

		timer.schedule(delay);
	}

	private Viewport createViewport(int width, int height) {
		return new Viewport(width + "px", height + "px");
	}

	private class TestEventHandlerImpl implements LoadImageDataEventHandler, ChangeImagePixelBoundsEventHandler, SetViewportPixelBoundsEventHandler {
		private final List<LoadImageDataEvent> receivedLoadEvents = new ArrayList<LoadImageDataEvent>();
		private final List<ChangeImagePixelBoundsEvent> receivedPositionEvents = new ArrayList<ChangeImagePixelBoundsEvent>();
		private final List<SetViewportPixelBoundsEvent> receivedViewportBoundsEvents = new ArrayList<SetViewportPixelBoundsEvent>();

		public void onLoadImageData(LoadImageDataEvent event) {
			receivedLoadEvents.add(event);
		}

		public void onSetImageBounds(ChangeImagePixelBoundsEvent event) {
			receivedPositionEvents.add(event);
		}

		public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
			receivedViewportBoundsEvents.add(event);
		}
	}

}
