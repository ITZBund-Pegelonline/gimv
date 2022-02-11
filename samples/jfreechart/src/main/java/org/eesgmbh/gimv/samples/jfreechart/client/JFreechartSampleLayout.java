package org.eesgmbh.gimv.samples.jfreechart.client;

import org.eesgmbh.gimv.client.widgets.Viewport;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class JFreechartSampleLayout {

	private VerticalPanel rootWidget;

	private Image shiftLeft;
	private DatePicker startDatePicker;
	private ToggleButton moveButton;
	private ToggleButton zoomButton;
	private Image mainChartImage;
	private Viewport mainViewport;
	private HTML zoomBox;
	private DatePicker endDatePicker;
	private Image shiftRight;

	private HTML verticalMousePointerLine;

	private Viewport horizontalOverviewViewport;
	private Image horizontalOverviewImage;
	private HorizontalPanel horizontalSlider;
	private Widget leftHandleWidget;
	private Widget mainHandleWidget;
	private Widget rightHandleWidget;
	private int minHorizontalSliderWidth;

	public JFreechartSampleLayout() {
		rootWidget = new VerticalPanel();
		rootWidget.setWidth("100%");
		rootWidget.setHeight("100%");
		rootWidget.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

		HorizontalPanel primaryImagePanel = new HorizontalPanel();
		primaryImagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		primaryImagePanel.setWidth("100%");
		primaryImagePanel.setHeight("100%");
		primaryImagePanel.setSpacing(6);

		shiftLeft = new Image("img/arrow-blue-rounded-left.png");
		DOM.setStyleAttribute(shiftLeft.getElement(), "cursor", "pointer");
		primaryImagePanel.add(shiftLeft);

		startDatePicker = new DatePicker();
		//wrap in simple panel with fixed size to workaround IE layout bug/feature
		SimplePanel spStartDatePicker = new SimplePanel();
		spStartDatePicker.add(startDatePicker);
		spStartDatePicker.setWidth("163px");
		primaryImagePanel.add(spStartDatePicker);
		primaryImagePanel.setCellHorizontalAlignment(spStartDatePicker, HasHorizontalAlignment.ALIGN_RIGHT);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.setHeight("100%");
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setSpacing(3);

		moveButton = new ToggleButton(new Image("img/move_on.png"), new Image("img/move_off.png"));
		moveButton.setSize("17px", "17px");
		DOM.setStyleAttribute(moveButton.getElement(), "padding", "0px");
		zoomButton = new ToggleButton(new Image("img/zoom_on.png"), new Image("img/zoom_off.png"));
		zoomButton.setSize("17px", "17px");
		DOM.setStyleAttribute(zoomButton.getElement(), "padding", "0px");

		buttonPanel.add(moveButton);
		buttonPanel.add(zoomButton);

		vp.add(buttonPanel);

		mainChartImage = new Image();

		mainViewport = new Viewport("100%", "100%");
		mainViewport.add(mainChartImage);
		mainViewport.setEnableZoomWhenShiftkeyPressed(true);

		//as it is focusable, we do not want to see an outline
		DOM.setStyleAttribute(mainViewport.getElement(), "outline", "none");

		zoomBox = new HTML();
		DOM.setStyleAttribute(zoomBox.getElement(), "backgroundColor", "blue");
		DOM.setStyleAttribute(zoomBox.getElement(), "opacity", "0.15");
		DOM.setStyleAttribute(zoomBox.getElement(), "filter", "alpha(opacity=15)"); //IE
		DOM.setStyleAttribute(zoomBox.getElement(), "outline", "black dashed 1px");
		DOM.setStyleAttribute(zoomBox.getElement(), "visibility", "hidden");
		mainViewport.add(zoomBox);

		verticalMousePointerLine = new HTML();
		DOM.setStyleAttribute(verticalMousePointerLine.getElement(), "backgroundColor", "blue");
		DOM.setStyleAttribute(verticalMousePointerLine.getElement(), "width", "1px");
		DOM.setStyleAttribute(verticalMousePointerLine.getElement(), "height", "100%");
		DOM.setStyleAttribute(verticalMousePointerLine.getElement(), "visibility", "hidden");
		mainViewport.add(verticalMousePointerLine);
		DOM.setStyleAttribute(verticalMousePointerLine.getElement(), "top", "0px");

		vp.add(mainViewport);
		vp.setCellWidth(mainViewport, "100%");
		vp.setCellHeight(mainViewport, "100%");

		primaryImagePanel.add(vp);
		primaryImagePanel.setCellWidth(vp, "100%");
		primaryImagePanel.setCellHeight(vp, "100%");

		endDatePicker = new DatePicker();
		//wrap in simple panel with fixed size to workaround IE layout bug/feature
		SimplePanel spEndDatePicker = new SimplePanel();
		spEndDatePicker.add(endDatePicker);
		spEndDatePicker.setWidth("163px");
		primaryImagePanel.add(spEndDatePicker);

		shiftRight = new Image("img/arrow-blue-rounded-right.png");
		DOM.setStyleAttribute(shiftRight.getElement(), "cursor", "pointer");
		primaryImagePanel.add(shiftRight);

		this.rootWidget.add(primaryImagePanel);
		this.rootWidget.setCellHeight(primaryImagePanel, "100%");

		horizontalOverviewImage = new Image();
		horizontalOverviewViewport = new Viewport("100%", "100px");
		horizontalOverviewViewport.add(horizontalOverviewImage);

		//as it is focusable, we do not want to see an outline
		DOM.setStyleAttribute(horizontalOverviewViewport.getElement(), "outline", "none");

		horizontalSlider = new HorizontalPanel();
		horizontalSlider.setHeight("100px");

		leftHandleWidget = buildHandleHTML("30px", "84px", "w-resize", "#6585d0", 0.5);
		rightHandleWidget = buildHandleHTML("30px", "84px", "e-resize", "#6585d0", 0.5);
		mainHandleWidget = buildHandleHTML("100%", "84px", "move", "#aaa", 0.3);

		horizontalSlider.add(leftHandleWidget);
		horizontalSlider.setCellWidth(leftHandleWidget, "30px");
		horizontalSlider.add(mainHandleWidget);
		horizontalSlider.setCellWidth(mainHandleWidget, "100%");
		horizontalSlider.add(rightHandleWidget);
		horizontalSlider.setCellWidth(rightHandleWidget, "30px");
		DOM.setStyleAttribute(horizontalSlider.getElement(), "visibility", "hidden");

		minHorizontalSliderWidth = 70;
		horizontalOverviewViewport.add(horizontalSlider);

		this.rootWidget.add(horizontalOverviewViewport);
	}

	private HTML buildHandleHTML(String width, String height, String cursor, String color, double transparancy) {
		HTML container = new HTML();
		container.setWidth(width);
		container.setHeight(height);
		DOM.setStyleAttribute(container.getElement(), "cursor", cursor);
		DOM.setStyleAttribute(container.getElement(), "backgroundColor", color);
		DOM.setStyleAttribute(container.getElement(), "opacity", Double.toString(transparancy));
		DOM.setStyleAttribute(container.getElement(), "mozOpacity", Double.toString(transparancy));
		DOM.setStyleAttribute(container.getElement(), "filter", "alpha(opacity = " + Double.toString(transparancy*100) + ")");
		return container;
	}

	public Widget getRootWidget() {
		return this.rootWidget;
	}

	public Image getShiftLeft() {
		return this.shiftLeft;
	}

	public DatePicker getStartDatePicker() {
		return this.startDatePicker;
	}

	public ToggleButton getMoveButton() {
		return this.moveButton;
	}

	public ToggleButton getZoomButton() {
		return this.zoomButton;
	}

	public Image getMainChartImage() {
		return this.mainChartImage;
	}

	public Viewport getMainViewport() {
		return this.mainViewport;
	}

	public HTML getZoomBox() {
		return this.zoomBox;
	}

	public DatePicker getEndDatePicker() {
		return this.endDatePicker;
	}

	public Image getShiftRight() {
		return this.shiftRight;
	}

	public HTML getVerticalMousePointerLine() {
		return this.verticalMousePointerLine;
	}

	public Image getHorizontalOverviewImage() {
		return this.horizontalOverviewImage;
	}

	public Widget getLeftHandleWidget() {
		return this.leftHandleWidget;
	}

	public Widget getRightHandleWidget() {
		return this.rightHandleWidget;
	}

	public Widget getMainHandleWidget() {
		return this.mainHandleWidget;
	}

	public Viewport getHorizontalOverviewViewport() {
		return this.horizontalOverviewViewport;
	}

	public HorizontalPanel getHorizontalSlider() {
		return this.horizontalSlider;
	}

	public int getMinHorizontalSliderWidth() {
		return this.minHorizontalSliderWidth;
	}
}
