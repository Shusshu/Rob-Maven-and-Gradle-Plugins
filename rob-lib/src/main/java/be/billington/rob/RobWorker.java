package be.billington.rob;

public class RobWorker implements Runnable {

    private final Configuration conf;

    public RobWorker(Configuration conf){
        this.conf = conf;
    }

    @Override
    public void run() {
        robLogs();
    }

    public void robLogs(){
        conf.getLogger().info("Robbing...");

        try {
            Rob.logs(conf);

        } catch (Exception ex) {
            conf.getLogger().error("Error: " + ex.getMessage(), ex);
        }

        conf.getLogger().info("Robbed.");
    }
}
