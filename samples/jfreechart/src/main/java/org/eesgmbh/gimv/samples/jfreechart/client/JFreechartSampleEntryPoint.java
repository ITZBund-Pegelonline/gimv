package org.eesgmbh.gimv.samples.jfreechart.client;

import org.eesgmbh.gimv.client.controls.DragImageControl;
import org.eesgmbh.gimv.client.controls.KeystrokeControl;
import org.eesgmbh.gimv.client.controls.MouseWheelControl;
import org.eesgmbh.gimv.client.controls.ViewportDimensionsListenerControl;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.presenter.BoundsShiftPresenter;
import org.eesgmbh.gimv.client.presenter.CalendarPresenter;
import org.eesgmbh.gimv.client.presenter.ImageMoveOrZoomToggleButtonPresenter;
import org.eesgmbh.gimv.client.presenter.ImagePresenter;
import org.eesgmbh.gimv.client.presenter.MousePointerPresenter;
import org.eesgmbh.gimv.client.presenter.OverviewPresenter;
import org.eesgmbh.gimv.client.presenter.TooltipPresenter;
import org.eesgmbh.gimv.client.presenter.ZoomBoxPresenter;
import org.eesgmbh.gimv.client.view.BoundsShiftViewImpl;
import org.eesgmbh.gimv.client.view.CalendarViewImpl;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.client.view.GenericWidgetViewImpl;
import org.eesgmbh.gimv.client.view.ImageMoveOrZoomToggleButtonViewImpl;
import org.eesgmbh.gimv.client.view.ImageViewImpl;
import org.eesgmbh.gimv.samples.jfreechart.client.img.JFreechartSampleDataService;
import org.eesgmbh.gimv.samples.jfreechart.client.img.JFreechartSampleDataServiceAsync;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Direction;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class JFreechartSampleEntryPoint implements EntryPoint {
	public void onModuleLoad() {
		JFreechartSampleDataServiceAsync gimvService = GWT.create(JFreechartSampleDataService.class);

		HandlerManager primaryHandlerManager = new HandlerManager(null);
		HandlerManager horizontalOverviewHandlerManager = new HandlerManager(null);

		JFreechartSampleLayout layout = new JFreechartSampleLayout();
//		Example1Controller appController = new Example1Controller(primaryHandlerManager, horizontalOverviewHandlerManager, layout.getMainViewport(), layout.getHorizontalOverviewViewport(), gimvService);
		MainJFreechartSampleController mainController = new MainJFreechartSampleController(
				primaryHandlerManager, horizontalOverviewHandlerManager, gimvService);

		OverviewJFreechartSampleController overviewController = new OverviewJFreechartSampleController(horizontalOverviewHandlerManager, gimvService);

		//attach all widgets, must be done before configuring GimvComponents
		RootPanel.get("rootPanel").add(layout.getRootWidget());

		configureGimvComponentsInMainImageConsole(primaryHandlerManager, layout);
		primaryHandlerManager.fireEvent(StateChangeEvent.createMove());

		configureGimvComponentsInHorizontalOverview(horizontalOverviewHandlerManager, primaryHandlerManager, layout);
		horizontalOverviewHandlerManager.fireEvent(StateChangeEvent.createMove());

		mainController.init(layout.getMainViewport());
		overviewController.init(layout.getHorizontalOverviewViewport());
	}

	private void configureGimvComponentsInMainImageConsole(HandlerManager handlerManager, JFreechartSampleLayout layout) {
		BoundsShiftPresenter.View shiftLeftView = new BoundsShiftViewImpl(layout.getShiftLeft());
		BoundsShiftPresenter shiftLeftPresenter = new BoundsShiftPresenter(handlerManager, shiftLeftView);
		shiftLeftPresenter.configureAbsoluteShift(-1 * 1000*60*60*24, 0);

		CalendarPresenter.View startCalendarView = new CalendarViewImpl(layout.getStartDatePicker());
		CalendarPresenter startCalendarPresenter = new CalendarPresenter(handlerManager, startCalendarView);
		startCalendarPresenter.configureBound(Bound.LEFT);

		ImageMoveOrZoomToggleButtonPresenter.View moveOrZoomToggleView = new ImageMoveOrZoomToggleButtonViewImpl(layout.getMoveButton(), layout.getZoomButton());
		new ImageMoveOrZoomToggleButtonPresenter(handlerManager, moveOrZoomToggleView);

		ImagePresenter.View imageView = new ImageViewImpl(layout.getMainChartImage());
		new ImagePresenter(handlerManager, imageView);

		layout.getMainViewport().setHandlerManager(handlerManager);

		GenericWidgetView zoomBoxView = new GenericWidgetViewImpl(layout.getZoomBox());
		new ZoomBoxPresenter(handlerManager, zoomBoxView);

		new DragImageControl(handlerManager);

		KeystrokeControl keystrokeControl = new KeystrokeControl(handlerManager);

		keystrokeControl.addTargetElement(layout.getMainViewport().getElement());
		keystrokeControl.addTargetElement(layout.getHorizontalOverviewViewport().getElement());
		keystrokeControl.addTargetElement(layout.getMoveButton().getElement());
		keystrokeControl.addTargetElement(layout.getZoomButton().getElement());
		keystrokeControl.addDocumentAndBodyAsTarget();

		//offset of 10 pixels on the arrow keys, no modifiers
		keystrokeControl.registerKey(KeyCodes.KEY_LEFT, Direction.EAST, 10);
		keystrokeControl.registerKey(KeyCodes.KEY_UP, Direction.SOUTH, 10);
		keystrokeControl.registerKey(KeyCodes.KEY_RIGHT, Direction.WEST, 10);
		keystrokeControl.registerKey(KeyCodes.KEY_DOWN, Direction.NORTH, 10);

		//higher offsets when the ctrl modifier is down (for demonstration purpose)
		keystrokeControl.registerKey(KeyCodes.KEY_LEFT, true, false, false, false, Direction.EAST, 30);
		keystrokeControl.registerKey(KeyCodes.KEY_UP, true, false, false, false, Direction.SOUTH, 30);
		keystrokeControl.registerKey(KeyCodes.KEY_RIGHT, true, false, false, false, Direction.WEST, 30);
		keystrokeControl.registerKey(KeyCodes.KEY_DOWN, true, false, false, false, Direction.NORTH, 30);

		new ViewportDimensionsListenerControl(layout.getMainViewport(), handlerManager);

		new MouseWheelControl(handlerManager);

		TooltipPresenter tooltipPresenter = new TooltipPresenter(handlerManager);
		tooltipPresenter.configureHoverMatch(true, false, true);

		MousePointerPresenter mousePointerPresenter = new MousePointerPresenter(handlerManager, new GenericWidgetViewImpl(layout.getVerticalMousePointerLine()));
		mousePointerPresenter.configure(true, false);

		CalendarPresenter.View endCalendarView = new CalendarViewImpl(layout.getEndDatePicker());
		CalendarPresenter endCalendarPresenter = new CalendarPresenter(handlerManager, endCalendarView);
		endCalendarPresenter.configureBound(Bound.RIGHT);

		BoundsShiftPresenter.View shiftRightView = new BoundsShiftViewImpl(layout.getShiftRight());
		BoundsShiftPresenter shiftRightPresenter = new BoundsShiftPresenter(handlerManager, shiftRightView);
		shiftRightPresenter.configureAbsoluteShift(+1 * 1000*60*60*24, 0);
	}

	private void configureGimvComponentsInHorizontalOverview(HandlerManager handlerManager, HandlerManager mainHandlerManager, JFreechartSampleLayout layout) {
		// Preview image of the overview
		ImagePresenter.View imageView = new ImageViewImpl(layout.getHorizontalOverviewImage());
		new ImagePresenter(handlerManager, imageView);

		new ViewportDimensionsListenerControl(layout.getHorizontalOverviewViewport(), handlerManager);

		OverviewPresenter overviewPresenter = new OverviewPresenter(new GenericWidgetViewImpl(layout.getHorizontalSlider()), handlerManager, mainHandlerManager);

		// Define handles for controlling the overview
		overviewPresenter.addHandle(new GenericWidgetViewImpl(layout.getLeftHandleWidget()), Bound.LEFT);
		overviewPresenter.addHandle(new GenericWidgetViewImpl(layout.getMainHandleWidget()), Bound.LEFT, Bound.RIGHT);
		overviewPresenter.addHandle(new GenericWidgetViewImpl(layout.getRightHandleWidget()), Bound.RIGHT);

		// what is the minimum width for the slider
		overviewPresenter.setMinClippingWidth(layout.getMinHorizontalSliderWidth());

		// it's a horizontal slider, dont' let it move vertically
		overviewPresenter.setVerticallyLocked(true);

		layout.getHorizontalOverviewViewport().setHandlerManager(handlerManager);
	}
}
