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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.client.view.GenericWidgetViewImpl;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.ImageEntity;
import org.eesgmbh.gimv.shared.util.Validate;

import java.util.*;

/**
 * <p>A presenter to display hover, clickable (not yet) and static (not yet) tooltips.
 *
 * <p>The actual tooltip html is obtained from the {@link ImageEntity} instances in the {@link SetImageEntitiesEvent}.
 * The html is passed into {@link HTML} widgets, that are internally constructed in a pool.
 *
 * <p>The presenter can be configured in what way it will match a particular tooltip to the current mouse position,
 * see {@link #configureHoverMatch(boolean, boolean, boolean)}.
 *
 * <p>The offset of the tooltip can be set with {@link #setDisplayOffset(int, int)}.
 *
 * <p>If there shouldn't be any tooltips for a certain {@link ImageEntity} group id, these can be set with {@link #addExcludedImageEntityGroupId(String)}.
 *
 *  <p>Registers with the {@link HandlerManager} to receive the following events
 * <ul>
 * 	<li> {@link SetImageEntitiesEvent} (mandatory, won't do anything otherwise)
 * 	<li> {@link ViewportMouseMoveEvent} (mandatory, won't do anything otherwise)
 * 	<li> {@link SetDataAreaPixelBoundsEvent} (optional, will not show any tooltips, if the mouse cursor is outside these points)
 * 	<li> {@link ViewportMouseOutEvent} (technically optional, but the last tooltip might remain visible after the mouse cursor wandered outside the viewport)
 * 	<li> {@link ViewportDragInProgressEvent} (optional, will stop showing any tooltips, when there dragging takes places)
 * 	<li> {@link ViewportDragFinishedEvent} (optional, will show tooltips again, after dragging finished )
 * </ul>
 *
 * <p>Fires no events.
 *
 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
 *
 */
public class TooltipPresenter {

	/*
	 * implementation notes:
	 *
	 * - to achieve acceptable performance the list of #imageEntities is converted to a
	 *   two-dimensional array reflecting both x and y coordinates of ImageEntity.
	 */


	private MatchConfiguration hoverMatchConfiguration;

	private int xOffset;
	private int yOffset;

	private List<String> excludedImageEntityGroupId = new ArrayList<String>();

	/**
	 * all image entities within a list
	 */
	private List<ImageEntity> imageEntities;

	/**
	 * ImageEntities represented with a two-dimensional array. X and y is represented by the two array indices.
	 * If the horizontal or the vertical axis are configured to be irrelevant, one of the two dimensions will have
	 * as size of one.
	 *
	 * This allows very efficient access in terms of CPU load at the client machine as no iteration is required.
	 *
	 * A Java map would be compiled into a javascript array structure with iterative access, which is very inefficient.
	 *
	 */
	private Map<String, List<ImageEntity>[][]>  hoverImageEntities;

	private final TooltipViewFactory tooltipViewFactory;

	private boolean dragInProgress = false;

	private Bounds currentDataAreaBounds;

	/**
	 * Instantiates the presenter.
	 *
	 * @param handlerManager A {@link HandlerManager}
	 */
	public TooltipPresenter(HandlerManager handlerManager) {
		TooltipPresenterEventHandler eventHandler = new TooltipPresenterEventHandler();
		handlerManager.addHandler(ViewportMouseMoveEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetImageEntitiesEvent.TYPE, eventHandler);
		handlerManager.addHandler(SetDataAreaPixelBoundsEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportMouseOutEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportDragInProgressEvent.TYPE, eventHandler);
		handlerManager.addHandler(ViewportDragFinishedEvent.TYPE, eventHandler);

		configureHoverMatch(true, false, false);
		setDisplayOffset(1, 1);
		tooltipViewFactory = new TooltipViewFactory(10000);
	}

	/**
	 * <p>Configures the tooltip display mechanism to be used as a result of the current mouse position.
	 *
	 * <p>Setting both horizontal and vertical to true will require the mouse position to
	 * be excactly above the {@link ImageEntity} bounds.
	 *
	 * <p>If for instance only horizontal is set to true, only the x position must match with
	 * the {@link ImageEntity} bounds, meaning that the tooltip will be displayed even if the mouse cursor
	 * is somewhere below or above the {@link ImageEntity} bounds.
	 *
	 * <p>The opposite holds true for horizontal=false and vertical=true.
	 *
	 * <p>The default is horizontal=true, vertical=false, displayAll=false
	 *
	 * <p><b>performance note:</b> This current version of the presenter requires a lot of initialization time
	 * when both horizontal and vertical must match, tests in IE 7 (the slowest browsers) resulted in 10 sec
	 * of initialization for only 800 tooltips. So basically horizontal=true and vertical=true should
	 * not be used at the moment.
	 *
	 * @param horizontal mouse position must match horizontally with the {@link ImageEntity} bounds
	 * @param vertical mouse position must match vertically with the {@link ImageEntity} bounds
	 * @param displayAll whether to display all {@link ImageEntity} tooltips whose bound's center is exactly at the current mouse position
	 */
	public void configureHoverMatch(boolean horizontal, boolean vertical, boolean displayAll) {
		Validate.isTrue(horizontal || vertical, "At least one of horizontal or vertical must be true");

		this.hoverMatchConfiguration = new MatchConfiguration(horizontal, vertical, displayAll);
		this.hoverImageEntities = configureHoverImageEntities(this.hoverMatchConfiguration);
	}

	/**
	 * An offset in pixels for the tooltip position based on the center of the bounds of the {@link ImageEntity}s
	 *
	 * <p>The default is xOffset=1, yOffset=1
	 *
	 * @param xOffset offset in pixels
	 * @param yOffset offset in pixels
	 */
	public void setDisplayOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	/**
	 * Add an {@link ImageEntity} group id, for which no tooltips should be displayed.
	 *
	 * @param groupId an {@link ImageEntity} group id
	 */
	public void addExcludedImageEntityGroupId(String groupId) {
		this.excludedImageEntityGroupId.add(groupId);
	}

	/**
	 * Set the z-index for all tooltips.
	 *
	 * <p>The default z-index is 10000.
	 *
	 * <p>You only need to invoke this method, if
	 * the tooltips do not appear on top of other components
	 * with a z-index higher than 10000.
	 *
	 * @param zIndex A css z-index for all tooltips
	 */
	public void setTooltipZIndex(int zIndex) {
		this.tooltipViewFactory.setZIndex(zIndex);
	}

	/*
	 * methods who receive events
	 */

	private void onSetImageEntities(SetImageEntitiesEvent event) {
		imageEntities = event.getImageEntities();

		hoverImageEntities = configureHoverImageEntities(hoverMatchConfiguration);
	}

	private void onMouseOut(ViewportMouseOutEvent event) {
		tooltipViewFactory.hideAll();
	}

	private void onDragInProgress(ViewportDragInProgressEvent event) {
		dragInProgress = true;
		tooltipViewFactory.hideAll();
	}

	private void onDragFinished(ViewportDragFinishedEvent event) {
		dragInProgress = false;
		//the widget will become visible at the next mouse move
	}

	private void onSetDataAreaBounds(SetDataAreaPixelBoundsEvent event) {
		currentDataAreaBounds = event.getBounds();
	}

	/*
	 * private methods
	 */

	TooltipViewFactory getTooltipViewFactory() {
		return this.tooltipViewFactory;
	}

	/**
	 * Sets the {@link #hoverImageEntities}. Runs once when a new image was loaded.
	 *
	 * Computationally expensive, this runs in 250ms (FF), 45ms (Chrome) and 2500ms !! (IE) on an Intel Core 2Duo, 2,5Ghz for
	 * about 800 tooltips with only horizontal match beeing configured and displayAll=true
	 *
	 * Runs in 4500ms !! (FF), 600ms (Chrome) and 25000ms !!!!!!!! (IE) on an Intel Core 2Duo, 2,5Ghz for
	 * about 800 tooltips with both horizontal and vertical match beeing configured and displayAll=true
	 *
	 * The major performance bottleneck is {@link #computeDistance(ImageEntity, int, int, MatchConfiguration)}
	 * in particular when configuring horizontal=true and vertical=true
	 *
	 * @param matchConfig
	 * @return
	 */
	private Map<String, List<ImageEntity>[][]> configureHoverImageEntities(MatchConfiguration matchConfig) {
		if (this.imageEntities != null && matchConfig != null) {
			int vpWidth = findMaxRight(this.imageEntities);
			int vpHeight = findMaxBottom(this.imageEntities);

			Map<String, List<ImageEntity>[][]> ent = new HashMap<String, List<ImageEntity>[][]>();

			//transform the list of imageEntities to a multidimensial array
			for (ImageEntity e : imageEntities) {
				Bounds bounds = e.getBounds();

				for (int x = matchConfig.horizontal ? bounds.getLeft().intValue() : 0; x <= (matchConfig.horizontal ? bounds.getRight().intValue() : 0); x++) {
					for (int y = matchConfig.vertical ? bounds.getTop().intValue() : 0; y <= (matchConfig.vertical ? bounds.getBottom().intValue() : 0); y++) {
						if (x >= 0 && y >= 0 && x < vpWidth + 1 && y < vpHeight + 1) { //do not risk index out of bounds
							if (!this.excludedImageEntityGroupId.contains(e.getGroupId())) {
								//Add a new entry for group id
								if (!ent.containsKey(e.getGroupId())) {
									@SuppressWarnings("unchecked")
									List<ImageEntity>[][] array = new List[matchConfig.horizontal ? vpWidth + 1 : 1][matchConfig.vertical ? vpHeight + 1 : 1];

									ent.put(e.getGroupId(), array);
								}

								//Add new at array position
								if (ent.get(e.getGroupId())[x][y] == null) {
									ent.get(e.getGroupId())[x][y] = new ArrayList<ImageEntity>();
								}

								ent.get(e.getGroupId())[x][y].add(e);
							}
						}
					}
				}
			}

			//Preserve those, who are exactly at this pixel position
			//if there are none exactly there keep only the most relevant
			if (matchConfig.displayAll) {
				for (String key : ent.keySet()) {
					for (int x = 0; x < ent.get(key).length; x++) {
						for (int y = 0; y < ent.get(key)[x].length; y++) {
							if (ent.get(key)[x][y] != null) {
								boolean containsDistZero = false;
								for (ImageEntity e  : ent.get(key)[x][y]) {
									if (Math.round(computeDistance(e, x, y, matchConfig)) == 0) {
										containsDistZero = true;
									}
								}

								if (containsDistZero) {
									List<ImageEntity> newEntities = new ArrayList<ImageEntity>();

									for (ImageEntity e  : ent.get(key)[x][y]) {
										if (Math.round(computeDistance(e, x, y, matchConfig)) == 0) {
											newEntities.add(e);
										}
									}

									ent.get(key)[x][y] = newEntities;
								} else {
									ent.get(key)[x][y] = Collections.singletonList(findClosest(ent.get(key)[x][y], x, y, matchConfig));
								}
							}
						}
					}
				}


			//just on per pixel point, only keep the most relevant
			} else {
				for (String key : ent.keySet()) {
					for (int x = 0; x < ent.get(key).length; x++) {
						for (int y = 0; y < ent.get(key)[x].length; y++) {
							if (ent.get(key)[x][y] != null) {
								ent.get(key)[x][y] = Collections.singletonList(findClosest(ent.get(key)[x][y], x, y, matchConfig));
							}
						}
					}
				}
			}

			return ent;
		} else {
			return null;
		}
	}

	private void processMouseMoveEvent(ViewportMouseMoveEvent event) {
		if (hoverImageEntities != null) {
			if (!dragInProgress) { //do not interfere with dragging

				tooltipViewFactory.hideAll(); //is the right thing to do, if we do not find any ImageEntity

				if (insideInnerPixelBounds(event.getGwtEvent().getX(), event.getGwtEvent().getY())) { //do nothing if the mouse is not inside inner pixel bounds
					int indexX = hoverMatchConfiguration.horizontal ? event.getGwtEvent().getX() : 0;
					int indexY = hoverMatchConfiguration.vertical ? event.getGwtEvent().getY() : 0;

					tooltipViewFactory.startObtaining();

					for (String key : hoverImageEntities.keySet()) {
						//checking the bounds, the array might be smaller than the viewport size
						if (indexX >= 0 && indexX < hoverImageEntities.get(key).length) {
							if (indexY >= 0 && indexY < hoverImageEntities.get(key)[indexX].length) {

								List<ImageEntity> imageEntities = hoverImageEntities.get(key)[indexX][indexY];

								if (imageEntities != null && !imageEntities.isEmpty()) { //actually never empty, but you never how the impl might change

									int verticalOffset = 0;
									for (ImageEntity e : imageEntities) {
										GenericWidgetView view = tooltipViewFactory.getNext();

										//Only display the tooltip if it is within the inner pixel bounds
										if (insideInnerPixelBounds(e.getBounds().getHorizontalCenter(), e.getBounds().getVerticalCenter())) {
											view.setRelX(event.getGwtEvent().getClientX() - event.getGwtEvent().getX() + (int)Math.round(e.getBounds().getHorizontalCenter()) + xOffset);
											view.setRelY(event.getGwtEvent().getClientY() - event.getGwtEvent().getY() + (int)Math.round(e.getBounds().getVerticalCenter()) + verticalOffset + yOffset);
											view.setHtml(e.getHoverHtmlFragment());
											view.show();

											verticalOffset += view.getHeight() + 5;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private ImageEntity findClosest(List<ImageEntity> imageEntities, int x, int y, MatchConfiguration matchConfig) {
		Validate.isFalse(imageEntities.isEmpty());

		double dist = Double.MAX_VALUE;
		ImageEntity mostRelevant = null;

		for (ImageEntity e : imageEntities) {
			double newDist = Math.min(dist, computeDistance(e, x, y, matchConfig));

			if (newDist < dist) {
				mostRelevant = e;
				dist = newDist;
			}
		}

		return mostRelevant;
	}

	private double computeDistance(ImageEntity imageEntity, int x, int y, MatchConfiguration matchConfig) {
		if (matchConfig.horizontal && !matchConfig.vertical) {
			return Math.abs(imageEntity.getBounds().getHorizontalCenter() - x);

		} else if (!matchConfig.horizontal && matchConfig.vertical) {
			return Math.abs(imageEntity.getBounds().getVerticalCenter() - y);

		} else if (matchConfig.horizontal && matchConfig.vertical) {
			//pythagoras
			return Math.sqrt(
					Math.pow(imageEntity.getBounds().getHorizontalCenter() - x, 2) +
					Math.pow(imageEntity.getBounds().getVerticalCenter() - y, 2)
			);

		} else {
			throw new IllegalStateException();
		}
	}

	private int findMaxRight(List<ImageEntity> imageEntities) {
		double maxRight = Double.MIN_VALUE;

		for (ImageEntity e : imageEntities) {
			maxRight = Math.max(maxRight, e.getBounds().getRight());
		}

		return (int) Math.round(maxRight);
	}

	private int findMaxBottom(List<ImageEntity> imageEntities) {
		double maxBottom = Double.MIN_VALUE;

		for (ImageEntity e : imageEntities) {
			maxBottom = Math.max(maxBottom, e.getBounds().getBottom());
		}

		return (int) Math.round(maxBottom);
	}

	private boolean insideInnerPixelBounds(double x, double y) {
		return currentDataAreaBounds == null || currentDataAreaBounds.contains(x, y);
	}

	/*
	 * inner class for recieving events
	 */

	private class TooltipPresenterEventHandler implements ViewportMouseMoveEventHandler, SetImageEntitiesEventHandler, ViewportMouseOutEventHandler, ViewportDragInProgressEventHandler, ViewportDragFinishedEventHandler, SetDataAreaPixelBoundsEventHandler {

		public void onMouseMove(ViewportMouseMoveEvent event) {
			TooltipPresenter.this.processMouseMoveEvent(event);
		}

		public void onSetImageEntities(SetImageEntitiesEvent event) {
			TooltipPresenter.this.onSetImageEntities(event);
		}


		public void onMouseOut(ViewportMouseOutEvent event) {
			TooltipPresenter.this.onMouseOut(event);
		}

		public void onDragInProgress(ViewportDragInProgressEvent event) {
			TooltipPresenter.this.onDragInProgress(event);
		}

		public void onDragFinished(ViewportDragFinishedEvent event) {
			TooltipPresenter.this.onDragFinished(event);
		}

		public void onSetDataAreaPixelBounds(SetDataAreaPixelBoundsEvent event) {
			TooltipPresenter.this.onSetDataAreaBounds(event);
		}
	}

	/*
	 * inner helper classes
	 */

	private class MatchConfiguration {
		private boolean horizontal;
		private boolean vertical;
		private boolean displayAll;

		private MatchConfiguration(boolean horizontal, boolean vertical, boolean displayAll) {
			this.horizontal = horizontal;
			this.vertical = vertical;
			this.displayAll = displayAll;
		}
	}

	/**
	 * Contains a dynamically growing list of tooltip views.
	 *
	 * Purpose is to restrict the creation of new widgets (will contain only one widget
	 * when displayAll=false in match mode).
	 *
	 * @author Christian Seewald - EES GmbH - c.seewald@ees-gmbh.de
	 *
	 */
	class TooltipViewFactory {
		private final List<GenericWidgetView> viewsPool = new ArrayList<GenericWidgetView>();
		private int zIndex;

		private int currentIndex;

		private TooltipViewFactory(int zIndex) {
			this.zIndex = zIndex;
		}

		private void setZIndex(int zIndex) {
			this.zIndex = zIndex;

			//changing the z-Index for all views in the pool
			for (GenericWidgetView view : this.viewsPool) {
				view.setZIndex(zIndex);
			}
		}

		private void startObtaining() {
			currentIndex = 0;
		}

		private GenericWidgetView getNext() {
			if (viewsPool.size() <= currentIndex) {
				viewsPool.add(createView());
			}

			return viewsPool.get(currentIndex++);
		}

		private void hideAll() {
			for (GenericWidgetView view : this.viewsPool) {
				view.hide();
			}
		}

		private GenericWidgetView createView() {
			Widget widget = new HTML();
			DOM.setStyleAttribute(widget.getElement(), "position", "absolute");
			DOM.setStyleAttribute(widget.getElement(), "cursor", "default"); // don't let the textcursor appear
			RootPanel.get().add(widget); //won't show otherwise

			GenericWidgetView view = new GenericWidgetViewImpl(widget);
			view.hide();
			view.setZIndex(zIndex);

			return view;
		}

		List<GenericWidgetView> getViewsPool() {
			return this.viewsPool;
		}
	}
}
