package org.kuali.coeus.common.api.document;

import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "DOCUMENT_WORKLOAD_DETAILS")
public class DocumentWorkloadDetails extends KcPersistableBusinessObjectBase {

    @PortableSequenceGenerator(name = "SEQ_DOCUMENT_WORKLOAD_DET_ID")
    @GeneratedValue(generator = "SEQ_DOCUMENT_WORKLOAD_DET_ID")
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "LAST_ACTION_TIME")
    private Timestamp lastActionTime;

    @Column(name = "CURRENT_PPL_FLW_STOP")
    private Integer currentPeopleFlowStop;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    public DocumentWorkloadDetails() {
    }

    public DocumentWorkloadDetails(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public DocumentWorkloadDetails(String documentNumber, Timestamp lastActionTime, Integer currentPeopleFlowStop) {
        this.documentNumber = documentNumber;
        this.lastActionTime = lastActionTime;
        this.currentPeopleFlowStop = currentPeopleFlowStop;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(Timestamp lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public Integer getCurrentPeopleFlowStop() {
        return currentPeopleFlowStop;
    }

    public void setCurrentPeopleFlowStop(Integer currentPeopleFlowStop) {
        this.currentPeopleFlowStop = currentPeopleFlowStop;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
}
