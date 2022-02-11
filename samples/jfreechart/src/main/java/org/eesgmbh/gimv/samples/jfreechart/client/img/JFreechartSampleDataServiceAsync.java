package org.eesgmbh.gimv.samples.jfreechart.client.img;

import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataRequest;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface JFreechartSampleDataServiceAsync {
  void getImageData(ImageDataRequest imageDataRequest, AsyncCallback<ImageDataResponse> callback);
}
