/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.protocol.noteattachment;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

import java.util.List;

public class ProtocolNotepadServiceImpl implements ProtocolNotepadService {

    private PersonService personService;
    
    protected final Logger LOG = LogManager.getLogger(getClass());
    private static final String PERSON_NOT_FOUND_FORMAT_STRING = "%s (not found)";
   
    @Override
    public void setProtocolNotepadUpdateUsersName(List<ProtocolNotepadBase> protocolNotepads) {
        for (ProtocolNotepadBase pnp : protocolNotepads) {
            if (LOG.isDebugEnabled()) { 
                LOG.debug(String.format("Looking up person for update user %s.", pnp.getUpdateUser()));
            }
            Person person = personService.getPersonByPrincipalName(pnp.getUpdateUser());
            pnp.setUpdateUserFullName(person==null?String.format(PERSON_NOT_FOUND_FORMAT_STRING, pnp.getUpdateUser()):person.getName());
            
            if (StringUtils.isNotBlank(pnp.getCreateUser())) {
                Person creator = personService.getPersonByPrincipalName(pnp.getCreateUser());
                pnp.setCreateUserFullName(creator==null?String.format(PERSON_NOT_FOUND_FORMAT_STRING, pnp.getCreateUser()):creator.getName());
            } else {
                pnp.setCreateUserFullName("");
            }
        }
    }

    /**
     * Gets the personService attribute. 
     * @return Returns the personService.
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Sets the personService attribute value.
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

}
