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

import static org.junit.Assert.*;

import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.event.ViewportDragFinishedEvent;
import org.eesgmbh.gimv.client.event.ViewportDragInProgressEvent;
import org.eesgmbh.gimv.client.testsupport.AbstractGimvUnitTest;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Before;
import org.junit.Test;


public class DragImageControlTest extends AbstractGimvUnitTest {

	private DragImageControl control;

	@Before
	public void setUp() {
		if (this.control == null) {
			this.control = new DragImageControl(testHM);
			testHM.fireEvent(StateChangeEvent.createMove());
		}

		testHM.fireEvent(new SetDomainBoundsEvent(new Bounds(0, 100, 0, 100)));
	}

	@Test
	public void testSetImagePosition() throws Exception {
		testHM.fireEvent(createDragInProgressEvent(5, 8));
		assertEquals(5d, testEH.changeImagePixelBoundsEvent.getOffsetX(), 0.001);
		assertEquals(8d, testEH.changeImagePixelBoundsEvent.getOffsetY(), 0.001);

		testHM.fireEvent(createDragInProgressEvent(-5, -8));
		assertEquals(-5d, testEH.changeImagePixelBoundsEvent.getOffsetX(), 0.001);
		assertEquals(-8d, testEH.changeImagePixelBoundsEvent.getOffsetY(), 0.001);

		assertNull(testEH.loadImageDataEvent);
	}

	@Test
	public void testSetBoundsEvent() throws Exception {
		testHM.fireEvent(createDragFinishedEvent(-0.2, -0.2));
		assertEquals(new Bounds(20, 120, 20, 120), testEH.setDomainBoundsEvent.getBounds());

		//based on the new bounds
		testHM.fireEvent(createDragFinishedEvent(0.1, 0.2));
		assertEquals(new Bounds(10, 110, 0, 100), testEH.setDomainBoundsEvent.getBounds());

		assertNull(testEH.changeImagePixelBoundsEvent);
	}

	@Test
	public void testLoadImageDataEvent() throws Exception {
		assertNull(testEH.loadImageDataEvent);

		testHM.fireEvent(createDragFinishedEvent(0.2, 0.2));
		assertNotNull(testEH.loadImageDataEvent);
	}

	private ViewportDragFinishedEvent createDragFinishedEvent(double proportionalHorizontalDragOffset, double proportionalVerticalDragOffset) {
		return new ViewportDragFinishedEvent(new Bounds(0d, proportionalHorizontalDragOffset, 0d, proportionalVerticalDragOffset), new Bounds());
	}

	private ViewportDragInProgressEvent createDragInProgressEvent(int horizontalDragOffset, int verticalDragOffset) {
		return new ViewportDragInProgressEvent(horizontalDragOffset, verticalDragOffset, new Bounds(10, 15, 10, 15), new Bounds(20, 20, 20, 20));
	}
}
