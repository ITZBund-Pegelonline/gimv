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

import com.google.gwt.event.dom.client.MouseDownEvent;

public class MockMouseDownEvent extends MouseDownEvent {

	private int x, y, clientX, clientY;

	public MockMouseDownEvent(int x, int y, int clientX, int clientY) {
		this.x = x;
		this.y = y;
		this.clientX = clientX;
		this.clientY = clientY;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getClientX() {
		return this.clientX;
	}

	@Override
	public int getClientY() {
		return this.clientY;
	}

	@Override
	public void preventDefault() {
		//do nothing
	}
}
