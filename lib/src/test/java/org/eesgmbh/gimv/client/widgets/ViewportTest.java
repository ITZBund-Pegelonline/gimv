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

package org.eesgmbh.gimv.client.widgets;

import org.eesgmbh.gimv.client.testsupport.AbstractGimvGwtTest;
import org.eesgmbh.gimv.client.testsupport.MockMouseDownEvent;
import org.eesgmbh.gimv.client.testsupport.MockMouseMoveEvent;
import org.eesgmbh.gimv.client.testsupport.MockMouseOutEvent;
import org.eesgmbh.gimv.client.testsupport.MockMouseUpEvent;
import org.eesgmbh.gimv.client.testsupport.MockMouseWheelEvent;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.junit.Test;


public class ViewportTest extends AbstractGimvGwtTest {

	private Viewport viewport;

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();

		if (this.viewport == null) {
			this.viewport = new Viewport();
			this.viewport.setWidth("100px");
			this.viewport.setHeight("50px");
			this.viewport.setHandlerManager(testHM);
		}
	}

	@Test
	public void testDragWithPositiveOffsets() throws Exception {
		this.viewport.fireEvent(new MockMouseDownEvent(10, 20, 110, 120)); //starts the drag

		this.viewport.fireEvent(new MockMouseMoveEvent(11, 22, 111, 122)); //mouse move while dragging

		assertEquals(1, testEH.dragInProgressEvent.getHorizontalDragOffset());
		assertEquals(2, testEH.dragInProgressEvent.getVerticalDragOffset());

		this.viewport.fireEvent(new MockMouseUpEvent(13, 24)); //stops the drag

		assertEquals(new Bounds(0.1, 0.13, 0.4, 0.48), testEH.dragFinishedEvent.getProportionalBounds());
		assertEquals(new Bounds(10, 13, 20, 24), testEH.dragFinishedEvent.getRelativePixelBounds());
	}

	@Test
	public void testDragWithNegativeOffsets() throws Exception {
		this.viewport.fireEvent(new MockMouseDownEvent(10, 20, 110, 120)); //starts the drag

		this.viewport.fireEvent(new MockMouseMoveEvent(9, 18, 111, 122)); //mouse move while dragging

		assertEquals(-1, testEH.dragInProgressEvent.getHorizontalDragOffset());
		assertEquals(-2, testEH.dragInProgressEvent.getVerticalDragOffset());

		this.viewport.fireEvent(new MockMouseUpEvent(6, 14)); //stops the drag

		assertEquals(new Bounds(0.1, 0.06, 0.4, 0.28), testEH.dragFinishedEvent.getProportionalBounds());
		assertEquals(new Bounds(10, 6, 20, 14), testEH.dragFinishedEvent.getRelativePixelBounds());
	}

	@Test
	public void testMouseWheelEvent() throws Exception {
		MockMouseWheelEvent wheelEvent = new MockMouseWheelEvent(10, 10, 3);

		this.viewport.fireEvent(wheelEvent);

		assertSame(wheelEvent, testEH.mouseWheelEvent.getMouseWheelEvent());
	}

	@Test
	public void testMouseOutEvent() throws Exception {
		MockMouseOutEvent outEvent = new MockMouseOutEvent(10, 20);

		this.viewport.fireEvent(outEvent);

		assertSame(outEvent, testEH.mouseOutEvent.getGwtEvent());
	}

	@Test
	public void testMouseMoveNoDragging() throws Exception {
		MockMouseMoveEvent moveEvent = new MockMouseMoveEvent(10, 20, 110, 120);

		this.viewport.fireEvent(moveEvent);

		assertSame(moveEvent, testEH.mouseMoveEvent.getGwtEvent());
	}
}
