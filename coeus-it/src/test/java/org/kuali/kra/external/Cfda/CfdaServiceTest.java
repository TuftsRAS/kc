/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.external.Cfda;

import org.junit.Assert;
import org.junit.Test;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.award.home.CFDA;
import org.kuali.kra.external.Cfda.service.impl.CfdaServiceImpl;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collections;


public class CfdaServiceTest extends KcIntegrationTestBase {

    @Test
    public void test_load_empty_db_largefile() throws IOException {
        final Resource largefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/AssistanceListings_DataGov_PUBLIC_CURRENT.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults results = service.updateCfda();

        Assert.assertNull(results.getMessage());
        Assert.assertEquals(2294, results.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(0, results.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, results.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, results.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, results.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, results.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(0, results.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(2294, results.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_load_largefile_reload() throws IOException {
        final Resource largefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/AssistanceListings_DataGov_PUBLIC_CURRENT.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));


        //execute twice with the same dataset
        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults secondUpdateResults = service.updateCfda();

        Assert.assertNull(secondUpdateResults.getMessage());
        Assert.assertEquals(2294, secondUpdateResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(2294, secondUpdateResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(2294, secondUpdateResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_load_largefile_reload_small() throws IOException {
        final Resource largefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/AssistanceListings_DataGov_PUBLIC_CURRENT.csv");
        final Resource smallFile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/cfda_small.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        service.setCfdaUrl(smallFile.getURL().toString());
        final CfdaUpdateResults secondUpdateResults = service.updateCfda();

        Assert.assertNull(secondUpdateResults.getMessage());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(2294, secondUpdateResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(2293, secondUpdateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_load_largefile_reload_small_twice() throws IOException {
        final Resource largefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/AssistanceListings_DataGov_PUBLIC_CURRENT.csv");
        final Resource smallFile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/cfda_small.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        service.setCfdaUrl(smallFile.getURL().toString());
        final CfdaUpdateResults secondUpdateResults = service.updateCfda();

        service.setCfdaUrl(smallFile.getURL().toString());
        final CfdaUpdateResults thirdUpdateResults = service.updateCfda();

        Assert.assertNull(thirdUpdateResults.getMessage());
        Assert.assertEquals(1, thirdUpdateResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(2294, thirdUpdateResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(2293, thirdUpdateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(1, thirdUpdateResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_load_largefile_reload_small_reload_largefile() throws IOException {
        final Resource largefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/AssistanceListings_DataGov_PUBLIC_CURRENT.csv");
        final Resource smallFile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/cfda_small.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        service.setCfdaUrl(smallFile.getURL().toString());
        final CfdaUpdateResults secondUpdateResults = service.updateCfda();

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults thirdUpdateResults = service.updateCfda();

        Assert.assertNull(thirdUpdateResults.getMessage());
        Assert.assertEquals(2294, thirdUpdateResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(2294, thirdUpdateResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(2293, thirdUpdateResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(1, thirdUpdateResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, thirdUpdateResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_load_largefile_load_unique_small() throws IOException {
        final Resource largefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/AssistanceListings_DataGov_PUBLIC_CURRENT.csv");
        final Resource smallUniquefile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/cfda_small_unique.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl(largefile.getURL().toString());
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        service.setCfdaUrl(smallUniquefile.getURL().toString());
        final CfdaUpdateResults secondUpdateResults = service.updateCfda();

        Assert.assertNull(secondUpdateResults.getMessage());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(2294, secondUpdateResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(2294, secondUpdateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_load_small_twice_manual_updates() throws IOException {
        final Resource smallFile = new DefaultResourceLoader().getResource("classpath:org/kuali/kra/external/Cfda/cfda_small.csv");

        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl(smallFile.getURL().toString());
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        final CFDA cfda = KcServiceLocator.getService(BusinessObjectService.class).findByPrimaryKey(CFDA.class, Collections.singletonMap("cfdaNumber","10.001"));
        cfda.setCfdaMaintenanceTypeId(Constants.CFDA_MAINT_TYP_ID_MANUAL);
        KcServiceLocator.getService(BusinessObjectService.class).save(cfda);

        service.setCfdaUrl(smallFile.getURL().toString());
        final CfdaUpdateResults secondUpdateResults = service.updateCfda();

        Assert.assertNull(secondUpdateResults.getMessage());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(1, secondUpdateResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, secondUpdateResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_bad_url() {
        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl("not_found_url");
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        Assert.assertNotNull(initialLoadResults.getMessage());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    @Test
    public void test_good_url_bad_resource() {
        final SettableUrlCfdaServiceImpl service = new SettableUrlCfdaServiceImpl();
        service.setBusinessObjectService(KcServiceLocator.getService(BusinessObjectService.class));
        service.setParameterService(KcServiceLocator.getService(ParameterService.class));

        service.setCfdaUrl("https://www.google.com/");
        final CfdaUpdateResults initialLoadResults = service.updateCfda();

        Assert.assertNotNull(initialLoadResults.getMessage());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsRetrievedFromWebSite());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsInKcDatabase());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsReActivated());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsNotUpdatedBecauseManual());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsUpdatedBecauseAutomatic());
        Assert.assertEquals(0, initialLoadResults.getNumberOfRecordsNewlyAddedFromWebSite());
    }

    private static class SettableUrlCfdaServiceImpl extends CfdaServiceImpl {
        private String cfdaUrl;

        @Override
        protected String getCfdaUrl() {
            return cfdaUrl;
        }

        public void setCfdaUrl(String cfdaUrl) {
            this.cfdaUrl = cfdaUrl;
        }
    }
}
