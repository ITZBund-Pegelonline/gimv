package org.eesgmbh.gimv.samples.jfreechart.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.eesgmbh.gimv.client.event.*;
import org.eesgmbh.gimv.client.history.DefaultSetDomainBoundsEventHistoryTokenTransformer;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.samples.jfreechart.client.img.JFreechartSampleDataServiceAsync;
import org.eesgmbh.gimv.samples.jfreechart.shared.CommonSettings;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataRequest;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataResponse;
import org.eesgmbh.gimv.shared.util.Bounds;

public class MainJFreechartSampleController extends AbstractJFreechartController {

	private final JFreechartSampleBrowserHistoryManager historyManager;

	private final HandlerManager overviewHandlerManager;

	public MainJFreechartSampleController(HandlerManager handlerManager, HandlerManager overviewHandlerManager, JFreechartSampleDataServiceAsync jfreechartSampleService) {
		super(handlerManager, jfreechartSampleService);
		this.overviewHandlerManager = overviewHandlerManager;

		this.historyManager = new JFreechartSampleBrowserHistoryManager(handlerManager);

		this.handlerManager.addHandler(LoadImageDataEvent.TYPE, new LoadImageDataEventHandlerImpl());
		this.handlerManager.addHandler(SetDomainBoundsEvent.TYPE, new SetBoundsEventHandlerImpl());
	}

	@SuppressWarnings("deprecation")
	public void init(Viewport viewport) {
		//define the default request data
		currentImageDataRequest = new ImageDataRequest(
				viewport.getOffsetWidth(), viewport.getOffsetHeight(),
				"PEGELONLINE Station MAXAU", "Date", "Value", true, true, true, false);

		//telling everyone about the size of the viewport
		handlerManager.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, viewport.getOffsetWidth(), 0, viewport.getOffsetHeight())));

		//checking for history tokens
		if (this.historyManager.hasUrlHistoryToken()) {

			//a bit overstated, ends up in the JFreechartSampleBrowserHistoryManager anyway, but we follow the GWT way
			History.fireCurrentHistoryState();

		} else {
			//Initialize bounds to a default
			onSetDomainBounds(new SetDomainBoundsEvent(CommonSettings.INITIAL_BOUNDS));

			//and render the image on the server side
			onLoadImageData();
		}
	}

	private void onLoadImageData() {
		//async image request
		jfreechartSampleService.getImageData(currentImageDataRequest, new AsyncCallback<ImageDataResponse>() {
			public void onFailure(Throwable th) {
				th.printStackTrace();

				Window.alert(th.getMessage());
			}

			public void onSuccess(ImageDataResponse imageDataResponse) {
				handlerManager.fireEvent(new SetImageUrlEvent(imageDataResponse.getImageUrl()));

				handlerManager.fireEvent(new SetDataAreaPixelBoundsEvent(imageDataResponse.getPlotArea()));

				handlerManager.fireEvent(new SetMaxDomainBoundsEvent(imageDataResponse.getMaxDomainBounds()));

				SetDomainBoundsEvent setDomainBoundsEvent = new SetDomainBoundsEvent(imageDataResponse.getDomainBounds());

				handlerManager.fireEvent(setDomainBoundsEvent);
				handlerManager.fireEvent(new SetImageEntitiesEvent(imageDataResponse.getImageEntities()));

				History.newItem("bounds=" + DefaultSetDomainBoundsEventHistoryTokenTransformer.toHistoryTokenValue(setDomainBoundsEvent), false);

				//tell the overview controller about its overview bounds
				overviewHandlerManager.fireEvent(new SetOverviewDomainBoundsEvent(setDomainBoundsEvent.getBounds()));
			}
		});
	}

	private void onSetDomainBounds(SetDomainBoundsEvent event) {
		currentImageDataRequest.setDomainBounds(event.getBounds());
	}

	private class LoadImageDataEventHandlerImpl implements LoadImageDataEventHandler {
		public void onLoadImageData(LoadImageDataEvent event) {
			MainJFreechartSampleController.this.onLoadImageData();
		}
	}

	private class SetBoundsEventHandlerImpl implements SetDomainBoundsEventHandler {
		public void onSetDomainBounds(SetDomainBoundsEvent event) {
			MainJFreechartSampleController.this.onSetDomainBounds(event);
		}
	}
}
