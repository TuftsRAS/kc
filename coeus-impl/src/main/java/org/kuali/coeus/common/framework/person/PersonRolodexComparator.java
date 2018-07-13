/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.person;

import java.util.Comparator;

import org.kuali.coeus.common.framework.rolodex.PersonRolodex;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PersonRolodexComparator implements Comparator<PersonRolodex> {
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(PersonRolodexComparator.class);

    public static final Comparator<PersonRolodex> INSTANCE = new PersonRolodexComparator();


    /**
     * compare one <code>{@link PersonRolodex}</code> instance to another. Sort by the role of the
     *  <code>{@link PersonRolodex}</code>
     */
    @Override
    public int compare(PersonRolodex person1, PersonRolodex person2) {
        int retval = 0;
               
        if (person1.isInvestigator() || person2.isInvestigator()) {
            if (person1.isPrincipalInvestigator() 
                    || person2.isPrincipalInvestigator()) {
               if (person1.isPrincipalInvestigator()) {
                   retval--;
               }
               
               if (person2.isPrincipalInvestigator()) {
                   retval++;
               }
            }

            if (retval == 0) {
                if (person1.isMultiplePi()
                        || person2.isMultiplePi()) {
                    if (person1.isMultiplePi()) {
                        retval--;
                    }

                    if (person2.isMultiplePi()) {
                        retval++;
                    }
                }
            }
        }

        if (retval == 0) {
            retval = massageOrdinalNumber(person1).compareTo(massageOrdinalNumber(person2));
        }
        
        if (retval == 0) {
            if (isNotBlank(person1.getFullName()) && isNotBlank(person1.getLastName()) && isNotBlank(person2.getLastName())) {
                retval = person1.getLastName().compareTo(person2.getLastName());
            }
            else if (isNotBlank(person2.getLastName())) {
                retval--; 
            }
        }
        
        if (LOG.isDebugEnabled()) {
        	LOG.debug("retval = " + retval);
        }

        return retval;
    }

    private Integer massageOrdinalNumber(PersonRolodex person) {
        return person.getOrdinalPosition() != null ? person.getOrdinalPosition() : -1;
    }
    
}
