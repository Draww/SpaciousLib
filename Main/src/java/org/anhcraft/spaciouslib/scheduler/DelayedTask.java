package org.anhcraft.spaciouslib.scheduler;

public class DelayedTask extends TaskScheduler {
    private long delay;

    /**
     * Creates a delayed scheduler
     * @param runnable the runnable for executing
     * @param delay the delay time (in seconds) before execute
     */
    public DelayedTask(Runnable runnable, long delay) {
        super(runnable);
        this.delay = delay;
    }

    @Override
    public void run() {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay*1000);
                    if(!stopped){
                        runnable.run();
                        stopped = true;
                    }
                    thread.interrupt();
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        this.thread.start();
    }
}