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

package org.eesgmbh.gimv.client.controls;

import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.ViewportMouseWheelEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvGwtTest;
import org.eesgmbh.gimv.client.testsupport.MockMouseWheelEvent;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Test;

import com.google.gwt.user.client.Timer;


public class MouseWheelControlTest extends AbstractGimvGwtTest {

	private MouseWheelControl control;

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();

		if (this.control == null) {
			this.control = new MouseWheelControl(testHM);

			testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 100, 0, 100)));
			testHM.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, 1000, 0, 1000)));
		}
	}

	@Test
	public void testSetImagePositionEvent() throws Exception {
		testHM.fireEvent(new ViewportMouseWheelEvent(new MockMouseWheelEvent(250, 250, 3)));
		assertEquals(-50d, testEH.changeImagePixelBoundsEvent.getOffsetX(), 0.001);
		assertEquals(-50d, testEH.changeImagePixelBoundsEvent.getOffsetY(), 0.001);
		assertEquals(200d, testEH.changeImagePixelBoundsEvent.getOffsetWidth(), 0.001);
		assertEquals(200d, testEH.changeImagePixelBoundsEvent.getOffsetHeight(), 0.001);

		//FIXME: this is probably not correct
		testHM.fireEvent(new ViewportMouseWheelEvent(new MockMouseWheelEvent(250, 250, -3)));
		assertEquals(41.666d, testEH.changeImagePixelBoundsEvent.getOffsetX(), 0.001);
		assertEquals(41.666d, testEH.changeImagePixelBoundsEvent.getOffsetY(), 0.001);
		assertEquals(-166.666d, testEH.changeImagePixelBoundsEvent.getOffsetWidth(), 0.001);
		assertEquals(-166.666d, testEH.changeImagePixelBoundsEvent.getOffsetHeight(), 0.001);
	}

	@Test
	public void testSetDomainBoundsEvent() throws Exception {
		testHM.fireEvent(new ViewportMouseWheelEvent(new MockMouseWheelEvent(250, 250, 3)));
		assertEquals(new Bounds(4.166666666666664, 87.5, 4.166666666666664, 87.5), testEH.setDomainBoundsEvent.getBounds());

		//back to the original values (almost due to floating point imprecision)
		testHM.fireEvent(new ViewportMouseWheelEvent(new MockMouseWheelEvent(250, 250, -3)));
		assertEquals(new Bounds(-5.329070518200751E-15, 100.0, -5.329070518200751E-15, 100.0), testEH.setDomainBoundsEvent.getBounds());
	}

	@Test
	public void testLoadImageDataEvent() throws Exception {
		delayTestFinish(20000);

		testHM.fireEvent(new ViewportMouseWheelEvent(new MockMouseWheelEvent(250, 250, 3)));

		assertNull(testEH.loadImageDataEvent); //must be null, as the firing is deferred

		Timer timer = new Timer() {
			public void run() {
				assertNotNull(testEH.loadImageDataEvent);
				finishTest();
			}
		};

		timer.schedule(350); //default is 150ms deferr
	}
}
