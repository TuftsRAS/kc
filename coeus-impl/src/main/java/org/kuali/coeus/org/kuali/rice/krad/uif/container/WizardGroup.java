/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.org.kuali.rice.krad.uif.container;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kuali.coeus.common.notification.impl.NotificationHelper;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.container.DialogGroup;
import org.kuali.rice.krad.uif.container.Group;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.LifecycleElement;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class WizardGroup extends DialogGroup {

    private static final Logger LOG = LogManager.getLogger(WizardGroup.class);

    @Override
    public void performApplyModel(Object model, LifecycleElement parent) {
        UifFormBase form = (UifFormBase) model;

        String stepStr = form.getActionParameters().get(this.getId()+".step");
        Integer step = 0;

        if (stepStr != null && stepStr.matches("\\d")) {
            step = Integer.valueOf(stepStr);
        } else try {
            // because the actionRequestParams are wiped out sometimes, we need to set this and retrieve it from the helper.
            final Object notificationHelper = PropertyUtils.getProperty(form, Constants.NOTIFICATION_HELPER);
            if (notificationHelper != null && ((NotificationHelper) notificationHelper).getNotificationStep() != null) {
                step = ((NotificationHelper) notificationHelper).getNotificationStep();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.info("Property notification helper was not found on the form." + e.getMessage(), e);
        }

        List<Component> currentItems = new ArrayList<>();
        for (int i = 0, len = getItems().size(); i < len; i++) {
            Component component = getItems().get(i);

            if (i == step) {
            	Component componentCopy = ComponentUtils.copy(component);
                if (componentCopy instanceof Group) {
                	Group group = (Group) componentCopy;
	                if (group.getFooter() != null) {
	                	setFooter(group.getFooter());
	                	group.setFooter(null);
	                }
                }
                currentItems.add(componentCopy);
            }
        }

        setItems(currentItems);

        super.performApplyModel(model, parent);
    }
}
