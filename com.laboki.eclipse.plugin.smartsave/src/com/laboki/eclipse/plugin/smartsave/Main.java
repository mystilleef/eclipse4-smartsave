// $codepro.audit.disable packageNamingConvention
package com.laboki.eclipse.plugin.smartsave;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MAddon;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.MApplicationFactory;

import com.laboki.eclipse.plugin.smartsave.saver.AutomaticSaverInitializer;

public final class Main {

	@Execute
	public static void execute(final MApplication application) {
		Main.installAddons(application, AutomaticSaverInitializer.class);
	}

	private static void installAddons(final MApplication application, final Class<?>... addonClasses) {
		for (final Class<?> addonClass : addonClasses)
			Main.installAddon(application, addonClass);
	}

	private static void installAddon(final MApplication application, final Class<?> addonClass) {
		Main.removeAddon(application, addonClass);
		if (Main.addonIsInstalled(application, addonClass)) return;
		application.getAddons().add(Main.createAddon(addonClass));
	}

	private static void removeAddon(final MApplication application, final Class<?> addonClass) {
		final List<MAddon> addons = application.getAddons();
		final MAddon addon = Main.findAddon(addonClass, addons);
		if (addon != null) addons.remove(addon);
	}

	private static MAddon findAddon(final Class<?> addonClass, final List<MAddon> addons) {
		for (final MAddon mAddon : addons)
			if (Main.hasFoundAddon(addonClass, mAddon)) return mAddon;
		return null;
	}

	private static boolean hasFoundAddon(final Class<?> addonClass, final MAddon mAddon) {
		return mAddon.getContributionURI().equals(Main.getAddonContributionURI(addonClass));
	}

	private static String getAddonContributionURI(final Class<?> addonClass) {
		return MessageFormat.format(AddonMetadata.CONTRIBUTION_URI, AddonMetadata.PLUGIN_NAME, addonClass.getCanonicalName());
	}

	private static boolean addonIsInstalled(final MApplication application, final Class<?> addonClass) {
		return Main.getAllContributionURIs(application).contains(Main.getAddonContributionURI(addonClass));
	}

	private static Set<String> getAllContributionURIs(final MApplication application) {
		final Set<String> contributionURIs = new HashSet<>();
		for (final MAddon addon : application.getAddons())
			if (addon.getContributionURI() != null) contributionURIs.add(addon.getContributionURI());
		return contributionURIs;
	}

	private static MAddon createAddon(final Class<?> addonClass) {
		final MAddon addon = MApplicationFactory.INSTANCE.createAddon();
		addon.setElementId(addonClass.getCanonicalName());
		addon.setContributionURI(Main.getAddonContributionURI(addonClass));
		addon.setContributorURI(AddonMetadata.CONTRIBUTOR_URI);
		return addon;
	}

	@Override
	public String toString() {
		return String.format("Processor [getClass()=%s, hashCode()=%s, toString()=%s]", this.getClass(), Integer.valueOf(this.hashCode()), super.toString());
	}
}
