/*
 * Copyright (c) 2001, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package nsk.jdi.StepRequest.addClassFilter_rt;

import nsk.share.*;
import nsk.share.jdi.*;
import nsk.share.jdi.ThreadState;

/**
 * This class is used as debuggee application for the filter_rt003 JDI test.
 */

public class filter_rt003a {

    //----------------------------------------------------- templete section

    static final int PASSED = 0;
    static final int FAILED = 2;
    static final int PASS_BASE = 95;

    static final long THREAD_STATE_TIMEOUT_MS = 30000;
    static final String STATE_INIT = "init";
    static final String STATE_THREAD_STARTED = "threadStarted";
    static final String STATE_JDI_INITED = "jdiInited";

    static ArgumentHandler argHandler;
    static Log log;

    //--------------------------------------------------   log procedures

    public static void log1(String message) {
        log.display("**> debuggee: " + message);
    }

    private static void logErr(String message) {
        log.complain("**> debuggee: " + message);
    }

    //====================================================== test program

    static Thread1filter_rt003a thread1filter_rt003a = new Thread1filter_rt003a(
            "thread1", new ThreadState(STATE_INIT, THREAD_STATE_TIMEOUT_MS));
    static Thread2filter_rt003a thread2filter_rt003a = new Thread2filter_rt003a(
            "thread2", new ThreadState(STATE_INIT, THREAD_STATE_TIMEOUT_MS));

    static Thread thread1 = JDIThreadFactory.newThread(thread1filter_rt003a);
    static Thread thread2 = JDIThreadFactory.newThread(thread2filter_rt003a);

    static filter_rt003aTestClass11 obj1 = new filter_rt003aTestClass11();
    static filter_rt003aTestClass21 obj2 = new filter_rt003aTestClass21();

    //------------------------------------------------------ common section

    static int exitCode = PASSED;

    static int instruction = 1;
    static int end         = 0;
                                   //    static int quit        = 0;
                                   //    static int continue    = 2;
    static int maxInstr    = 1;    // 2;

    static int lineForComm = 2;

    private static void methodForCommunication() {
        int i1 = instruction;
        int i2 = i1;
        int i3 = i2;
    }
    //----------------------------------------------------   main method

    public static void main (String argv[]) {

        argHandler = new ArgumentHandler(argv);
        log = argHandler.createDebugeeLog();

        thread1.start();
        thread2.start();
        thread1filter_rt003a.getThreadState().waitForState(STATE_THREAD_STARTED);
        thread2filter_rt003a.getThreadState().waitForState(STATE_THREAD_STARTED);

        log1("debuggee started!");

        for (int i = 0; ; i++) {

            log1("methodForCommunication();");
            methodForCommunication();
            if (instruction == end)
                break;

            if (instruction > maxInstr) {
                logErr("ERROR: unexpected instruction: " + instruction);
                exitCode = FAILED;
                break ;
            }

            switch (i) {

//------------------------------------------------------  section tested

                case 0:
                thread1filter_rt003a.getThreadState().setState(STATE_JDI_INITED);
                thread2filter_rt003a.getThreadState().setState(STATE_JDI_INITED);
                waitForThreadJoin ( thread1, "thread1" );
                waitForThreadJoin ( thread2, "thread2" );

//-------------------------------------------------    standard end section

                default:
                instruction = end;
                break;
            }
        }

        log1("debuggee exits");
        System.exit(exitCode + PASS_BASE);
    }

    static void waitForThreadJoin (Thread thread, String threadName) {
        log1("waiting for " + threadName + " join");

        // get internal timeout in minutes for waiting of thread completion.
        int waitTime = argHandler.getWaitTime();
        if (thread.isAlive()) {
            try {
                thread.join(waitTime * 60 * 1000);
            } catch (InterruptedException e) {
                throw new Failure("catched unexpected InterruptedException while waiting of " + threadName + " join:" + e);
            };
        }
        if (thread.isAlive()) {
            throw new Failure(threadName + " is still alive");
        } else {
            log1(threadName + " joined");
        }
    }

}

class filter_rt003aTestClass10{
    static void m10() {
        filter_rt003a.log1("entered: m10");
    }
}
class filter_rt003aTestClass11 extends filter_rt003aTestClass10{
    static void m11() {
        filter_rt003a.log1("entered: m11");
        filter_rt003aTestClass10.m10();
    }
}

class Thread1filter_rt003a extends NamedTask {

    private ThreadState threadState = null;

    public Thread1filter_rt003a(String threadName, ThreadState threadState) {
        super(threadName);
        this.threadState = threadState;
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public void run() {
        filter_rt003a.log1("  'run': enter  :: threadName == " + getName());
        threadState.setAndWait(filter_rt001a.STATE_THREAD_STARTED, filter_rt001a.STATE_JDI_INITED);
        filter_rt003aTestClass11.m11();
        filter_rt003a.log1("  'run': exit   :: threadName == " + getName());
        return;
    }
}

class filter_rt003aTestClass20{
    static void m20() {
        filter_rt003a.log1("entered: m20");
    }
}
class filter_rt003aTestClass21 extends filter_rt003aTestClass20{
    static void m21() {
        filter_rt003a.log1("entered: m21");
        filter_rt003aTestClass20.m20();
    }
}

class Thread2filter_rt003a extends NamedTask {

    private ThreadState threadState = null;

    public Thread2filter_rt003a(String threadName, ThreadState threadState) {
        super(threadName);
        this.threadState = threadState;
    }

    public ThreadState getThreadState() {
        return threadState;
    }

    public void run() {
        filter_rt003a.log1("  'run': enter  :: threadName == " + getName());
        threadState.setAndWait(filter_rt001a.STATE_THREAD_STARTED, filter_rt001a.STATE_JDI_INITED);
        filter_rt003aTestClass21.m21();
        filter_rt003a.log1("  'run': exit   :: threadName == " + getName());
        return;
    }
}
