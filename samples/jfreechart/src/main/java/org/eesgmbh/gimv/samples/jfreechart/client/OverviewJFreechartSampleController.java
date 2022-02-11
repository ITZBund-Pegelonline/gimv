package org.eesgmbh.gimv.samples.jfreechart.client;

import java.util.Date;

import org.eesgmbh.gimv.client.controls.ViewportDimensionsListenerControl;
import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.event.LoadImageDataEventHandler;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetImageUrlEvent;
import org.eesgmbh.gimv.client.event.SetMaxDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.samples.jfreechart.client.img.JFreechartSampleDataServiceAsync;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataRequest;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataResponse;
import org.eesgmbh.gimv.shared.util.Bounds;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OverviewJFreechartSampleController extends AbstractJFreechartController {

	public OverviewJFreechartSampleController(HandlerManager handlerManager, JFreechartSampleDataServiceAsync jfreechartSampleService) {
		super(handlerManager, jfreechartSampleService);

		this.handlerManager.addHandler(LoadImageDataEvent.TYPE, new LoadImageDataEventHandlerImpl());
	}

	@SuppressWarnings("deprecation")
	public void init(Viewport viewport) {
		//initialize to some defaults
		currentImageDataRequest = new ImageDataRequest(
				new Bounds(
						//must use deprecated Date here, 110 is 2010 (1900 + 110)
						new Date(110, 1, 23).getTime(), new Date(110, 1, 27, 23, 59).getTime(),
						470l, 230l
				),
				viewport.getOffsetWidth(), viewport.getOffsetHeight(),
				null, null, null, false, false, false, true);

		//telling everyone about the size of the viewport
		handlerManager.fireEvent(new SetViewportPixelBoundsEvent(new Bounds(0, viewport.getOffsetWidth(), 0, viewport.getOffsetHeight())));

		//and render the image on the server side
		handlerManager.fireEvent(new LoadImageDataEvent());
	}

	/**
	 * Will be invoked once by init and possibly by {@link ViewportDimensionsListenerControl}
	 */
	private class LoadImageDataEventHandlerImpl implements LoadImageDataEventHandler {
		public void onLoadImageData(LoadImageDataEvent event) {
			//request the image for the overview async
			jfreechartSampleService.getImageData(currentImageDataRequest, new AsyncCallback<ImageDataResponse>() {
				public void onFailure(Throwable th) {
					th.printStackTrace();

					Window.alert(th.getMessage());
				}

				public void onSuccess(ImageDataResponse imageDataResponse) {
					handlerManager.fireEvent(new SetImageUrlEvent(imageDataResponse.getImageUrl()));

					//TODO: this must be fired and used in OverviewPresenter
//					handlerManager.fireEvent(new SetDataAreaPixelBoundsEvent(imageDataResponse.getPlotArea()));

					handlerManager.fireEvent(new SetMaxDomainBoundsEvent(imageDataResponse.getMaxDomainBounds()));

					SetDomainBoundsEvent setDomainBoundsEvent = new SetDomainBoundsEvent(imageDataResponse.getDomainBounds());

					handlerManager.fireEvent(setDomainBoundsEvent);
				}
			});
		}
	}
}
