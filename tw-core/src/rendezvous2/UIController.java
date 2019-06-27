package rendezvous2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

// producer on controlQueue
public class UIController implements Runnable {
//public class UIController implements Callable<String> {

	private BlockingQueue<String> controlQueue;
    
	public UIController(BlockingQueue<String> queue) {
	    this.controlQueue = queue;
 	}
   
    private void mimickUserSession() throws InterruptedException {
    	System.out.println(Thread.currentThread().getName() + " UIController start user session");
        controlQueue.put("start simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent start simulation");
        Thread.sleep(10);
        controlQueue.put("pause simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent pause simulation");
//        Thread.sleep(5);
//        controlQueue.put("resume simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent resume simulation");
        Thread.sleep(10);
//        controlQueue.put("reset simulation");
//    	System.out.println(Thread.currentThread().getName() + " UIController sent reset simulation");
//        Thread.sleep(15);
        controlQueue.put("stop simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent stop simulation");
        controlQueue.put("quit");
    	System.out.println(Thread.currentThread().getName() + " UIController sent quit");
        System.out.println(Thread.currentThread().getName() + " UIController end user session");
     }

//	@Override
//	public String call() throws Exception {
//	       try {
//	    	   mimickUserSession();
//	        } catch (InterruptedException e) {
//	            Thread.currentThread().interrupt();
//	        }
//		return "UIController finished";
//	}

	@Override
	public void run() {
	       try {
	    	   mimickUserSession();
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
		return;
	}

}
