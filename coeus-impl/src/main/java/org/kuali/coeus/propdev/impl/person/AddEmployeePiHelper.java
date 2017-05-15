package org.kuali.coeus.propdev.impl.person;


import org.apache.commons.lang3.StringUtils;
import org.kuali.coeus.common.framework.person.KcPerson;

public class AddEmployeePiHelper {

    private String personId;
    private KcPerson kcPerson;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public KcPerson getKcPerson() {
        if (StringUtils.isNotBlank(personId) && (kcPerson == null || !personId.equals(kcPerson.getPersonId()))) {
            kcPerson = KcPerson.fromPersonId(personId);
        }

        return kcPerson;
    }

    public void setKcPerson(KcPerson kcPerson) {
        this.kcPerson = kcPerson;
    }
}