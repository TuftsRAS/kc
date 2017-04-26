/*
 * Kuali Coeus, a comprehensive research administration system for higher education.
 * 
 * Copyright 2005-2016 Kuali, Inc.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.coeus.org.kuali.rice.krad.uif.container;

import org.apache.commons.beanutils.PropertyUtils;
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
import java.util.logging.Logger;

public class WizardGroup extends DialogGroup {

    private static final Logger LOG = Logger.getLogger(WizardGroup.class.getName());

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
            LOG.info("Property notification helper was not found on the form." + e.getMessage());
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
