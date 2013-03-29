package nl.runnable.alfresco.extensions.webconsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.runnable.alfresco.extensions.Extension;
import nl.runnable.alfresco.extensions.ExtensionRegistry;
import nl.runnable.alfresco.webscripts.annotations.Attribute;
import nl.runnable.alfresco.webscripts.annotations.Authentication;
import nl.runnable.alfresco.webscripts.annotations.AuthenticationType;
import nl.runnable.alfresco.webscripts.annotations.HttpMethod;
import nl.runnable.alfresco.webscripts.annotations.RequestParam;
import nl.runnable.alfresco.webscripts.annotations.Uri;
import nl.runnable.alfresco.webscripts.annotations.UriVariable;
import nl.runnable.alfresco.webscripts.annotations.WebScript;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * Web Script for the Dynamic Extensions console.
 * 
 * @author Laurens Fridael
 * 
 */
@ManagedBean
@WebScript
@Authentication(AuthenticationType.ADMIN)
public class WebConsole implements BundleContextAware {

	private static final int FRAMEWORK_BUNDLE_ID = 0;

	/* Dependencies */

	@Inject
	private ExtensionRegistry extensionRegistry;

	private BundleContext bundleContext;

	/* Main operations */

	/**
	 * Generates reference data for the index page.
	 * 
	 * @return The model.
	 */
	@Uri(method = HttpMethod.GET, value = "/dynamic-extensions", defaultFormat = "html")
	public Map<String, Object> index() {
		final Map<String, Object> model = new HashMap<String, Object>();
		populateWithExtensions(model);
		model.put(TemplateVariables.FILE_INSTALL_PATHS, extensionRegistry.getContainer().getFileInstallPaths());
		return model;
	}

	/**
	 * Restarts the {@link Bundle} with the given ID.
	 * 
	 * @param wait
	 * @param response
	 * @throws IOException
	 */
	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/framework/restart")
	public void restartFramework(@RequestParam(defaultValue = "0") final long wait,
			@Attribute final ResponseHelper response) throws IOException {
		restartBundle(bundleContext.getBundle(FRAMEWORK_BUNDLE_ID), response, wait);
	}

	/**
	 * Restarts the {@link Bundle} with the given ID.
	 * 
	 * @param bundleId
	 * @param response
	 * @throws IOException
	 */
	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/bundles/{bundleId}/restart")
	public void restartBundle(@UriVariable final long bundleId, @Attribute final ResponseHelper response)
			throws IOException {
		restartBundle(bundleContext.getBundle(bundleId), response, 0);
	}

	/**
	 * Uninstalls {@link Bundle} with the given ID.
	 * 
	 * @param bundleId
	 * @param response
	 * @throws IOException
	 */
	@Uri(method = HttpMethod.POST, value = "/dynamic-extensions/bundles/uninstall")
	public void uninstallBundle(@RequestParam final long bundleId, @Attribute final ResponseHelper response)
			throws IOException {
		if (bundleId == 0) {
			response.status(HttpServletResponse.SC_BAD_REQUEST, "Cannot uninstall Framework bundle.");
			return;
		}
		final Bundle bundle = bundleContext.getBundle(bundleId);
		if (bundle != null) {
			try {
				bundle.uninstall();
				response.redirectToIndex();
			} catch (final BundleException e) {
				final String message = String.format("Error uninstalling bundle: %s", e.getMessage());
				response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
			}
		} else {
			response.status(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/* Attributes */

	@Attribute
	protected ResponseHelper getResponseHelper(final WebScriptRequest request, final WebScriptResponse response) {
		return new ResponseHelper(request, response);
	}

	/* Utility operations */

	/**
	 * Populates the given model with {@link Extension}.
	 * 
	 * @param model
	 */
	protected void populateWithExtensions(final Map<String, Object> model) {
		final List<Extension> extensions = new ArrayList<Extension>();
		final List<Extension> coreBundles = new ArrayList<Extension>();
		for (final Extension extension : extensionRegistry.getExtensions()) {
			if (extension.isCoreBundle()) {
				coreBundles.add(extension);
			} else {
				extensions.add(extension);
			}

		}
		Collections.sort(extensions);
		Collections.sort(coreBundles);
		model.put(TemplateVariables.EXTENSIONS, extensions);
		model.put(TemplateVariables.CORE_BUNDLES, coreBundles);
	}

	protected void restartBundle(final Bundle bundle, final ResponseHelper response, final long timeToWait)
			throws IOException {
		try {
			bundle.update();
			if (timeToWait > 0) {
				try {
					Thread.sleep(timeToWait);
				} catch (final InterruptedException e) {
				}
			}
			response.redirectToIndex();
		} catch (final BundleException e) {
			final String message = String.format("Error restarting framework: %s", e.getMessage());
			response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
		}
	}

	/* Dependencies */

	@Override
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}