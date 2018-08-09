/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.negotiations.lookup;

import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.auth.UnitAuthorizationService;
import org.kuali.coeus.common.framework.sponsor.Sponsor;
import org.kuali.coeus.common.framework.unit.Unit;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.negotiations.bo.Negotiation;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.*;

/**
 * Negotiation Lookup Helper Service
 */
public class NegotiationLookupableHelperServiceImpl extends KraLookupableHelperServiceImpl {

    private static final long serialVersionUID = -5559605739121335896L;
    private static final String USER_ID = "userId";
    private static final String VIEW_ACTIVITIES = "view activities";
    
    private NegotiationDao negotiationDao;
    private UnitAuthorizationService unitAuthorizationService;
    

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocationDocFormKey(fieldValues);
        if (this.getParameters().containsKey(USER_ID)) {
            fieldValues.put("associatedNegotiable.piId", ((String[]) this.getParameters().get(USER_ID))[0]);
            fieldValues.put("negotiatorPersonId", ((String[]) this.getParameters().get(USER_ID))[0]);
        }
        List<Negotiation> searchResults = new ArrayList<Negotiation>();
        searchResults.addAll(getNegotiationDao().getNegotiationResults(fieldValues));

        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        htmlDataList.add(getOpenLink(((Negotiation) businessObject).getDocument()));
        if (unitAuthorizationService.hasPermission(getUserIdentifier(), Constants.MODULE_NAMESPACE_NEGOTIATION, PermissionConstants.NEGOTIATION_VIEW_ACTIVITIES_LOOKUP)) {
        	htmlDataList.add(getViewActivitiesLink((Negotiation) businessObject));
        } 
        htmlDataList.add(getMedusaLink(((Negotiation) businessObject).getDocument().getDocumentNumber(), false));
        return htmlDataList;
    }
    
    protected AnchorHtmlData getOpenLink(Document document) {
        AnchorHtmlData htmlData = new AnchorHtmlData();
        htmlData.setDisplayText("open");
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.DOC_HANDLER_METHOD);
        parameters.put(KRADConstants.PARAMETER_COMMAND, KewApiConstants.DOCSEARCH_COMMAND);
        parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, getDocumentTypeName());
        parameters.put("docId", document.getDocumentNumber());
        String href  = UrlFactory.parameterizeUrl("../" + getHtmlAction(), parameters);
        
        htmlData.setHref(href);
        return htmlData;

    }
    
    protected AnchorHtmlData getViewActivitiesLink(Negotiation negotiation) {
    	AnchorHtmlData htmlData = new AnchorHtmlData();
        htmlData.setDisplayText(VIEW_ACTIVITIES);
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, Constants.MAPPING_VIEW_ACTIVITIES);        
        parameters.put(Constants.VIEW_NEGOTIATION_REQUEST_PARAM, negotiation.getNegotiationId().toString());
        String href  = UrlFactory.parameterizeUrl("../" + getHtmlAction(), parameters);
        
        htmlData.setHref(href);
        htmlData.setId(Constants.NEGOTIATION_ID_VIEW_LIGHTBOX_ACTIVITIES);
        htmlData.setAnchorClass(Constants.VIEW_IFRAME);
        return htmlData;

    }

    
    @Override
    protected String getDocumentTypeName() {
        return "NegotiationDocument";
    }
    
    @Override
    protected String getKeyFieldName() {
        return "negotiationId";
    } 

    @Override
    protected String getHtmlAction() {
        return "negotiationNegotiation.do";
    }



    public NegotiationDao getNegotiationDao() {
        return negotiationDao;
    }



    public void setNegotiationDao(NegotiationDao negotiationDao) {
        this.negotiationDao = negotiationDao;
    }
    
    /**
     * Call's the super class's performLookup function and edits the URLs for the unit name, unit number, sponsor name, and pi name.
     * @see org.kuali.kra.lookup.KraLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        final String leadUnitName = "associatedNegotiable.leadUnitName";
        final String leadUnitNumber = "associatedNegotiable.leadUnitNumber";
        final String sponsorName = "associatedNegotiable.sponsorName";
        final String piName = "associatedNegotiable.piName";
                
        Collection lookupStuff = super.performLookup(lookupForm, resultTable, bounded);
        Iterator i = resultTable.iterator();
        while (i.hasNext()) {
            ResultRow row = (ResultRow) i.next();
            for (Column column : row.getColumns()) {
                //the unit name, pi Name and sponsor name don't need to generate links.
                if (StringUtils.equalsIgnoreCase(column.getPropertyName(), leadUnitName) 
                        || StringUtils.equalsIgnoreCase(column.getPropertyName(), sponsorName)
                        || StringUtils.equalsIgnoreCase(column.getPropertyName(), piName)) {
                    column.setPropertyURL("");
                    for (AnchorHtmlData data : column.getColumnAnchors()) {
                        if (data != null) {
                            data.setHref("");
                        }
                        
                    }
                }
                if (StringUtils.equalsIgnoreCase(column.getPropertyName(), leadUnitNumber)){
                    String unitNumber = column.getPropertyValue();
                    String newUrl = "inquiry.do?businessObjectClassName="+ Unit.class.getName()+"&unitNumber=" + unitNumber + "&methodToCall=start";
                    column.setPropertyURL(newUrl);
                    for (AnchorHtmlData data : column.getColumnAnchors()) {
                        if (data != null) {
                            data.setHref(newUrl);
                        }
                        
                    }
                }
            }
        }
        return lookupStuff;
    }

    @Override
    protected void setRows() {
        super.setRows();
        List<String> lookupFieldAttributeList = null;
        if (getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(
                    getBusinessObjectClass());
        }
        for (Row row : getRows()) {
            for (Field field : row.getFields()) {
                if (StringUtils.equalsIgnoreCase(field.getPropertyName(), "associatedNegotiable.sponsorCode")) {
                    field.setQuickFinderClassNameImpl(Sponsor.class.getName());
                    field.setFieldConversions("sponsorCode:associatedNegotiable.sponsorCode");
                    field.setLookupParameters("");
                    field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
                    field.setImageSrc(null);
                    field.setInquiryParameters("associatedNegotiable.sponsorCode:sponsorCode");
                    field.setFieldDirectInquiryEnabled(true);
                } else if (StringUtils.equalsIgnoreCase(field.getPropertyName(), "associatedNegotiable.leadUnitNumber")) {
                    field.setQuickFinderClassNameImpl(Unit.class.getName());
                    field.setFieldConversions("unitNumber:associatedNegotiable.leadUnitNumber");
                    field.setLookupParameters("");
                    field.setBaseLookupUrl(LookupUtils.getBaseLookupUrl(false));
                    field.setImageSrc(null);
                    field.setInquiryParameters("associatedNegotiable.leadUnitNumber:unitNumber");
                    field.setFieldDirectInquiryEnabled(true);
                }
            }
        }

    }

    /**
     * Set the Unit Authorization Service.  Injected by the Spring Framework.
     * @param unitAuthorizationService the Unit Authorization Service
     */
    public final void setUnitAuthorizationService(UnitAuthorizationService unitAuthorizationService) {
        this.unitAuthorizationService = unitAuthorizationService;
    }
    
    protected String getUserIdentifier() {
        return GlobalVariables.getUserSession().getPrincipalId();
    }
    
}
