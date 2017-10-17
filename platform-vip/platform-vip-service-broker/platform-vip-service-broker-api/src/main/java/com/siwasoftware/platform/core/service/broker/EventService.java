package com.siwasoftware.platform.core.service.broker;

import com.siwasoftware.platform.core.dto.broker.Event;
import com.siwasoftware.platform.core.dto.broker.EventSubmission;

import java.util.List;

/**
 * H2 For OLTP
 * MapDB for the NoSQL audit + config db. !!!
 *
 * TODO:
 *      Async spike-proof wrapper in client lib!.
 *
 * Standard Client behaviour is to increase by one thread (up to worker pool) each time a lock is made, and reduce it
 * each time a lock is not made. In order to not spike the db with multiple clients, the scaling is performed only if
 * a random number is found, perhaps 25%. For this reason, and perhaps with a random additional delay, in this way
 * we can avoid having to put another (hard to debug) async queue inside the library (although there is no reason to
 * not add one such queued version around the core version at a later date). Note that this is a valid option because
 * we control the bridge code.
 */
    /*
    -- build the sql. Finds the id from each queue by creation date, then orders those by error_count and then
    -- last updated. This will keep the order per queue but also rotate the queue each run allowing a top1 to
    -- not get stuck. The idea comes from here (by Tom H, part 2):
    -- http://stackoverflow.com/questions/723054/extra-fields-with-sql-min-group-by
    -- DANGER, WILL ROBINSON: Be careful putting criteria on the outer join as it will actually exclude the
    -- result from the match and affect the finding of the 'top' of each queue. Criteria on the where will
    -- work as expected. Think of it like this, the first criteria (on the join) are deciding what is a valid
    -- match for the minimum of the queue of the current, and the second is for the current itself. So for
    -- example we need date deleted twice - once because we don't consider an older record that as been
    -- deleted a valid 'eralier' record for the current, and again because we don't want current records that
    -- are already deleted.


    TODO:
    Jetty for HTTP

Because you can’t be a web application without HTTP, Dropwizard uses the Jetty HTTP library to embed an incredibly tuned HTTP server directly into your project. Instead of handing your application off to a complicated application server, Dropwizard projects have a main method which spins up an HTTP server. Running your application as a simple process eliminates a number of unsavory aspects of Java in production (no PermGen issues, no application server configuration and maintenance, no arcane deployment tools, no class loader troubles, no hidden application logs, no trying to tune a single garbage collector to work with multiple application workloads) and allows you to use all of the existing Unix process management tools instead.
Jersey for REST

For building RESTful web applications, we’ve found nothing beats Jersey (the JAX-RS reference implementation) in terms of features or performance. It allows you to write clean, testable classes which gracefully map HTTP requests to simple Java objects. It supports streaming output, matrix URI parameters, conditional GET requests, and much, much more.
Jackson for JSON

In terms of data formats, JSON has become the web’s lingua franca, and Jackson is the king of JSON on the JVM. In addition to being lightning fast, it has a sophisticated object mapper, allowing you to export your domain models directly.
Metrics for metrics

Note: it's all strict. If you pass a null, it's an error, there is too much of a chance of error in scripting
to let that slide!

Also, we don't derive watermark, source, etc from lock, that could make for a disaster. In fact, it's designed as a
check and balance!

NOTE: to avoid locks becoming deadlocked.
a) always request all locks at the start of a stage.
b) any failed lock requires all to be immediately released and for the task to return
c) eventually, the ttl will kill the lock. If your lock gets killed, immediately cleanup and release all other locks.
d) never release partial locks (parts of a group for example).

Could do this either way, with LoginRequest returning a LoginSession which is used to get an EventLock etc.
But i'm going to go with simple id's for these reasons. 1/ an experiement in minimalism. 2/ to avoid checking they
match (is that a useful? check and balance...) and 3/ To make it simpler to port to rest-apis and perhaps other non
types access methods.

     */
public interface EventService {

    Integer lockEventSource(String sessionUid, String source);

    void releaseEventLock(String lockUid);

    Event submitEvent(String lockUid, EventSubmission init); // returns a handle for tracking, not the task because that would bypass the locking.

    List<Event> getPendingEvents(String lockUid); // automatically applies a max. To find if more, call again from the last id returned. No results = finished. Max is in the config, default to 100.

    List<Event> getPendingEvents(String lockUid, String fromUid, int max); // To find if more, call again from the last id returned. No results = finished. Max is in the config, default to 100.

    List<Event> getPendingEvents(String lockUid, String type);

    List<Event> getPendingEvents(String lockUid, String type, String fromUid, int max);

    void deleteEvent(String lockUid, Event info); // POST

    void clearEventsAfter(String lockUid, String fromUid);

    void clearEventsBefore(String lockUid, String toUid);

    void clearEventsBetween(String lockUid, String fromUid, String toUid);

    void clearEventSource(String lockUid);

}
