package org.eesgmbh.gimv.samples.jfreechart.client;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.eesgmbh.gimv.client.history.DefaultSetDomainBoundsEventHistoryTokenTransformer;
import org.eesgmbh.gimv.client.history.UnparsableHistoryTokenException;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

public class JFreechartSampleBrowserHistoryManager {

	private final HandlerManager handlerManager;

	public JFreechartSampleBrowserHistoryManager(HandlerManager handlerManager) {
		this.handlerManager = handlerManager;

		History.addValueChangeHandler(new ValueChangeHandlerImpl());
	}

	public boolean hasUrlHistoryToken() {
		return !("".equals(History.getToken()));
	}

	private void onHistoryChanged(ValueChangeEvent<String> event) {
		if (event.getValue().contains("bounds=")) {
			String boundsHistoryTokenValue = event.getValue().substring(event.getValue().indexOf("bounds=") + "bounds=".length());

			try {
				handlerManager.fireEvent(DefaultSetDomainBoundsEventHistoryTokenTransformer.toEvent(boundsHistoryTokenValue));
				handlerManager.fireEvent(new LoadImageDataEvent());

			} catch (UnparsableHistoryTokenException e) {
				e.printStackTrace();

				Window.alert(e.getMessage());
			}
		}
	}

	private final class ValueChangeHandlerImpl implements ValueChangeHandler<String> {
		public void onValueChange(ValueChangeEvent<String> event) {
			onHistoryChanged(event);
		}
	}
}
