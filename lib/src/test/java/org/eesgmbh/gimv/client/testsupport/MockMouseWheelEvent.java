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

package org.eesgmbh.gimv.client.testsupport;

import com.google.gwt.event.dom.client.MouseWheelEvent;

public class MockMouseWheelEvent extends MouseWheelEvent {
	private final int x;
	private final int y;
	private final int deltaY;

	public MockMouseWheelEvent(int x, int y, int deltaY) {
		super();
		this.x = x;
		this.y = y;
		this.deltaY = deltaY;
	}

	@Override
	public int getDeltaY() {
		return deltaY;
	}

	@Override
	public boolean isNorth() {
		return deltaY > 0;
	}

	@Override
	public boolean isSouth() {
		return deltaY < 0;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
}