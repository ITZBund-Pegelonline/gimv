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

package org.eesgmbh.gimv.client.presenter;

import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerManager;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.view.ImageViewImpl;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Validate;

/**
 * The presenter wraps the actual image view contained within
 * the {@link Viewport}.<br>
 * It is responsible for setting the url within the view, changing
 * position and dimensions of the image and resetting its position
 * after a load event.
 *
 * <p>An implementation of {@link View} is {@link ImageViewImpl}.
 *
 * <p>The view can be given an {@link ErrorHandler}, which will be invoked if
 * for some reason the image could not be loaded under the given URL.
 *
 * <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link SetImageUrlEvent} (mandatory, won't work otherwise)
 * 	<li> {@link ChangeImagePixelBoundsEvent} (might be received e.g. while dragging or mouse wheels)
 * 	<li> {@link SetViewportPixelBoundsEvent} (used to reset the dimensions of a newly rendered image e.g. after width and height
 * 		 changes caused by mouse wheels)
 * </ul>
 *
 * <p>Fires no events.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 */
public class ImagePresenter {

	/**
	 * The view interface of {@link ImagePresenter}.
	 *
	 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
	 *
	 */
	public interface View {
		void setUrl(String url);
		void setPosition(int x, int y);
		void changePosition(int offsetX, int offsetY);
		void setDimensions(int width, int height);
		void changeDimensions(int offsetWidth, int offsetHeight);
		void addLoadHandler(LoadHandler loadHandler);

		/**
		 * Adds an error handler to the view, which will be
		 * invoked, if the image could not be obtained from the
		 * server under the given URL.
		 *
		 * @param errorHandler An implementation of {@link ErrorHandler}
		 */
		void addErrorHandler(ErrorHandler errorHandler);
	}

	private final View view;

	private Bounds currentViewportBounds;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 * @param view A {@link View} implementation.
	 */
	public ImagePresenter(HandlerManager handlerManager, View view) {
		Validate.notNull(handlerManager);
		this.view = Validate.notNull(view);

		ImagePresenterEventHandler eventHandler = new ImagePresenterEventHandler();

		handlerManager.addHandler(SetImageUrlEvent.TYPE, eventHandler);
		handlerManager.addHandler(ChangeImagePixelBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetViewportPixelBoundsEvent.TYPE, eventHandler);

		view.addLoadHandler(eventHandler);
	}

	private void onSetImageUrl(SetImageUrlEvent event) {
		view.setUrl(event.getUrl());
	}

	private void onSetImagePosition(ChangeImagePixelBoundsEvent event) {
		view.changePosition((int) event.getOffsetX(), (int)event.getOffsetY());
		view.changeDimensions((int) event.getOffsetWidth(), (int) event.getOffsetHeight());
	}

	private void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
		currentViewportBounds = event.getBounds();
	}

	/**
	 * The image will always be repositioned to its origin and
	 * width and height set to the viewport size after it was loaded.
	 *
	 * This is because both position and dimensions can change before the
	 * load due to dragging or mouse wheel for instance.
	 */
	private void onImageLoad(LoadEvent event) {
		view.setPosition(0, 0);

		//might not be set during init, x=0, y=0 should suffice in this case
		if (currentViewportBounds != null) {
			view.setDimensions(currentViewportBounds.getAbsWidth().intValue(), currentViewportBounds.getAbsHeight().intValue());
		}
	}

	private class ImagePresenterEventHandler implements SetImageUrlEventHandler, ChangeImagePixelBoundsEventHandler, SetViewportPixelBoundsEventHandler, LoadHandler {
		public void onSetImageUrl(SetImageUrlEvent event) {
			ImagePresenter.this.onSetImageUrl(event);
		}

		public void onSetImageBounds(ChangeImagePixelBoundsEvent event) {
			ImagePresenter.this.onSetImagePosition(event);
		}

		public void onSetViewportBounds(SetViewportPixelBoundsEvent event) {
			ImagePresenter.this.onSetViewportBounds(event);
		}

		public void onLoad(LoadEvent event) {
			ImagePresenter.this.onImageLoad(event);
		}
	}
}
