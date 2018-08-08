/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
package org.kuali.kra.negotiations.service;

import static org.junit.Assert.*;
import static org.kuali.kra.negotiations.service.NegotiationServiceImpl.ZERO_DAYS_LONG_STRING;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kra.negotiations.bo.*;

public class NegotiationServiceImplTest {

	private NegotiationServiceImpl negotiationService;
	private NegotiationActivityType activityType1;
	private NegotiationLocation location1;
	private NegotiationLocation location2;
	
	@Before
	public void setup() {
		negotiationService = new NegotiationServiceImpl();
		
		activityType1 = new NegotiationActivityType();
		activityType1.setCode("1");
		activityType1.setDescription("Activity Type 1");
		
		location1 = new NegotiationLocation();
		location1.setCode("1");
		location1.setDescription("Location 1");
		
		location2 = new NegotiationLocation();
		location2.setCode("2");
		location2.setDescription("Location 2");
		
	}
	
	@Test
	public void testHistoryBeanGeneration_emptyList() {
		negotiationService.getNegotiationActivityHistoryLineBeans(new ArrayList<NegotiationActivity>());
	}
	
	@Test
	public void testHistoryBeanGeneration_oneActivity_noStartDate() {
		NegotiationActivity activity = new NegotiationActivity();
		activity.setLocation(location1);
		activity.setActivityType(activityType1);
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(1, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals("", historyBeans.get(0).getLocationDays());
	}
	
	@Test
	public void testHistoryBeanGeneration_oneActivity_noEndDate() {
		int numberOfDaysAgo = 10;
		NegotiationActivity activity = new NegotiationActivity();
		activity.setLocation(location1);
		activity.setActivityType(activityType1);
		activity.setStartDate(new Date(DateTime.now().minusDays(numberOfDaysAgo).getMillis()));
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(1, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(numberOfDaysAgo), historyBeans.get(0).getLocationDays());
	}
	
	@Test
	public void testHistoryBeanGeneration_oneActivity() {
		NegotiationActivity activity = new NegotiationActivity();
		activity.setLocation(location1);
		activity.setActivityType(activityType1);
		int numberOfDaysAgo = 10;
		int activityLength1 = 2;
		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity.setStartDate(new Date(startDate1.getMillis()));
		DateTime endDate1 = startDate1.plusDays(activityLength1);
		activity.setEndDate(new Date(endDate1.getMillis()));
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(1, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
	}
	
	@Test
	public void testHistoryBeanGeneration_twoActivities_sameLocation_contiguousDates() {
		int numberOfDaysAgo = 10;
		int activityLength1 = 2;
		int activityLength2 = 3;
		
		NegotiationActivity activity1 = new NegotiationActivity();
		activity1.setLocation(location1);
		activity1.setActivityType(activityType1);

		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity1.setStartDate(new Date(startDate1.getMillis()));
		DateTime endDate1 = startDate1.plusDays(activityLength1);
		activity1.setEndDate(new Date(endDate1.getMillis()));
		
		NegotiationActivity activity2 = new NegotiationActivity();
		activity2.setLocation(location1);
		activity2.setActivityType(activityType1);
		DateTime startDate2 = startDate1.plusDays(activityLength1+1);
		activity2.setStartDate(new Date(startDate2.getMillis()));
		DateTime endDate2 = startDate2.plusDays(activityLength2);
		activity2.setEndDate(new Date(endDate2.getMillis()));
		
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity1);
		activities.add(activity2);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(2, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
		assertEquals(location1.getDescription(), historyBeans.get(1).getLocation());
		assertEquals(String.valueOf(activityLength2), historyBeans.get(1).getLocationDays());
		DateTime effectiveStartDate = endDate1.plusDays(1);
		assertEquals(effectiveStartDate.toDate(), historyBeans.get(1).getEfectiveLocationStartDate());
	}
	
	@Test
	public void testHistoryBeanGeneration_twoActivities_sameLocation_noEndDates() {
		int numberOfDaysAgo = 10;
		
		NegotiationActivity activity1 = new NegotiationActivity();
		activity1.setLocation(location1);
		activity1.setActivityType(activityType1);

		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity1.setStartDate(new Date(startDate1.getMillis()));
		
		NegotiationActivity activity2 = new NegotiationActivity();
		activity2.setLocation(location1);
		activity2.setActivityType(activityType1);
		DateTime startDate2 = startDate1.plusDays(3);
		activity2.setStartDate(new Date(startDate2.getMillis()));
		
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity1);
		activities.add(activity2);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(2, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(numberOfDaysAgo), historyBeans.get(0).getLocationDays());
		assertEquals(location1.getDescription(), historyBeans.get(1).getLocation());
		assertEquals(ZERO_DAYS_LONG_STRING, historyBeans.get(1).getLocationDays());
	}

    @Test
    public void testNegotiationActivity() {
        NegotiationActivity activity = new NegotiationActivity();
        int numberOfDaysAgo = 10;
        DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
        DateTime endDate1 = null;

        Assert.assertTrue(StringUtils.equalsIgnoreCase(activity.getNumberOfDays(new Date(startDate1.getMillis()), null), "10"));

        numberOfDaysAgo = 5;
        startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
        endDate1 = null;
        Assert.assertTrue(StringUtils.equalsIgnoreCase(activity.getNumberOfDays(new Date(startDate1.getMillis()), null), numberOfDaysAgo + ""));

        numberOfDaysAgo = 5;
        startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
        endDate1 = DateTime.now().minusDays(1);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(activity.getNumberOfDays(new Date(startDate1.getMillis()),new Date(endDate1.getMillis())), "4"));

        startDate1 = DateTime.now().plusDays(5);
        endDate1 = DateTime.now().minusDays(1);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(activity.getNumberOfDays(new Date(startDate1.getMillis()), new Date(endDate1.getMillis())), "-6"));

        endDate1 = DateTime.now().plusDays(5);
        startDate1 = DateTime.now().minusDays(1);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(activity.getNumberOfDays(new Date(startDate1.getMillis()), new Date(endDate1.getMillis())), "6"));

    }


	@Test
	public void testGetNegotiationAge() {
		Negotiation negotiation = new Negotiation();
		long age=0L;

		negotiation.setNegotiationStartDate(new Date(2018,3,1));
		negotiation.setNegotiationEndDate(new Date(2018,3,31));
		age= negotiation.getNegotiationAge();
		assertEquals(age, 30L);
		negotiation.setNegotiationStartDate(new Date(2017,11,1));
		negotiation.setNegotiationEndDate(new Date(2017,11,30));
		age=negotiation.getNegotiationAge();
		assertEquals(age, 29L);
		negotiation.setNegotiationStartDate(new Date(2017,7,1));
		negotiation.setNegotiationEndDate(new Date(2017,7,31));
		age=negotiation.getNegotiationAge();
		assertEquals(age, 30L);


		negotiation.setNegotiationStartDate(new Date(2016,12,31));
		negotiation.setNegotiationEndDate(new Date(2017,12,31));
		age=negotiation.getNegotiationAge();
		assertEquals(age, 365L);
	}

	@Test
	public void testHistoryBeanGeneration_twoActivities_sameLocation_noFinalEndDate() {
		int numberOfDaysAgo = 10;
		int activityLength1 = 2;
		
		NegotiationActivity activity1 = new NegotiationActivity();
		activity1.setLocation(location1);
		activity1.setActivityType(activityType1);

		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity1.setStartDate(new Date(startDate1.getMillis()));
		DateTime endDate1 = startDate1.plusDays(activityLength1);
		activity1.setEndDate(new Date(endDate1.getMillis()));
		
		NegotiationActivity activity2 = new NegotiationActivity();
		activity2.setLocation(location1);
		activity2.setActivityType(activityType1);
		DateTime startDate2 = endDate1.plusDays(1);
		activity2.setStartDate(new Date(startDate2.getMillis()));
		
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity1);
		activities.add(activity2);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(2, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
		assertEquals(location1.getDescription(), historyBeans.get(1).getLocation());
		assertEquals(NegotiationActivity.getNumberOfDays(new Date(startDate2.getMillis()), new Date(DateTime.now().getMillis())), historyBeans.get(1).getLocationDays());
		DateTime effectiveStartDate = endDate1.plusDays(1);
		assertEquals(effectiveStartDate.toDate(), historyBeans.get(1).getEfectiveLocationStartDate());
	}
	
	@Test
	public void testHistoryBeanGeneration_twoActivities_sameLocation_overLappingDates() {
		int numberOfDaysAgo = 30;
		int activityLength1 = 15;
		int activityLength2 = 10;
		
		NegotiationActivity activity1 = new NegotiationActivity();
		activity1.setLocation(location1);
		activity1.setActivityType(activityType1);

		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity1.setStartDate(new Date(startDate1.getMillis()));
		DateTime endDate1 = startDate1.plusDays(activityLength1);
		activity1.setEndDate(new Date(endDate1.getMillis()));
		
		NegotiationActivity activity2 = new NegotiationActivity();
		activity2.setLocation(location1);
		activity2.setActivityType(activityType1);
		DateTime startDate2 = startDate1.plusDays(8);
		activity2.setStartDate(new Date(startDate2.getMillis()));
		DateTime endDate2 = startDate2.plusDays(activityLength2);
		activity2.setEndDate(new Date(endDate2.getMillis()));
		
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity1);
		activities.add(activity2);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(2, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
		assertEquals(location1.getDescription(), historyBeans.get(1).getLocation());
		assertEquals(NegotiationActivity.getNumberOfDays(new Date(endDate1.plusDays(1).getMillis()), new Date(endDate2.getMillis())), historyBeans.get(1).getLocationDays());
		DateTime effectiveStartDate = endDate1.plusDays(1);
		assertEquals(effectiveStartDate.toDate(), historyBeans.get(1).getEfectiveLocationStartDate());
	}

    @Test
    public void testHistoryBeanGeneration_twoActivities_sameLocation_overLappingDates_daylight_savings() {
        int numberOfDaysAgo = 20;
        int activityLength1 = 10;

        NegotiationActivity activity1 = new NegotiationActivity();
        activity1.setLocation(location1);
        activity1.setActivityType(activityType1);

        DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
        activity1.setStartDate(new Date(startDate1.getMillis()));
        DateTime endDate1 = startDate1.plusDays(activityLength1);
        activity1.setEndDate(new Date(endDate1.getMillis()));

        NegotiationActivity activity2 = new NegotiationActivity();
        activity2.setLocation(location1);
        activity2.setActivityType(activityType1);

        DateTime startDate2 = DateTime.now().minusDays(15);
        activity2.setStartDate(new Date(startDate2.getMillis()));
        DateTime endDate2 = DateTime.now().plusDays(30);
        activity2.setEndDate(new Date(endDate2.getMillis()));

        List<NegotiationActivity> activities = new ArrayList<>();
        activities.add(activity1);
        activities.add(activity2);

        List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);

        assertEquals(2, historyBeans.size());
        assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
        assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
        assertEquals(location1.getDescription(), historyBeans.get(1).getLocation());
        assertEquals("39", historyBeans.get(1).getLocationDays());
    }

	@Test
	public void testHistoryBeanGeneration_twoActivities_sameLocation_inclusiveDates() {
		int numberOfDaysAgo = 30;
		int activityLength1 = 15;
		int activityLength2 = 10;
		
		NegotiationActivity activity1 = new NegotiationActivity();
		activity1.setLocation(location1);
		activity1.setActivityType(activityType1);

		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity1.setStartDate(new Date(startDate1.getMillis()));
		DateTime endDate1 = startDate1.plusDays(activityLength1);
		activity1.setEndDate(new Date(endDate1.getMillis()));
		
		NegotiationActivity activity2 = new NegotiationActivity();
		activity2.setLocation(location1);
		activity2.setActivityType(activityType1);
		DateTime startDate2 = startDate1.plusDays(1);
		activity2.setStartDate(new Date(startDate2.getMillis()));
		DateTime endDate2 = startDate2.plusDays(activityLength2);
		activity2.setEndDate(new Date(endDate2.getMillis()));
		
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity1);
		activities.add(activity2);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(2, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
		assertEquals(location1.getDescription(), historyBeans.get(1).getLocation());
		assertEquals(ZERO_DAYS_LONG_STRING, historyBeans.get(1).getLocationDays());
		assertNull(historyBeans.get(1).getEfectiveLocationStartDate());
	}
	
	@Test
	public void testHistoryBeanGeneration_twoActivities_twoLocations() {
		int numberOfDaysAgo = 10;
		int activityLength1 = 2;
		int activityLength2 = 5;
		
		NegotiationActivity activity1 = new NegotiationActivity();
		activity1.setLocation(location1);
		activity1.setActivityType(activityType1);

		DateTime startDate1 = DateTime.now().minusDays(numberOfDaysAgo);
		activity1.setStartDate(new Date(startDate1.getMillis()));
		DateTime endDate1 = startDate1.plusDays(activityLength1);
		activity1.setEndDate(new Date(endDate1.getMillis()));
		
		NegotiationActivity activity2 = new NegotiationActivity();
		activity2.setLocation(location2);
		activity2.setActivityType(activityType1);
		DateTime startDate2 = endDate1.minusDays(1);
		activity2.setStartDate(new Date(startDate2.getMillis()));
		activity2.setEndDate(new Date(startDate2.plusDays(activityLength2).getMillis()));
		
		List<NegotiationActivity> activities = new ArrayList<>();
		activities.add(activity1);
		activities.add(activity2);
		
		List<NegotiationActivityHistoryLineBean> historyBeans = negotiationService.getNegotiationActivityHistoryLineBeans(activities);
		
		assertEquals(3, historyBeans.size());
		assertEquals(location1.getDescription(), historyBeans.get(0).getLocation());
		assertEquals(String.valueOf(activityLength1), historyBeans.get(0).getLocationDays());
		assertEquals(location2.getDescription(), historyBeans.get(2).getLocation());
		assertEquals(String.valueOf(activityLength2), historyBeans.get(2).getLocationDays());
		assertEquals(activity2.getStartDate(), historyBeans.get(2).getEfectiveLocationStartDate());
	}
}
