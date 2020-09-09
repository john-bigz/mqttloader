/*
 * Copyright 2020 Distributed Systems Group
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mqttloader.client;

import static mqttloader.Constants.PUB_CLIENT_ID_PREFIX;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import mqttloader.Loader;

public abstract class AbstractPublisher extends AbstractClient implements Runnable {
    protected final String topic;
    protected final int payloadSize;
    protected int numMessage;
    protected final int pubInterval;

    protected ScheduledExecutorService service;
    protected ScheduledFuture future;

    protected volatile boolean cancelled = false;

    public AbstractPublisher(int clientNumber, String topic, int payloadSize, int numMessage, int pubInterval) {
        super(PUB_CLIENT_ID_PREFIX + String.format("%05d", clientNumber));
        this.topic = topic;
        this.payloadSize = payloadSize;
        this.numMessage = numMessage;
        this.pubInterval = pubInterval;
    }

    public void start(long delay) {
        service = Executors.newSingleThreadScheduledExecutor();
        if(pubInterval==0){
            future = service.schedule(this, delay, TimeUnit.MILLISECONDS);
        }else{
            future = service.scheduleAtFixedRate(this, delay, pubInterval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void run() {
        if(pubInterval==0){
            continuousRun();
        }else{
            periodicalRun();
        }
    }

    private void continuousRun() {
        for(int i=0;i<numMessage;i++){
            if(cancelled) {
                Loader.logger.info("Publish task is cancelled: "+clientId);
                break;
            }
            if(isConnected()) {
                publish();
            } else {
                Loader.logger.warning("On sending publish, client was not connected: "+clientId);
            }
        }

        Loader.logger.info("Publisher finishes to send publish: "+clientId);
        Loader.countDownLatch.countDown();
    }

    private void periodicalRun() {
        if(numMessage > 0) {
            if(isConnected()) {
                publish();
            } else {
                Loader.logger.warning("On sending publish, client was not connected: "+clientId);
            }

            numMessage--;
            if(numMessage==0){
                Loader.logger.info("Publisher finishes to send publish: "+clientId);
                Loader.countDownLatch.countDown();
            }
        }
    }

    protected void recordSend(long currentTime) {
        StringBuilder sb = new StringBuilder();
        sb.append(currentTime);
        sb.append(",");
        sb.append(clientId);
        sb.append(",S,");
        Loader.queue.offer(new String(sb));

        Loader.logger.fine("Published a message (" + topic + "): "+clientId);
    }

    protected void terminateTasks() {
        if(!future.isDone()) {
            cancelled = true;
            future.cancel(false);
        }

        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract void publish();
    protected abstract boolean isConnected();
}