package org.kuali.coeus.common.api.document;

import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.*;

@Entity
@Table(name = "DOCUMENT_WORKFLOW_USER_DETAILS")
public class DocumentWorkflowUserDetails extends KcPersistableBusinessObjectBase {

    @PortableSequenceGenerator(name = "SEQ_WORKFLOW_USER_DET_ID")
    @GeneratedValue(generator = "SEQ_WORKFLOW_USER_DET_ID")
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "PRINCIPAL_ID")
    private String principalId;

    @Column(name = "DOCUMENT_NUMBER")
    private String documentNumber;

    @Column(name = "STEPS")
    private int steps;


    public DocumentWorkflowUserDetails() {
    }

    public DocumentWorkflowUserDetails(String principalId, Integer steps, String documentNumber) {
        this.principalId = principalId;
        this.steps = steps;
        this.documentNumber = documentNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

}
