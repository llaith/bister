package com.siwasoftware.platform.rest.server.application;

import com.siwasoftware.platform.core.dto.broker.Lock;
import com.siwasoftware.platform.core.dto.broker.Task;
import com.siwasoftware.platform.core.dto.broker.TaskStatus;
import com.siwasoftware.platform.core.dto.broker.TaskSubmission;
import com.siwasoftware.platform.core.dto.broker.TaskUpdate;
import com.siwasoftware.platform.core.service.broker.TaskService;

import java.util.Date;
import java.util.List;

/**
 *
 */
public class ServiceContext {

    public static void clean1(final TaskService taskService, final String sessionId) {

        final List<Lock> locks = taskService.listQueueLocks(sessionId, new Date(1900-2000,0,1), 0, 100);

        for (final Lock lock : locks) {

            if (lock.getDeleteDate() == null) taskService.releaseQueueLock(sessionId, lock.getExternalId());

        }

    }

    public static void test1(final TaskService taskService, final String sessionId) {


        final String lockId = taskService.lockRequestedQueue(sessionId, "watermark1", "queue1");

        System.out.println("LockId: " + lockId);

        final String taskId = taskService.submitTask(sessionId, lockId, new TaskSubmission(
                new Date(),
                new Date(),
                "raise",
                "init",
                "{nodata={}}"));

        System.out.println("TaskId: " + taskId);

        final TaskStatus taskStatus = taskService.taskStatus(sessionId, taskId);

        System.out.println("TaskStatus: " + taskStatus);

        final Task task = taskService.nextTask(sessionId, lockId);

        System.out.println("Task: " + task);

        final TaskUpdate update = task.toUpdate();
        update.update("state2", "step2", "{stillnodata=1}");
        update.error("Pooped");
        update.sleep(100);

        final Task modTask = taskService.updateTask(sessionId, lockId, update);

        System.out.println("ModTask: " + modTask);

        taskService.deleteTask(sessionId, lockId, taskId, "closed");

        taskService.cleanQueue(sessionId, lockId, new Date());

    }

    public static void test2(final TaskService taskService, final String sessionId) {

        final String lockId1 = taskService.lockRequestedQueue(sessionId, "watermark1", "queue2_1");
        taskService.submitTask(sessionId, lockId1, new TaskSubmission(
                new Date(),
                new Date(),
                "raise",
                "init",
                "{nodata={}}"));
        System.out.println("Submitted with LockId1: " + lockId1);

        final String lockId2 = taskService.lockRequestedQueue(sessionId, "watermark1", "queue2_2");
        taskService.submitTask(sessionId, lockId2, new TaskSubmission(
                new Date(),
                new Date(),
                "raise",
                "init",
                "{nodata={}}"));
        System.out.println("Submitted with LockId2: " + lockId2);

        // some infos
        System.out.println("Queue/Queue2_2: " + taskService.getQueueInfo(sessionId, "watermark1", "queue2_2"));
        System.out.println("Queue/All: " + taskService.listQueueInfo(sessionId, "watermark1", 100));
        //System.out.println("Locks: " + taskService.listQueueLocks(sessionId, 0, 100));

        taskService.releaseQueueLock(sessionId, lockId1);
        taskService.releaseQueueLock(sessionId, lockId2);

        // relock
        final String lockIdNew1 = taskService.lockNextQueue(sessionId, "watermark1");
        final String lockIdNew2 = taskService.lockNextQueue(sessionId, "watermark1");

        System.out.println("LockIdNew1: " + lockIdNew1);
        System.out.println("LockIdNew2: " + lockIdNew2);

        System.out.println("TaskId1: " + lockIdNew1);
        System.out.println("TaskId2: " + lockIdNew2);

    }

}
