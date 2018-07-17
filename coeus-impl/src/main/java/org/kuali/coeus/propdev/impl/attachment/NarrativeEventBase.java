/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.propdev.impl.attachment;

import org.kuali.coeus.propdev.impl.core.ProposalDevelopmentDocument;
import org.kuali.coeus.sys.framework.rule.KcDocumentEventBase;

import java.util.List;


/**
 * Base implementation for events triggered when a Key Person state is modified on a
 * <code>{@link ProposalDevelopmentDocument}</code>
 * 
 */
public abstract class NarrativeEventBase extends KcDocumentEventBase implements NarrativeEvent {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager
            .getLogger(NarrativeEventBase.class);

    private Narrative narrative;
    private List<Narrative> narratives;

    /**
     * 
     * Constructs a NarrativeEventBase
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     * @param narrative
     */
    protected NarrativeEventBase(String description, String errorPathPrefix, ProposalDevelopmentDocument document,
            Narrative narrative) {
        super(description, errorPathPrefix, document);
        if (narrative != null) {
            this.narrative = narrative;
        }
        narratives = document.getDevelopmentProposal().getNarratives();
        logEvent();
    }

    /**
     * 
     * Constructs a NarrativeEventBase.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    protected NarrativeEventBase(String description, String errorPathPrefix, ProposalDevelopmentDocument document) {
        super(description, errorPathPrefix, document);
        narratives = document.getDevelopmentProposal().getNarratives();
    }


    /**
     * @return <code>{@link Narrative}</code> that triggered this event.
     */
    @Override
    public Narrative getNarrative() {
        return narrative;
    }

    /**
     * @return <code>{@link Narrative}</code> that triggered this event.
     */
    public List<Narrative> getNarratives() {
        return narratives;
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    @Override
    protected void logEvent() {
        LOG.debug(getDescription());
    }
}
