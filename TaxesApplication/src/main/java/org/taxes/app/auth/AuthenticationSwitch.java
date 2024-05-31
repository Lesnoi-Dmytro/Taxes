package org.taxes.app.auth;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AuthenticationSwitch {
	public enum View {LOG_IN, REGISTER};

	private final ObjectProperty<View> currentView = new SimpleObjectProperty<>(View.LOG_IN);

	public ObjectProperty<View> currentViewProperty() {
		return currentView;
	}

	public final View getCurrentView() {
		return currentView.get();
	}

	public final void setCurrentView(View currentView) {
		this.currentView.set(currentView);
	}
}
