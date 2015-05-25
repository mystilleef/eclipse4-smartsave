package com.laboki.eclipse.plugin.smartsave.preferences.ui;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Joiner;
import com.google.common.eventbus.Subscribe;
import com.laboki.eclipse.plugin.smartsave.contexts.EditorContext;
import com.laboki.eclipse.plugin.smartsave.events.AddContentTypeToBlacklistEvent;
import com.laboki.eclipse.plugin.smartsave.events.RemoveContentTypeFromBlacklistEvent;
import com.laboki.eclipse.plugin.smartsave.instance.EventBusInstance;
import com.laboki.eclipse.plugin.smartsave.task.Task;
import com.laboki.eclipse.plugin.smartsave.task.TaskMutexRule;

public final class ContentTypeBlacklistUpdater
	extends
		EventBusInstance {

	private static final TaskMutexRule RULE = new TaskMutexRule();

	public ContentTypeBlacklistUpdater() {
		super();
	}

	@Subscribe
	public static void
	eventHandler(final AddContentTypeToBlacklistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				ContentTypeBlacklistUpdater.update(event.getContentTypeId(), true);
			}
		}.setRule(ContentTypeBlacklistUpdater.RULE)
			.setPriority(Job.INTERACTIVE)
			.start();
	}

	@Subscribe
	public static void
	eventHandler(final RemoveContentTypeFromBlacklistEvent event) {
		new Task() {

			@Override
			public void
			execute() {
				ContentTypeBlacklistUpdater.update(event.getContentTypeId(), false);
			}
		}.setRule(ContentTypeBlacklistUpdater.RULE)
			.setPriority(Job.INTERACTIVE)
			.start();
	}

	protected static void
	update(final String contentId, final boolean canAdd) {
		final ArrayList<String> blacklist = EditorContext.getBlacklist();
		ContentTypeBlacklistUpdater.updateList(canAdd, contentId, blacklist);
		EditorContext.setBlacklist(Joiner.on(";").join(blacklist));
	}

	private static void
	updateList(	final boolean canAdd,
							final String contentId,
							final ArrayList<String> blacklist) {
		if (canAdd) blacklist.add(contentId);
		else blacklist.remove(contentId);
	}
}
