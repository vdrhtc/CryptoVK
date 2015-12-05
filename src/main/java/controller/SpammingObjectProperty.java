package controller;

import javafx.beans.property.SimpleObjectProperty;

public class SpammingObjectProperty<T> extends SimpleObjectProperty<T> {
	
	public SpammingObjectProperty() {
		super();
	}
	
	@Override
	public void set(T newValue) {
		if (!newValue.equals(get()))
			super.set(newValue);
		else
			fireValueChangedEvent();
	}
}
