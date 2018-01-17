/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.framework.person;

public enum PersonTypeConstants {
    EMPLOYEE ("E", "Employee"),
    NONEMPLOYEE ("N", "Non Employee"),
    TBN("T", "To Be Named");

    private final String code;   
    private final String description; 
    
    PersonTypeConstants(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode()   
    { 
        return code; 
    }

    public String getDescription() 
    { 
        return description; 
    }

}

