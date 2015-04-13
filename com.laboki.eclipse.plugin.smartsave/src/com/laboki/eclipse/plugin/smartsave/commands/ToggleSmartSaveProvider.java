package com.laboki.eclipse.plugin.smartsave.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.main.EditorContext;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;

public final class ToggleSmartSaveProvider extends AbstractSourceProvider {

	public final static String SMART_SAVE_IS_ENABLED =
		"com.laboki.eclipse.plugin.smartsave.variable.smartSaveIsEnabled";

	public ToggleSmartSaveProvider() {
		EventBus.register(this);
		EditorContext.out("enable toggle smart save provider");
	}

	@Override
	public Map getCurrentState() {
		final Map<String, Boolean> currentState = new HashMap<>(1);
		currentState.put(ToggleSmartSaveProvider.SMART_SAVE_IS_ENABLED,
			EditorContext.canSaveAutomatically());
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] {
			ToggleSmartSaveProvider.SMART_SAVE_IS_ENABLED
		};
	}

	@Override
	public void dispose() {
		EventBus.unregister(this);
		EditorContext.out("disable toggle smart save provider");
	}

	@Subscribe
	public void eventHandler(final PreferenceStoreChangeEvent event) {
		new AsyncTask() {

			@Override
			public void execute() {
				ToggleSmartSaveProvider.this.update();
			}
		}.setDelay(EditorContext.SHORT_DELAY).start();
	}

	protected void update() {
		this.fireSourceChanged(ISources.WORKBENCH,
			ToggleSmartSaveProvider.SMART_SAVE_IS_ENABLED,
			EditorContext.canSaveAutomatically());
		EditorContext.out("fired source change event "
			+ EditorContext.canSaveAutomatically());
	}
}
