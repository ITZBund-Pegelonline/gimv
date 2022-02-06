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

package org.eesgmbh.gimv.client.view;

import org.eesgmbh.gimv.shared.util.Bounds;

/**
 * A view interface common to some presenters.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public interface GenericWidgetView {

	int getAbsX();
	void setRelX(int x);
	int getAbsY();
	void setRelY(int y);
	int getWidth();
	void setWidth(int width);
	int getHeight();
	void setHeight(int height);
	Bounds getAbsBounds();
	void setHtml(String html);
	String getHtml();
	void setZIndex(int zIndex);
	void hide();
	void show();

}