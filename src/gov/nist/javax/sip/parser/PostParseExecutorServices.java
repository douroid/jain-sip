package gov.nist.javax.sip.parser;

import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.stack.BlockingQueueDispatchAuditor;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PostParseExecutorServices {

    private static List<ExecutorService> postParseExecutors = null;

    public static class NamedThreadFactory implements ThreadFactory {

        static long threadNumber = 0;

        public Thread newThread(Runnable arg0) {
            Thread thread = new Thread(arg0);
            thread.setName("SIP-TCP-Core-PipelineThreadpool-" + threadNumber++ % 999999999);
            return thread;
        }

    }

    public static BlockingQueueDispatchAuditor staticQueueAuditor;
    public static void setPostParseExcutorSize(SipStackImpl sipStack, int threads, int queueTimeout){
    	if(postParseExecutor != null) {
    		postParseExecutor.shutdownNow();
    	}
    	if(staticQueueAuditor != null) {
    		try {
    			staticQueueAuditor.stop();
    		} catch (Exception e) {

    		}
    	}
    	if(threads<=0) {
    		postParseExecutor = null;
    	} else {
    		staticQueue = new LinkedBlockingQueue<Runnable>();
    		postParseExecutor = new ThreadPoolExecutor(threads, threads,
    				0, TimeUnit.SECONDS, staticQueue,
    				new NamedThreadFactory());
    		// Contribution for https://github.com/Mobicents/jain-sip/issues/39
    		if(sipStack.getStackCongestionControlTimeout() > 0) {
	    		staticQueueAuditor = new BlockingQueueDispatchAuditor(staticQueue);
	    		staticQueueAuditor.setTimeout(queueTimeout);
	    		staticQueueAuditor.start(2000);
    		}
    	}

    }

    public static ExecutorService getPostParseExecutor() {
        if (postParseExecutors != null) {
            return getAffinityPostParseExecutor(new Object());
        } else {
            return null;
        }
    }

    public static ExecutorService getAffinityPostParseExecutor(Object hash) {
        if (postParseExecutors != null) {
            int hashCode = Math.abs(hash.hashCode());
            int assignedThread = hashCode % postParseExecutors.size();
            return postParseExecutors.get(assignedThread);
        } else {
            return null;
        }
    }

    public static void shutdownThreadpool() {
        if (postParseExecutors != null) {
            for (ExecutorService execServ : postParseExecutors) {

                execServ.shutdown();
            }
            postParseExecutors = null;
        }
        if (staticQueueAuditor != null) {
            try {
                staticQueueAuditor.stop();
            } catch (Exception e) {

            }
        }
    }
}
