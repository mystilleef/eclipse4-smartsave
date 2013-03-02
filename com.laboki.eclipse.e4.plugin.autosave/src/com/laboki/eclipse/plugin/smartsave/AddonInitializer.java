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

public final class AddonInitializer {

	@Execute
	public static void execute(final MApplication application) {
		AddonInitializer.installAddons(application, AutomaticSaverInitializer.class);
	}

	private static void installAddons(final MApplication application, final Class<?>... addonClasses) {
		for (final Class<?> addonClass : addonClasses)
			AddonInitializer.installAddon(application, addonClass);
	}

	private static void installAddon(final MApplication application, final Class<?> addonClass) {
		AddonInitializer.removeAddon(application, addonClass);
		if (AddonInitializer.addonIsInstalled(application, addonClass)) return;
		application.getAddons().add(AddonInitializer.createAddon(addonClass));
	}

	private static void removeAddon(final MApplication application, final Class<?> addonClass) {
		final List<MAddon> addons = application.getAddons();
		final MAddon addon = AddonInitializer.findAddon(addonClass, addons);
		if (addon != null) addons.remove(addon);
	}

	private static MAddon findAddon(final Class<?> addonClass, final List<MAddon> addons) {
		for (final MAddon mAddon : addons)
			if (AddonInitializer.hasFoundAddon(addonClass, mAddon)) return mAddon;
		return null;
	}

	private static boolean hasFoundAddon(final Class<?> addonClass, final MAddon mAddon) {
		return mAddon.getContributionURI().equals(AddonInitializer.getAddonContributionURI(addonClass));
	}

	private static String getAddonContributionURI(final Class<?> addonClass) {
		return MessageFormat.format(AddonMetadata.CONTRIBUTION_URI, AddonMetadata.PLUGIN_NAME, addonClass.getCanonicalName());
	}

	private static boolean addonIsInstalled(final MApplication application, final Class<?> addonClass) {
		return AddonInitializer.getAllContributionURIs(application).contains(AddonInitializer.getAddonContributionURI(addonClass));
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
		addon.setContributionURI(AddonInitializer.getAddonContributionURI(addonClass));
		addon.setContributorURI(AddonMetadata.CONTRIBUTOR_URI);
		return addon;
	}

	@Override
	public String toString() {
		return String.format("Processor [getClass()=%s, hashCode()=%s, toString()=%s]", this.getClass(), Integer.valueOf(this.hashCode()), super.toString());
	}
}
