package com.twitter.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static com.gmail.merikbest2015.constants.FeignConstants.USER_SERVICE;
import static com.gmail.merikbest2015.constants.PathConstants.API_V1_AUTH;

@Configuration
public class ClientConfig {
//    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    public int maxProfitAssignment(int[] difficulty, int[] profit, int[] worker) {
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        int sum = 0;


        for (int i = 0; i <difficulty.length; i++) {
            int finalI = i;
            treeMap.compute(difficulty[i], (key, value) -> {
                if (value == null) {
                    return profit[finalI];
                } else if (value < profit[finalI]) {
                    return profit[finalI];
                }
                return value;
            });
        }
        // diff: pro = {1, 20}, {2, 18}, {3, 21}
        System.out.println("Build a treemap that difficulty will be the key and the profit is the value:" + treeMap);

        int maxCurr = 0;
        for (Map.Entry<Integer, Integer> entry : treeMap.entrySet()) {
            if (entry.getValue() > maxCurr) {
                maxCurr = entry.getValue();
            } else {
                entry.setValue(maxCurr);
            }
        }
        // diff: pro = {1, 20}, {2, 20}, {3, 21}
        // diff: pro = {3, 21}, {2, 20}, {1, 20}
        System.out.println("Aggregate treemap ready for greedy algorithm" + treeMap);
        Set<Integer> keySet = treeMap.descendingKeySet();

        int index = 0;
        Iterator<Integer> curKeyIte = keySet.iterator();
        Integer curKey = curKeyIte.next();
        int[] desWorker = Arrays.stream(worker).boxed().sorted(Collections.reverseOrder()).mapToInt(Integer::intValue).toArray();
        while(index < worker.length) {
            if(desWorker[index] > curKey) {
                sum += treeMap.get(curKey);;
                index++;
            } else {
                if (curKeyIte.hasNext()) {
                    curKey = curKeyIte.next();
                } else {
                    return sum;
                }
            }
        }

        return sum;
    }
}
