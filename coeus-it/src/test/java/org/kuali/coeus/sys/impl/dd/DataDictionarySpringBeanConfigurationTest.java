/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.impl.dd;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DataDictionarySpringBeanConfigurationTest extends KcIntegrationTestBase {

    private static final Logger LOG = LogManager.getLogger(DataDictionarySpringBeanConfigurationTest.class);

    private static final Collection<String> IGNORE_PATTERN = Stream.<String>empty().collect(Collectors.toList());

    /**
     * This test method makes sure all Data Dictionary spring beans can be retrieved.
     */
    @Test
    public void test_all_spring_bean_retrieval() {
        //noinspection ResultOfMethodCallIgnored
        toEachSpringBean((defaultListableBeanFactory, name) -> defaultListableBeanFactory.getBean(name).toString(), false);
    }

    /**
     * This test method makes sure all Data Dictionary spring beans named with the -parentBean convention are also abstract.
     * These beans should be abstract so that they are never instantiated directly.
     */
    @Test
    public void test_all_parentBean_abstract() {

        final Set<String> nonAbstractParents = new HashSet<>();

        toEachSpringBean(((context, name) -> {
            if (name.contains("-parentBean")) {
                BeanDefinition definition = context.getBeanDefinition(name);
                if (!definition.isAbstract() && !isSpringUtilType(definition)) {
                    nonAbstractParents.add(name);
                }
            }
        }), true);

        Assert.assertTrue("The DataDictionary contains parent beans that aren't abstract: " + nonAbstractParents, nonAbstractParents.isEmpty());

    }

    /**
     * Spring beans created like: <util:map cannot be marked as abstract.
     * @return true if collection type
     */
    private boolean isSpringUtilType(BeanDefinition definition) {
        final Class<?> beanClass;
        try {
            beanClass = Class.forName(definition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return MapFactoryBean.class.equals(beanClass) || ListFactoryBean.class.equals(beanClass) || PropertiesFactoryBean.class.equals(beanClass) || SetFactoryBean.class.equals(beanClass);
    }

    /**
     * Apply a void function to each spring bean available in each spring context available from each spring resource loader.
     *
     * @param function the function to apply
     * @param ignoreCreationException whether to ignore exception that occurs when creating a bean
     */
    @SuppressWarnings("deprecation")
    private void toEachSpringBean(VoidFunction function, boolean ignoreCreationException) {
        final List<KeyValue<String, Exception>> failedBeans = new ArrayList<>();

        final DefaultListableBeanFactory context = getContext(KcServiceLocator.getService(DataDictionaryService.class).getDataDictionary());
        for (String name : context.getBeanDefinitionNames()) {
            if (process(name)) {
                try {
                    function.r(context, name);
                } catch (BeanIsAbstractException e) {
                    //ignore since the bean can't be created
                } catch (BeanCreationException e) {
                    //if there is no way to ignore creation errors all tests will fail even if one bean is bad regardless of the type
                    //we do want this type of failure to be tested by at least one test method but not all tests
                    if (!ignoreCreationException) {
                        LOG.error("unable to create bean " + name, e);
                        throw e;
                    }
                } catch (RuntimeException e) {
                    LOG.error("failed to execute function for bean " + name, e);
                    failedBeans.add(new DefaultKeyValue<>(name, e));
                }
            }
        }

        Assert.assertTrue("the following beans failed to retrieve " + failedBeans, failedBeans.isEmpty());
    }

    private boolean process(String name) {
        return IGNORE_PATTERN.stream().noneMatch(name::matches);
    }

    private DefaultListableBeanFactory getContext(DataDictionary dataDictionary) {
        try {
            final Field ddBeansField = DataDictionary.class.getDeclaredField("ddBeans");
            ddBeansField.setAccessible(true);
            return ((DefaultListableBeanFactory) ddBeansField.get(dataDictionary));
        } catch (NoSuchFieldException|IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    private interface VoidFunction {
        void r(DefaultListableBeanFactory context, String name);
    }
}
