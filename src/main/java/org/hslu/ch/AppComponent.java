/*
 * Copyright 2017-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hslu.ch;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.packet.IpAddress;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.intent.Key;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.HostId;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.host.HostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private IntentService intentService;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private CoreService coreService;
    
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private HostService hostService;
    
    private ApplicationId appId;
    
    private Key key;
    

    @Activate
    protected void activate() {
        log.info("Started");
        
        appId = coreService.registerApplication("org.hslu.testapp");
        key = Key.of("test-key", appId);
        
        
        HostId sourceId = hostService.getHostsByIp(IpAddress.valueOf("10.0.0.1")).iterator().next().id(); //get first host's hostid with the specified IP Address
        
        HostId destinationId = hostService.getHostsByIp(IpAddress.valueOf("10.0.0.2")).iterator().next().id(); //get first host's hostid with the specified IP Address
        
        TrafficSelector selector = DefaultTrafficSelector.emptySelector();
        TrafficTreatment treatment = DefaultTrafficTreatment.emptyTreatment();
        
        HostToHostIntent intent = HostToHostIntent.builder()
                .appId(appId)
                .key(key)
                .one(sourceId) // host 1
                .two(destinationId) // host 2
                .selector(selector) // to which criteria the intent is applied
                .treatment(treatment) // how matched packets are threated
                .build();
        intentService.submit(intent);
        log.info("Intent submitted");
    }

    @Deactivate
    protected void deactivate() {
        HostToHostIntent intent = (HostToHostIntent) intentService.getIntent(key);
        intentService.withdraw(intent);
        log.info("Stopped");
    }

}
