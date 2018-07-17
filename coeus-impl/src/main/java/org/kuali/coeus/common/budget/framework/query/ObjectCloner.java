/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.common.budget.framework.query;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;

/**
 * The class is used to Deep Clone an Object
 * 
 * @version 1.0
 */

public class ObjectCloner {
    private static final Logger LOG = LogManager.getLogger(ObjectCloner.class);

    // so that nobody can accidentally create an ObjectCloner object
    private ObjectCloner() {
    }

    // returns a deep copy of an object
    static public Object deepCopy(Object oldObj) {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj); // C
            oos.flush(); // D
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin); // F
            // return the new object
            Object object = ois.readObject();
            oos.close();
            ois.close();
            return object; // G
        }
        catch (Exception e) {
            return null;
        }
        finally {
            try {
                if (oos != null)
                    oos.close();
                if (ois != null)
                    ois.close();
            }catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
