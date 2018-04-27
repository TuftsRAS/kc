/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.coeus.sys.framework.controller.rest;

import org.junit.Test;
import org.kuali.coeus.sys.framework.model.KcPersistableBusinessObjectBase;
import org.kuali.rice.krad.data.CompoundKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SimpleCrudRestControllerTest {

    @Test
    public void test_primaryKeyToString_single_key() {
        assertEquals("value", new TestSimpleCrudRestController().primaryKeyToString("value"));
    }

    @Test
    public void test_primaryKeyToString_compound_key() {
        assertEquals("value1:2", new TestSimpleCrudRestController(){
            @Override
            public String getPrimaryKeyColumn() {
                return "key1:key2";
            }
        }.primaryKeyToString(new CompoundKey(new HashMap<String, Object>() {{
            put("key1", "value1");
            put("key2", 2);
        }})));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_primaryKeyToString_invalid_compound_key() {
        new TestSimpleCrudRestController(){
            @Override
            public String getPrimaryKeyColumn() {
                return "key1:key2:key3";
            }
        }.primaryKeyToString(new CompoundKey(new HashMap<String, Object>() {{
            put("key1", "value1");
            put("key2", 2);
        }}));
    }

    @Test
    public void test_toPlural_test_Bo_removal() {
        assertEquals("TeacupPigs", new TestSimpleCrudRestController().toPlural("TeacupPigBo"));
    }

    @Test
    public void test_toPlural_test_ies_rule() {
        assertEquals("Parties", new TestSimpleCrudRestController().toPlural("Party"));
        assertEquals("Sties", new TestSimpleCrudRestController().toPlural("Sty"));
        assertEquals("Trays", new TestSimpleCrudRestController().toPlural("Tray"));
        assertEquals("Preys", new TestSimpleCrudRestController().toPlural("Prey"));
        assertEquals("Couiys", new TestSimpleCrudRestController().toPlural("Couiy"));
        assertEquals("Toys", new TestSimpleCrudRestController().toPlural("Toy"));
        assertEquals("Guys", new TestSimpleCrudRestController().toPlural("Guy"));
    }

    @Test
    public void test_toPlural_test_es_rule() {
        assertEquals("Buses", new TestSimpleCrudRestController().toPlural("Bus"));
        assertEquals("Foxes", new TestSimpleCrudRestController().toPlural("Fox"));
        assertEquals("Buzzes", new TestSimpleCrudRestController().toPlural("Buzz"));
        assertEquals("Witches", new TestSimpleCrudRestController().toPlural("Witch"));
        assertEquals("Bushes", new TestSimpleCrudRestController().toPlural("Bush"));
    }

    @Test
    public void test_toPlural_test_normal_s__rule() {
        assertEquals("Balls", new TestSimpleCrudRestController().toPlural("Ball"));
    }

    static class TestSimpleCrudRestController extends SimpleCrudRestControllerBase<TestBo, Map<String, Object>> {

        @Override
        protected Object getPropertyValueFromDto(String propertyName, Map<String, Object> dataObject) {
            return null;
        }

        @Override
        protected Collection<Map<String, Object>> translateAllDataObjects(Collection<TestBo> dataObjects) {
            return null;
        }

        @Override
        protected List<String> getExposedProperties() {
            return null;
        }

        @Override
        protected Map<String, Object> convertDataObjectToDto(TestBo dataObject) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Map<String, Object> convertObjectToDto(Object o) {
            return (Map<String, Object>) o;
        }

        @Override
        protected List<String> getListOfTrackedProperties() {
            return null;
        }

        @Override
        protected TestBo convertDtoToDataObject(Map<String, Object> input) {
            return null;
        }

        @Override
        protected void updateDataObjectFromDto(TestBo existingDataObject, Map<String, Object> input) {

        }

        @Override
        protected void mergeDataObjectFromDto(TestBo existingDataObject, Map<String, Object> input) {

        }

    }

    static class TestBo extends KcPersistableBusinessObjectBase {

    }
}
