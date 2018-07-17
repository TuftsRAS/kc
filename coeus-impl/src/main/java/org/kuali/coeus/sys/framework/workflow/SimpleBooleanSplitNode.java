/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.workflow;

import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.engine.node.SplitNode;
import org.kuali.rice.kew.engine.node.SplitResult;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

import java.util.ArrayList;
import java.util.List;

/**
 * This code was taken directly from KFS SimpleBooleanSplitNode
 *  @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class SimpleBooleanSplitNode implements SplitNode {

    private static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(SimpleBooleanSplitNode.class);

    /**
     * This method will look up the document being routed, if it is an instance of ResearchDocumentBase
     * it will call answerSplitNodeQuestion on it passing the name of the route node.  The default implementation (currently)
     * throws an UnsupportedOperationException for any input. If one wishes to support the SplitNode for a given document, the
     * method should be overridden and return boolean T/F based on which of the branches ( always names "True" and "False" ) 
     * KEW should route to based upon the name of the split node.
     * 
     * @see org.kuali.rice.kew.engine.node.SimpleNode#process(org.kuali.rice.kew.engine.RouteContext, org.kuali.rice.kew.engine.RouteHelper)
     */
    
    @Override
    public SplitResult process(RouteContext context, RouteHelper helper ) throws Exception {
        String documentID = context.getDocument().getDocumentId();
        String routeNodeName = context.getNodeInstance().getRouteNode().getRouteNodeName();
        if( LOG.isDebugEnabled() )
            LOG.debug(String.format("Entering routeNode:%s for documentId:%s",routeNodeName,documentID ));
        Document document = 
            KcServiceLocator.getService(DocumentService.class).getByDocumentHeaderIdSessionless(documentID);
        
        if( document instanceof SimpleBooleanSplitNodeAware) {
            boolean ret = ((SimpleBooleanSplitNodeAware)document).answerSplitNodeQuestion( routeNodeName );
            if( LOG.isDebugEnabled() )
                LOG.debug( String.format("answerSplitNodeQuestion returned:%s",ret) );
            return booleanToSplitResult( ret ); 
        }             
        throw new UnsupportedOperationException( "Document was not instance of:" + document.getClass().getName() + ", not supported by SimpleBooleanSplitNode." );
    }
    
    
    /**
     * Converts a boolean value to SplitResult where the branch name is "True" or "False" based on the value of the given boolean
     * @param b a boolean to convert to a SplitResult
     * @return the converted SplitResult
     */
    protected SplitResult booleanToSplitResult(boolean b) {
        List<String> branches = new ArrayList<String>();
        final String branchName = b ? "True" : "False";
        branches.add(branchName);
        return new SplitResult(branches);
    }


}
