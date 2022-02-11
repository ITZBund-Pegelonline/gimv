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

package org.eesgmbh.gimv.client.util;

import java.util.ArrayList;
import java.util.List;

import org.eesgmbh.gimv.client.util.EventAccumulator.Callback;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;


public class EventAccumulatorTest extends GWTTestCase {

	private TestCallback testCallback;
	private EventAccumulator eventAccum;

	private TestGwtEvent gwtEvent1;
	private TestGwtEvent gwtEvent2;
	private TestGwtEvent gwtEvent3;
	private TestGwtEvent gwtEvent4;

	@Override
	public String getModuleName() {
		return "org.eesgmbh.gimv.Gimv";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();

		testCallback = new TestCallback();

		gwtEvent1 = new TestGwtEvent();
		gwtEvent2 = new TestGwtEvent();
		gwtEvent3 = new TestGwtEvent();
		gwtEvent4 = new TestGwtEvent();
	}

	public void testSingleEventAccumulation() {
		delayTestFinish(20000);

		eventAccum = new EventAccumulator(200, testCallback);

		delayAddEvent(gwtEvent1, 100);
		delayAddEvent(gwtEvent2, 150);

		delayedAssertion(100, false); //nothing yet
		delayedAssertion(200, false); //nothing yet
		delayedAssertion(450, true, gwtEvent1, gwtEvent2); //time elapsed, both are there
	}

	public void testMultipleEventAccumulation() {
		delayTestFinish(20000);

		eventAccum = new EventAccumulator(200, testCallback);

		delayAddEvent(gwtEvent1, 50);
		delayAddEvent(gwtEvent2, 150);

		delayedAssertion(50, false); //nothing yet
		delayedAssertion(150, false); //nothing yet
		delayedAssertion(450, false, gwtEvent1, gwtEvent2); //time elapsed, both are there

		delayClearEvent(550);
		delayAddEvent(gwtEvent3, 650); //the accumulation timer starts again here
		delayAddEvent(gwtEvent4, 750);

		delayedAssertion(700, false); //nothing yet
		delayedAssertion(800, false); //nothing yet
		delayedAssertion(1050, true, gwtEvent3, gwtEvent4); //time elapsed, both are there
	}

	public void testNoEventAccumulation() {
		eventAccum = new EventAccumulator(0, testCallback);

		assertReceivedEvents(0); //nothing

		eventAccum.addEvent(gwtEvent1);
		assertReceivedEvents(0, gwtEvent1); //immediatly delegated

		testCallback.gwtEvents.clear();

		eventAccum.addEvent(gwtEvent2);
		assertReceivedEvents(0, gwtEvent2); //immediatly delegated
	}

	private void assertReceivedEvents(int assertionDelay, TestGwtEvent... ecpectedEvents) {
		assertEquals("Failure after " + assertionDelay + " ms", ecpectedEvents.length, testCallback.gwtEvents.size());

		for (int i = 0; i < ecpectedEvents.length; i++) {
			assertSame("Failure after " + assertionDelay + " ms", ecpectedEvents[i], testCallback.gwtEvents.get(i));
		}
	}

	private Timer delayedAssertion(final int assertionDelay, final boolean finishTest, final TestGwtEvent... ecpectedEvents) {
		Timer assertionTimer = new Timer() {
			public void run() {
				assertReceivedEvents(assertionDelay, ecpectedEvents);

				if (finishTest) {
					finishTest();
				}
			}
		};

		assertionTimer.schedule(assertionDelay);

		return assertionTimer;
	}

	private Timer delayAddEvent(final GwtEvent<? extends EventHandler> event, int delay) {
		Timer timer = new Timer() {
			public void run() {
				eventAccum.addEvent(event);
			}
		};

		timer.schedule(delay);

		return timer;
	}

	private Timer delayClearEvent(int delay) {
		Timer timer = new Timer() {
			public void run() {
				testCallback.gwtEvents.clear();
			}
		};

		timer.schedule(delay);

		return timer;
	}

	private class TestCallback implements Callback {
		private List<GwtEvent<? extends EventHandler>> gwtEvents = new ArrayList<GwtEvent<? extends EventHandler>>();

		public void excute(List<GwtEvent<? extends EventHandler>> gwtEvents) {
			this.gwtEvents.addAll(gwtEvents);

		}
	}

	private class TestGwtEvent extends GwtEvent<EventHandler> {

		@Override
		protected void dispatch(EventHandler handler) {
		}

		@Override
		public Type<EventHandler> getAssociatedType() {
			return null;
		}
	}
}
