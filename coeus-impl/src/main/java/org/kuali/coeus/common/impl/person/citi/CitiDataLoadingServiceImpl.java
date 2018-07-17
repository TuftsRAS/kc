/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.coeus.common.impl.person.citi;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.coeus.common.framework.person.citi.CitiDataLoadingService;
import org.kuali.coeus.common.framework.person.citi.PersonTrainingCitiRecord;
import org.kuali.coeus.common.framework.person.citi.PersonTrainingCitiRecordError;
import org.kuali.coeus.common.framework.person.citi.PersonTrainingCitiRecordStatus;
import org.kuali.coeus.sys.framework.util.CollectionUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.beans.PropertyEditorSupport;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Component("citiDataLoadingService")
public class CitiDataLoadingServiceImpl implements CitiDataLoadingService {

    private static final Logger LOG = LogManager.getLogger(CitiDataLoadingServiceImpl.class);

    private static final Pattern COMMA_REGEX = Pattern.compile(",");
    private static final String CITI_ENDPOINTS = "citi.endpoints";
    private static final String CITI_DELIMETER = "citi.delimiter";
    private static final String CITI_HEADER_LABEL_PREFIX = "citi.header.label.";

    @Autowired
    @Qualifier("restOperations")
    private RestOperations restOperations;

    @Autowired
    @Qualifier("kualiConfigurationService")
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("businessObjectService")
    private BusinessObjectService businessObjectService;


    @Override
    public void loadRecords() {
        final String citiEndpoints = getConfigurationService().getPropertyValueAsString(CITI_ENDPOINTS);

        if (StringUtils.isNotEmpty(citiEndpoints)) {
            getBusinessObjectService().deleteMatching(PersonTrainingCitiRecordError.class, Collections.emptyMap());
            getBusinessObjectService().deleteMatching(PersonTrainingCitiRecord.class, Collections.emptyMap());

            final char citiDelimiter = getConfigurationService().getPropertyValueAsString(CITI_DELIMETER).charAt(0);
            final Map<String, String> headerMap = getConfigurationService().getAllProperties().entrySet().stream()
                    .filter(e -> e.getKey().startsWith(CITI_HEADER_LABEL_PREFIX)).collect(CollectionUtils.entriesToMap());

            //Purposefully not using a custom ResponseExtractor to stream the response.  This means the entire response stays in memory to be
            //processed.  This is to avoid the citi endpoint from timing out when processing the response as a stream.
            COMMA_REGEX.splitAsStream(citiEndpoints)
                    .map(String::trim)
                    .map(endpoint -> getRestOperations().getForObject(endpoint, String.class))
                    .forEach(records -> {
                        try (Reader reader = new StringReader(records)) {
                            CSVParser parser = CSVFormat.DEFAULT
                                    .withDelimiter(citiDelimiter)
                                    .withFirstRecordAsHeader()
                                    .withAllowMissingColumnNames(true)
                                    .withIgnoreEmptyLines(true)
                                    .withIgnoreHeaderCase(true)
                                    .withIgnoreSurroundingSpaces(true)
                                    .withTrim(true)
                                    .withQuote(null)
                                    .parse(reader);

                            processRecords(headerMap, parser.iterator());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }


    /**
     * The iterator passed in is a streaming iterator from the Reader.  This means that all the records are not in
     * memory at the same time. This allows the code to iterate and save each record one at a time.  Instead of using
     * the iterator, if all the records were retrieved and saved in bulk, large datasets can cause an OutOfMemoryException
     */
    protected void processRecords(Map<String, String> headerMap, Iterator<CSVRecord> records) {
        StreamSupport.stream(((Iterable<CSVRecord>) () -> records).spliterator(), false).map(record -> {
            final PersonTrainingCitiRecord sr = new PersonTrainingCitiRecord();
            sr.setStatusCode(PersonTrainingCitiRecordStatus.STAGED.getCode());

            try {
                final BeanWrapper wrapper = new BeanWrapperImpl(sr);
                wrapper.registerCustomEditor(Timestamp.class, new CitiTimestampEditor());
                headerMap.entrySet().stream()
                        .filter(e -> record.isMapped(e.getValue()))
                        .forEach(e -> wrapper.setPropertyValue(e.getKey().replace(CITI_HEADER_LABEL_PREFIX, ""), record.get(e.getValue())));
            } catch (RuntimeException e) {
                sr.addError(new PersonTrainingCitiRecordError(e.getMessage()));
                LOG.error(record.toString() + " has an error", e);
            }
            sr.setRawRecord(record.toString());
            return sr;
        }).forEach(sr -> getBusinessObjectService().save(sr));
    }

    /**
     * CITI data uses multiple formats for Date/Time.  This property editor supports both formats.
     */
    private static class CitiTimestampEditor extends PropertyEditorSupport {
        private static final String TIMESTAMP_FORMAT1 = "MM/d/yyyy h:m:s a";
        private static final String TIMESTAMP_FORMAT2 = "MM/dd/yy";

        @Override
        public void setAsText(String value) {
            setValue(value);
        }

        @Override
        public Object getValue() {
            if (super.getValue() == null || StringUtils.isBlank(super.getValue().toString())) {
                return null;
            }

            final SimpleDateFormat format1 = new SimpleDateFormat(TIMESTAMP_FORMAT1);
            Date date;
            try {
                date = format1.parse(super.getValue().toString());
            } catch (ParseException e) {
                final SimpleDateFormat format2 = new SimpleDateFormat(TIMESTAMP_FORMAT2);
                try {
                    date = format2.parse(super.getValue().toString());
                } catch (ParseException e1) {
                    throw new RuntimeException(e);
                }
            }

            return new Timestamp(date.getTime());
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public RestOperations getRestOperations() {
        return restOperations;
    }

    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
