package org.eesgmbh.gimv.samples.jfreechart.client.img;

import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataRequest;
import org.eesgmbh.gimv.samples.jfreechart.shared.ImageDataResponse;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("imagedata")
public interface JFreechartSampleDataService extends RemoteService {
	ImageDataResponse getImageData(ImageDataRequest imageDataRequest);
}
