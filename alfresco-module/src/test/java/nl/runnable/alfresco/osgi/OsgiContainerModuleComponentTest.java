/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.osgi;

import static org.junit.Assert.*;

import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration test for {@link OsgiContainerModuleComponent}.
 * 
 * @author Laurens Fridael
 * 
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class OsgiContainerModuleComponentTest {

	private OsgiContainerModuleComponent moduleComponent;

	@Autowired
	public void setModuleComponent(final OsgiContainerModuleComponent moduleComponent) {
		this.moduleComponent = moduleComponent;
	}

	@Before
	public void setup() {
		moduleComponent.executeInternal();
	}

	@Test
	public void testRegisteredServices() {
		final Framework framework = moduleComponent.getFrameworkManager().getFramework();
		final BundleContext bundleContext = framework.getBundleContext();
		assertNotNull(bundleContext.getServiceReference(CategoryService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(ContentService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(FileFolderService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(NodeService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(PolicyComponent.class.getName()));
		assertNotNull(bundleContext.getServiceReference(SearchService.class.getName()));
		assertNotNull(bundleContext.getServiceReference(TransactionService.class.getName()));
	}

}
