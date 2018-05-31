/*
 * Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */

package org.kuali.kra.web.krad.homepage;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.kuali.coeus.sys.framework.service.KcServiceLocator;
import org.kuali.kra.test.infrastructure.KcIntegrationTestBase;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

import static org.kuali.coeus.sys.framework.util.CollectionUtils.entry;

public class HomePageMenuItemServiceTest extends KcIntegrationTestBase {

    @Test
    public void verifyLinks() {
        final List<HomePageMenuItemServiceImpl.HomePageItemSuggestion> suggestions =  KcServiceLocator.getService(HomePageMenuItemService.class).getAllMenuItems();
        final Map<Integer, List<HomePageMenuItemServiceImpl.HomePageItemSuggestion>> failedSuggestions = suggestions.stream().map(suggestion -> {
            final HttpClient instance = HttpClientBuilder.create().build();
            try {
                final URI uri = URI.create(suggestion.getHref());
                final HttpResponse response = instance.execute(new HttpGet(uri));

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    final List<NameValuePair> pairs = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
                    //follow the real url in order to see if it is working not just the commonly used /kc-krad/landingPage?methodToCall=start&href="..."
                    final Optional<NameValuePair> href = pairs.stream().filter(pair -> "href".equals(pair.getName())).findFirst();
                    if (href.isPresent()) {
                        final HttpResponse hrefResponse = instance.execute(new HttpGet(href.get().getValue()));
                        return entry(hrefResponse.getStatusLine().getStatusCode(), Collections.singletonList(suggestion));
                    }
                }
                return entry(response.getStatusLine().getStatusCode(), Collections.singletonList(suggestion));
            } catch (IOException e) {
                throw new RuntimeException("failed to request link at " + suggestion);
            }
        }).filter(result -> result.getKey() != 200).collect(entriesToMapWithMergedList());

        assertTrue("contains failed links " + failedSuggestions, failedSuggestions.isEmpty());
    }

    private Collector<Map.Entry<Integer, List<HomePageMenuItemServiceImpl.HomePageItemSuggestion>>, ?, Map<Integer, List<HomePageMenuItemServiceImpl.HomePageItemSuggestion>>> entriesToMapWithMergedList() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (v1, v2) -> Stream.concat(v1.stream(), v2.stream())
                        .collect(Collectors.toList()), HashMap::new);
    }
}
