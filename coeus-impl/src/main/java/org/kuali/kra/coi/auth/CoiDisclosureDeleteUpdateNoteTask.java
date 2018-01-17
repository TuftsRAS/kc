/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.coi.auth;

import org.kuali.kra.coi.CoiDisclosure;
import org.kuali.kra.coi.notesandattachments.notes.CoiDisclosureNotepad;

public class CoiDisclosureDeleteUpdateNoteTask extends CoiDisclosureTask {

    private CoiDisclosureNotepad note;
    public CoiDisclosureDeleteUpdateNoteTask(String taskName, CoiDisclosure coiDisclosure, CoiDisclosureNotepad note) {
        super(taskName, coiDisclosure);
        this.note = note;
    }

    public CoiDisclosureNotepad getNote() {
        return note;
    }
    
}
