package com.laboki.eclipse.plugin.smartsave.commands;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.PreferenceStoreChangeEvent;
import com.laboki.eclipse.plugin.smartsave.main.EventBus;
import com.laboki.eclipse.plugin.smartsave.preferences.Store;
import com.laboki.eclipse.plugin.smartsave.task.AsyncTask;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class ToggleSmartSaveProvider extends AbstractSourceProvider {

	private static final int ONE_SECOND = 1000;
	private static final String FAMILY = "Toggle smart save provider family";
	private static final TaskMutexRule RULE = new TaskMutexRule();
	public final static String SMART_SAVE_IS_ENABLED =
		"com.laboki.eclipse.plugin.smartsave.variable.smartSaveIsEnabled";
	public final static String IS_BLACKLISTED =
		"com.laboki.eclipse.plugin.smartsave.variable.isBlacklisted";

	public ToggleSmartSaveProvider() {
		EventBus.register(this);
		this.update();
	}

	@Override
	public Map
	getCurrentState() {
		final Map<String, Boolean> currentState = new HashMap<>(1);
		currentState.put(ToggleSmartSaveProvider.SMART_SAVE_IS_ENABLED,
			Store.getCanSaveAutomatically());
		currentState.put(ToggleSmartSaveProvider.IS_BLACKLISTED,
			EditorContext.isBlacklisted(EditorContext.getEditor()));
		return currentState;
	}

	@Override
	public String[]
	getProvidedSourceNames() {
		return new String[] {
			ToggleSmartSaveProvider.SMART_SAVE_IS_ENABLED,
			ToggleSmartSaveProvider.IS_BLACKLISTED
		};
	}

	@Override
	public void
	dispose() {
		EventBus.unregister(this);
	}

	@Subscribe
	public void
	eventHandler(final PreferenceStoreChangeEvent event) {
		new AsyncTask() {

			@Override
			public void
			execute() {
				ToggleSmartSaveProvider.this.update();
			}
		}.setFamily(ToggleSmartSaveProvider.FAMILY)
			.setPriority(Job.INTERACTIVE)
			.setDelay(ToggleSmartSaveProvider.ONE_SECOND)
			.setRule(ToggleSmartSaveProvider.RULE)
			.start();
	}

	protected void
	update() {
		this.fireSourceChanged(ISources.WORKBENCH,
			ToggleSmartSaveProvider.SMART_SAVE_IS_ENABLED,
			Store.getCanSaveAutomatically());
		this.fireSourceChanged(ISources.WORKBENCH,
			ToggleSmartSaveProvider.IS_BLACKLISTED,
			EditorContext.isBlacklisted(EditorContext.getEditor()));
	}
}
