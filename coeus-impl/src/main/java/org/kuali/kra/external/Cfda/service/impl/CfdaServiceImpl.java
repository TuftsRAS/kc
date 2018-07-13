/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.external.Cfda.service.impl;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.kra.award.home.CFDA;
import org.kuali.kra.external.Cfda.CfdaService;
import org.kuali.kra.external.Cfda.CfdaUpdateResults;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entriesToMap;
import static org.kuali.coeus.sys.framework.util.CollectionUtils.entry;


/**
 * This class is an implementation of the CfdaService.
 * Updates the CFDA table with values from sam.gov
 */
public class CfdaServiceImpl implements CfdaService {

    private static final Pattern CFDA_NUMBER_PATTERN = Pattern.compile("^[0-9]{2}\\.[0-9]{3}$");
    private static final Logger LOG = LogManager.getLogger(CfdaServiceImpl.class);

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;

    /**
     * This method retrieves cfda codes from the government site.
     */
    protected Map<String, CFDA> retrieveGovCodes() throws IOException {
        final String govURL = getCfdaUrl();

        LOG.info("Getting government file from URL " + govURL + " for update");

        final Resource csvFile = new DefaultResourceLoader(this.getClass().getClassLoader()).getResource(govURL);
        if (csvFile != null && csvFile.exists() && csvFile.isReadable()) {
            LOG.info("reading input file");
            try (InputStreamReader screenReader = new InputStreamReader(csvFile.getInputStream())) {
                final List<CSVRecord> records = CSVFormat.DEFAULT.withSkipHeaderRecord(true).parse(screenReader).getRecords();
                return records
                        .stream()
                        .filter(record -> {
                            final String number = record.get(1);
                            final Matcher regExResult = CFDA_NUMBER_PATTERN.matcher(number);
                            return regExResult.matches();
                        }).map(record -> {
                            final CFDA cfda = new CFDA();
                            cfda.setCfdaNumber(record.get(1));
                            cfda.setCfdaProgramTitleName(trimProgramTitleName(record.get(0)));
                            cfda.setCfdaMaintenanceTypeId(Constants.CFDA_MAINT_TYP_ID_AUTOMATIC);
                            return cfda;
                        }).map(cfda -> entry(cfda.getCfdaNumber(), cfda))
                        .collect(entriesToMap(TreeMap::new));
            }
        } else {
            throw new IOException("The file could not be retrieved from " + govURL);
        }
    }

    /**
     * This method gets the url from the parameter service used to retrieve CFDA file.
     */
    protected String getCfdaUrl() {
        return getParameterService().getParameterValueAsString(Constants.MODULE_NAMESPACE_AWARD,
                Constants.PARAMETER_COMPONENT_DOCUMENT, Constants.CFDA_GOV_URL_PARAMETER);
    }

    /**
     * This method updates the CFDA table with the values received from the
     * gov site.
     */
    @Override
    public CfdaUpdateResults updateCfda() {

        final Map<String, CFDA> govCfdaMap;
        try {
            govCfdaMap = retrieveGovCodes();
        } catch (IOException ioe) {
            final CfdaUpdateResults updateResults = new CfdaUpdateResults();
            updateResults.setMessage("Problem encountered while retrieving cfda numbers, the database was not updated. " + ioe.getMessage());
            LOG.error(ioe.getMessage(), ioe);
            return updateResults;
        }

        final CfdaUpdateResults updateResults = new CfdaUpdateResults();
        final Map<String, CFDA> kcMap = getCfdaValuesInDatabase();
        updateResults.setNumberOfRecordsInKcDatabase(kcMap.size());
        updateResults.setNumberOfRecordsRetrievedFromWebSite(govCfdaMap.size());

        kcMap.forEach((cfdaNumber, kcCfda) -> {
            if (kcCfda.getCfdaMaintenanceTypeId().equalsIgnoreCase(Constants.CFDA_MAINT_TYP_ID_MANUAL)) {
                // Leave it alone. It's maintained manually.
                updateResults.setNumberOfRecordsNotUpdatedBecauseManual(updateResults.getNumberOfRecordsNotUpdatedBecauseManual() + 1);
            } else if (kcCfda.getCfdaMaintenanceTypeId().equalsIgnoreCase(Constants.CFDA_MAINT_TYP_ID_AUTOMATIC)) {

                final CFDA govCfda = govCfdaMap.get(cfdaNumber);
                if (govCfda == null) {
                    if (kcCfda.getActive()) {
                        kcCfda.setActive(false);
                        businessObjectService.save(kcCfda);
                        updateResults.setNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite(updateResults.getNumberOfRecordsDeactivatedBecauseNoLongerOnWebSite() + 1);
                    } else {
                        updateResults.setNumberOfRecordsNotUpdatedForHistoricalPurposes(updateResults.getNumberOfRecordsNotUpdatedForHistoricalPurposes() + 1);
                    }
                } else {
                    if (kcCfda.getActive()) {
                        updateResults.setNumberOfRecordsUpdatedBecauseAutomatic(updateResults.getNumberOfRecordsUpdatedBecauseAutomatic() + 1);
                    } else {
                        kcCfda.setActive(true);
                        updateResults.setNumberOfRecordsReActivated(updateResults.getNumberOfRecordsReActivated() + 1);
                    }

                    kcCfda.setCfdaProgramTitleName(govCfda.getCfdaProgramTitleName());
                    businessObjectService.save(kcCfda);
                }
            }
            govCfdaMap.remove(cfdaNumber);
        });

        addNew(govCfdaMap);
        updateResults.setNumberOfRecordsNewlyAddedFromWebSite(govCfdaMap.size());

        return updateResults;
    }

    /**
     * This method gets the current CFDA values in the table.
     */
    protected Map<String, CFDA> getCfdaValuesInDatabase() {
        final Collection<CFDA> cfdaValues = getBusinessObjectService().findAll(CFDA.class);
        return cfdaValues.stream()
                .map(o -> entry(o.getCfdaNumber(), o))
                .collect(entriesToMap(TreeMap::new));
    }

    /**
     * This method adds new cfda numbers to the cfda table.
     */
    protected void addNew(Map<String, CFDA> newCfdas) {
        final List<CFDA> cfdas = newCfdas.values()
                .stream()
                .peek(cfda -> {
                    final String cfdaProgramTitleName = trimProgramTitleName(cfda.getCfdaProgramTitleName());
                    cfda.setCfdaProgramTitleName(SQLUtils.cleanString(cfdaProgramTitleName));
                    cfda.setActive(true);
                    cfda.setCfdaMaintenanceTypeId(Constants.CFDA_MAINT_TYP_ID_AUTOMATIC);
                }).collect(Collectors.toList());
        getBusinessObjectService().save(cfdas);
    }

    protected String trimProgramTitleName(String programTitleName) {
        return StringUtils.substring(programTitleName, 0, Constants.MAX_ALLOWABLE_CFDA_PGM_TITLE_NAME);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }
}
